package br.edu.fatecpg.reviu.dto.responses;

import br.edu.fatecpg.reviu.domain.card.Card;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "CardResponseDTO", description = "DTO de resposta contendo as informações atualizadas do card e FSRS")
public record CardResponseDTO(

        @Schema(description = "ID único do card", example = "1")
        Long id,

        @Schema(description = "Texto frontal do card", example = "What is polymorphism?")
        String frontText,

        @Schema(description = "Texto traseiro do card", example = "Ability of objects to take many forms.")
        String backText,

        @Schema(description = "Dificuldade atual do card (FSRS)", example = "0.32")
        double difficulty,

        @Schema(description = "Estabilidade atual do card (FSRS)", example = "12.5")
        double stability,

        @Schema(description = "Retenção atual (R), baseada na curva de esquecimento", example = "0.83")
        double retrievability,

        @Schema(description = "Data da última revisão")
        LocalDate lastReview,

        @Schema(description = "Data da próxima revisão agendada")
        LocalDate nextReview,

        @Schema(description = "URL da imagem do card (opcional)")
        String imageUrl,

        @Schema(description = "URL do áudio do card (opcional)")
        String audioUrl

) {
    public CardResponseDTO(Card card) {
        this(
                card.getId(),
                card.getFrontText(),
                card.getBackText(),
                card.getDifficulty(),
                card.getStability(),
                card.getRetrievability(),
                card.getLastReview(),
                card.getNextReview(),
                card.getImageUrl(),
                card.getAudioUrl()
        );
    }
}
