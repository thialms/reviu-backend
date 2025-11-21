package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "RegisterRequestDTO", description = "DTO para registro de um novo usuário")
public record RegisterRequestDTO(

        @Schema(description = "Nome completo do usuário", example = "Thiago Almeida")
        String name,

        @Schema(description = "Nome de usuário único", example = "thi_almeida")
        String username,

        @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
        String email,

        @Schema(
                description = "Senha do usuário. Deve ter no mínimo 8 caracteres, conter letras, números e pelo menos um caractere especial.",
                example = "SenhaForte123!"
        )
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,}$",
                message = "A senha deve conter letras, números e caracteres especiais."
        )
        String password

) {}
