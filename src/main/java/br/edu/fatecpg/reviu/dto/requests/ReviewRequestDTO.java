package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ReviewRequestDTO", description = "DTO para enviar a avaliação (rating) de um card no sistema FSRS 4.5")
public record ReviewRequestDTO(

        @Schema(
                description = """
                        Avaliação do usuário segundo o FSRS:
                        1 = Again (esqueceu)
                        2 = Hard (difícil)
                        3 = Good (acertou normalmente)
                        4 = Easy (muito fácil)
                        """,
                example = "3"
        )
        int rating

) {}
