package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangePasswordDTO", description = "DTO para alteração da senha do usuário")
public record ChangePasswordDTO(

        @Schema(description = "Senha atual do usuário", example = "SenhaAtual123!")
        String currentPassword,

        @Schema(
                description = "Nova senha do usuário. Deve ter no mínimo 8 caracteres, conter letras, números e pelo menos um caractere especial.",
                example = "NovaSenha123!"
        )
        @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,}$",
                message = "A senha deve conter letras, números e caracteres especiais."
        )
        String newPassword

) {}

