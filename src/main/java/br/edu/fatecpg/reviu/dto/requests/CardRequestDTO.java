package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardRequestDTO", description = "DTO para criação ou atualização de um card")
public record CardRequestDTO(

        @Schema(description = "ID do card (utilizado apenas em atualizações)", example = "1")
        Long id,

        @Schema(description = "Texto que ficará na frente do card", example = "O que é Java?")
        String frontText,

        @Schema(description = "Texto que ficará no verso do card", example = "Uma linguagem de programação orientada a objetos.")
        String backText,

        @Schema(description = "URL da imagem associada ao card (opcional)", example = "https://minhaapi.com/imagens/card1.png")
        String imageUrl,

        @Schema(description = "URL do áudio associado ao card (opcional)", example = "https://minhaapi.com/audios/card1.mp3")
        String audioUrl

) {}
