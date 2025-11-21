package br.edu.fatecpg.reviu.dto;

import br.edu.fatecpg.reviu.domain.card.Card;

import java.time.LocalDate;
import java.util.List;

public record CardResponseDTO(Long id, String frontText, String backText, int repetition, int interval, double easinessFactor, LocalDate nextReview) {
    public CardResponseDTO(Card card){
        this(
                card.getId(),
                card.getFrontText(),
                card.getBackText(),

                card.getRepetitions(),
                card.getInterval(),
                card.getEasinessFactor(),
                card.getNextReview()
        );
    }
}
