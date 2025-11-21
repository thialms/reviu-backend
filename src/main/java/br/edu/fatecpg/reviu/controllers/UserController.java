package br.edu.fatecpg.reviu.controllers;

import br.edu.fatecpg.reviu.domain.user.User;
import br.edu.fatecpg.reviu.dto.requests.ChangePasswordDTO;
import br.edu.fatecpg.reviu.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(
        name = "Usuário",
        description = "Endpoints relacionados à gestão de dados do usuário autenticado."
)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    @Operation(
            summary = "Altera a senha do usuário",
            description = "Permite que o usuário autenticado altere sua senha atual para uma nova senha."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO body) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Valida se a senha atual esta correta
        if (!passwordEncoder.matches(body.currentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Senha atual incorreta.");
        }

        // 3. Seta a nova senha
        user.setPassword(passwordEncoder.encode(body.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}
