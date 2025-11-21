package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmailRequestDTO", description = "DTO para envio de e-mail do usuário, usado em verificação ou reenvio de código")
public record EmailRequestDTO(

        @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
        String email

) {}
