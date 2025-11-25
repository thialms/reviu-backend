package br.edu.fatecpg.reviu.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "ResetForgotPasswordDTO", description = "DTO para setar nova senha do usuário")
public record ResetForgotPasswordDTO(String code,
                                     @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres.")
                                     @Pattern(
                                             regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,}$",
                                             message = "A senha deve conter letras, números e caracteres especiais."
                                     )
                                     String newPassword) {
}