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

    //CREATE
    public Card createCard(Long deckId, CardRequestDTO request) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found"));

        Card card = new Card();
        card.setFrontText(request.frontText());
        card.setBackText(request.backText());
        card.setDeck(deck);

        card.setImageUrl(request.imageUrl());
        card.setAudioUrl(request.audioUrl());

        if (request.audioUrl() == null) {
            String audioUrl = dictionaryAPIService.getFirstAudioUrl(request.frontText());
            card.setAudioUrl(audioUrl);
        }

        card.setRepetitions(0);
        card.setInterval(1);
        card.setEasinessFactor(2.5);
        card.setNextReview(LocalDate.now());

        return cardRepository.save(card);

    }

    //VIEW
    public List<Card> getCardByDeck(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        return cardRepository.findByDeckId(deckId);
    }

    //UPDATE
    public Card updateCard(Long deckId, Long cardId, CardRequestDTO request){
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getDeck().getId().equals(deck.getId())){
            throw new IllegalArgumentException("This card does not belong to this deck");
        }

        card.setFrontText(request.frontText());
        card.setBackText(request.backText());
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

    //LÓGICA DIFICULDADE & INTERVALO
    public void processReview(Card card, int quality) {

        double ef = card.getEasinessFactor();
        int repetitions = card.getRepetitions();
        int interval = card.getInterval();

        // 1. Reseta se quality < 3 (erro sério)
        if (quality < 3) {
            repetitions = 0;
            interval = 1; // revisa amanhã
        }
        else {
            // acerto
            repetitions++;

            if (repetitions == 1) {
                interval = 1; // 1º acerto
            } else if (repetitions == 2) {
                interval = 6; // 2º acerto
            } else {
                interval = (int) Math.round(interval * ef);
            }

            // cálculo do easiness factor
            ef = ef + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
            if (ef < 1.3)
                ef = 1.3;
        }

        card.setRepetitions(repetitions);
        card.setInterval(interval);
        card.setEasinessFactor(ef);

        card.setLastReview(LocalDate.now());
        card.setNextReview(LocalDate.now().plusDays(interval));
    }

    //IMPLEMENTAÇÃO DA LÓGICA "DIFICULDADE & INTERVALO" NA CARTA
    public Card reviewCard(Long cardId, int quality){
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        processReview(card, quality);
        return cardRepository.save(card);
    }

    public List<Card> getDueCards(Long deckId){
        return cardRepository.findByDeckIdAndNextReviewLessThanEqual(deckId, LocalDate.now());
    }
}
