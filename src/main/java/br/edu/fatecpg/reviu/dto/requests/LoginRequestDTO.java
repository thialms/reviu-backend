package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequestDTO", description = "DTO para autenticação de usuário (login)")
public record LoginRequestDTO(

        @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
        String email,

        @Schema(description = "Senha do usuário", example = "SenhaForte123!")
        String password

) {}
