package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardRequestDTO", description = "DTO para criação ou atualização de um card")
public record CardRequestDTO(

        @Schema(
                description = "Texto que ficará na frente do card",
                example = "What is polymorphism?"
        )
        String frontText,

        @Schema(
                description = "Texto que ficará no verso do card",
                example = "Ability of objects to take many forms."
        )
        String backText,

        @Schema(
                description = "URL da imagem associada ao card (opcional)",
                example = "https://minhaapi.com/imagens/card1.png"
        )
        String imageUrl,

        @Schema(
                description = "URL do áudio associado ao card (opcional)",
                example = "https://minhaapi.com/audios/card1.mp3"
        )
        String audioUrl

) {}
