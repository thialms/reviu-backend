package br.edu.fatecpg.reviu.controllers;

import br.edu.fatecpg.reviu.domain.card.Card;
import br.edu.fatecpg.reviu.domain.deck.Deck;
import br.edu.fatecpg.reviu.domain.user.User;
import br.edu.fatecpg.reviu.dto.*;
import br.edu.fatecpg.reviu.services.CardService;
import br.edu.fatecpg.reviu.services.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decks/{deckId}/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@PathVariable Long deckId, @RequestBody CardRequestDTO request){

        Card newCard = cardService.createCard(deckId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(newCard));
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> getCardByDeck(@PathVariable Long deckId){

        List<Card> cards= cardService.getCardByDeck(deckId);
        List<CardResponseDTO> response= cards.stream().map(CardResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> updateCard(@PathVariable Long deckId, @PathVariable Long cardId, @RequestBody CardRequestDTO request){

        Card updateCard = cardService.updateCard(deckId, cardId, request);
        return ResponseEntity.ok(new CardResponseDTO(updateCard));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long deckId, @PathVariable Long cardId){

        cardService.deleteCard(deckId, cardId);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{cardId}/review")
    public ResponseEntity<CardResponseDTO> reviewCard(@PathVariable Long cardId, @RequestBody ReviewRequestDTO request){

        Card reviewCard = cardService.reviewCard(cardId, request.quality());
        return ResponseEntity.ok(new CardResponseDTO(reviewCard));
    }

    @GetMapping("/due")
    public ResponseEntity<List<CardResponseDTO>> getDueCards(@PathVariable Long deckId){

       List<Card> dueCards = cardService.getDueCards(deckId);
       return ResponseEntity.ok(dueCards.stream().map(CardResponseDTO::new).toList());
    }
}
