package br.edu.fatecpg.reviu.services;

import br.edu.fatecpg.reviu.domain.card.Card;
import br.edu.fatecpg.reviu.domain.deck.Deck;
import br.edu.fatecpg.reviu.dto.requests.CardRequestDTO;
import br.edu.fatecpg.reviu.integration.DictionaryAPIService;
import br.edu.fatecpg.reviu.repositories.CardRepository;
import br.edu.fatecpg.reviu.repositories.DeckRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final DictionaryAPIService  dictionaryAPIService;
    private final FsrsService fsrsService;

    //CREATE
    private Card buildCard(Deck deck, CardRequestDTO request) {

        Card card = new Card();
        card.setFrontText(request.frontText());
        card.setBackText(request.backText());
        card.setDeck(deck);

        card.setImageUrl(request.imageUrl());
        card.setAudioUrl(request.audioUrl());

        // Buscar áudio automático
        if (request.audioUrl() == null && request.frontText() != null) {

            String word = request.frontText().trim();

            if (!word.isBlank() && isValidDictionaryWord(word)) {
                String audioUrl = dictionaryAPIService.getFirstAudioUrl(word);
                card.setAudioUrl(audioUrl);
            }
        }

        // FSRS valores iniciais
        card.setDifficulty(0.3);
        card.setStability(0.0);
        card.setRetrievability(1.0);
        card.setInterval(0);
        card.setRepetitions(0);
        card.setLastReview(null);
        card.setNextReview(LocalDate.now());

        return card;
    }


    public Card createCard(Long deckId, CardRequestDTO request) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found"));

        Card card = buildCard(deck, request);

        return cardRepository.save(card);
    }

    public List<Card> createManyCards(Long deckId, List<CardRequestDTO> requests) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found"));

        List<Card> cards = requests.stream()
                .map(req -> buildCard(deck, req))
                .toList();

        return cardRepository.saveAll(cards);
    }


    //VIEW
    public List<Card> getCardByDeck(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        return cardRepository.findByDeckId(deckId);
    }

    //UPDATE
    public Card updateCard(Long deckId, Long cardId, CardRequestDTO request) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getDeck().getId().equals(deckId)) {
            throw new IllegalArgumentException("This card does not belong to this deck");
        }

        // Atualiza frontText se o usuário mandou
        if (request.frontText() != null) {
            card.setFrontText(request.frontText());
        }

        // Atualiza backText se o usuário mandou
        if (request.backText() != null) {
            card.setBackText(request.backText());
        }

        // Atualiza imagem apenas se o usuário mandou alguma imagem
        if (request.imageUrl() != null) {
            card.setImageUrl(request.imageUrl());
        }

        // -------- ÁUDIO --------
        if (request.audioUrl() != null) {
            // Usuário mandou explicitamente → substitui
            card.setAudioUrl(request.audioUrl());

        } else if (request.frontText() != null) {
            // Usuário mudou o frontText → tentar pegar novo áudio
            String word = request.frontText().trim();

            if (isValidDictionaryWord(word)) {
                String audio = dictionaryAPIService.getFirstAudioUrl(word);
                card.setAudioUrl(audio);
            }
        }
        // Se audioUrl e frontText NÃO foram enviados → mantém o áudio atual

        return cardRepository.save(card);
    }


    //DELETE
    public void deleteCard(Long deckId, Long cardId){
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getDeck().getId().equals(deck.getId())){
            throw new IllegalArgumentException("This card does not belong to this deck");
        }

        cardRepository.delete(card);
    }

    //IMPLEMENTAÇÃO DA LÓGICA "DIFICULDADE & INTERVALO" NA CARTA
    public Card reviewCard(Long cardId, int rating) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        fsrsService.processFsrs(card, rating); // usa FSRS 4.5 (implementação acima)

        return cardRepository.save(card);
    }

    public List<Card> getDueCards(Long deckId){
        return cardRepository.findByDeckIdAndNextReviewLessThanEqual(deckId, LocalDate.now());
    }

    private boolean isValidDictionaryWord(String word) {

        if (word == null || word.isBlank()) return false;

        word = word.trim();

        // Se tiver espaços, não é palavra
        if (word.contains(" ")) return false;

        // Apenas letras (sem números, acentos, hífens, etc)
        return word.matches("^[a-zA-Z]+$");
    }

}
