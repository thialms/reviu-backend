package br.edu.fatecpg.reviu.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginAndRegisterResponseDTO", description = "DTO de resposta enviado após login ou registro do usuário, contendo nome e token JWT")
public record LoginAndRegisterResponseDTO(

        @Schema(description = "Nome do usuário", example = "Thiago Almeida")
        String name,

        @Schema(description = "Token JWT para autenticação nas próximas requisições", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token

) {}
