package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReviewRequestDTO", description = "DTO para enviar a avaliação de um card usando o sistema SM2")
public record ReviewRequestDTO(

        @Schema(
                description = "Qualidade da revisão do card (0 a 5). Quanto maior, mais fácil o card será marcado como aprendido.",
                example = "4"
        )
        int quality

) {}
