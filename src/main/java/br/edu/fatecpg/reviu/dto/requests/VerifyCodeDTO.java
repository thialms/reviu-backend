package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VerifyCodeDTO", description = "DTO para envio do código de verificação do e-mail do usuário")
public record VerifyCodeDTO(

        @Schema(description = "Código de verificação enviado para o e-mail do usuário", example = "123456")
        String code

) {}
