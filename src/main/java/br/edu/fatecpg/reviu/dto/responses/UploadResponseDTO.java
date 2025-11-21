package br.edu.fatecpg.reviu.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UploadResponseDTO", description = "DTO de resposta após upload de arquivo, retornando a URL do arquivo enviado")
public record UploadResponseDTO(

        @Schema(description = "URL do arquivo enviado (imagem ou áudio)", example = "https://minhaapi.com/uploads/arquivo1.png")
        String url

) {}
