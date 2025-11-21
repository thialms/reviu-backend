package br.edu.fatecpg.reviu.controllers;

import br.edu.fatecpg.reviu.domain.card.Card;
import br.edu.fatecpg.reviu.dto.requests.CardRequestDTO;
import br.edu.fatecpg.reviu.dto.requests.ReviewRequestDTO;
import br.edu.fatecpg.reviu.dto.responses.CardResponseDTO;
import br.edu.fatecpg.reviu.dto.responses.UploadResponseDTO;
import br.edu.fatecpg.reviu.services.CardService;
import br.edu.fatecpg.reviu.services.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/decks/{deckId}/cards")
@RequiredArgsConstructor
@Tag(
        name = "Cards",
        description = "Endpoints responsáveis pela criação, gerenciamento e revisão de cards dentro de um deck."
)
public class CardController {
    private final CardService cardService;
    private final UploadService  uploadService;

    @PostMapping
    @Operation(
            summary = "Cria um novo card",
            description = "Cria e adiciona um card ao deck informado pelo ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Deck não encontrado")
    })
    public ResponseEntity<CardResponseDTO> createCard(@PathVariable Long deckId, @RequestBody CardRequestDTO request){

        Card newCard = cardService.createCard(deckId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardResponseDTO(newCard));
    }

    @GetMapping
    @Operation(
            summary = "Lista os cards de um deck",
            description = "Retorna todos os cards pertencentes ao deck informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Deck não encontrado")
    })
    public ResponseEntity<List<CardResponseDTO>> getCardByDeck(@PathVariable Long deckId){

        List<Card> cards= cardService.getCardByDeck(deckId);
        List<CardResponseDTO> response= cards.stream().map(CardResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cardId}")
    @Operation(
            summary = "Atualiza um card",
            description = "Atualiza o conteúdo de um card específico dentro de um deck."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Deck ou card não encontrado")
    })
    public ResponseEntity<CardResponseDTO> updateCard(@PathVariable Long deckId, @PathVariable Long cardId, @RequestBody CardRequestDTO request){

        Card updateCard = cardService.updateCard(deckId, cardId, request);
        return ResponseEntity.ok(new CardResponseDTO(updateCard));
    }

    @DeleteMapping("/{cardId}")
    @Operation(
            summary = "Remove um card",
            description = "Exclui permanentemente um card de um deck."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Deck ou card não encontrado")
    })
    public ResponseEntity<Void> deleteCard(@PathVariable Long deckId, @PathVariable Long cardId){

        cardService.deleteCard(deckId, cardId);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{cardId}/review")
    @Operation(
            summary = "Registra uma revisão do card (SM2)",
            description = "Aplica o algoritmo SM2 para atualizar a dificuldade, intervalo e próximo agendamento de revisão do card."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Revisão registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nota de qualidade inválida"),
            @ApiResponse(responseCode = "404", description = "Card não encontrado")
    })
    public ResponseEntity<CardResponseDTO> reviewCard(@PathVariable Long cardId, @RequestBody ReviewRequestDTO request){

        Card reviewCard = cardService.reviewCard(cardId, request.quality());
        return ResponseEntity.ok(new CardResponseDTO(reviewCard));
    }

    @GetMapping("/due")
    @Operation(
            summary = "Retorna os cards de revisão pendente",
            description = "Lista todos os cards do deck cujo prazo de revisão está vencido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Deck não encontrado")
    })
    public ResponseEntity<List<CardResponseDTO>> getDueCards(@PathVariable Long deckId){

       List<Card> dueCards = cardService.getDueCards(deckId);
       return ResponseEntity.ok(dueCards.stream().map(CardResponseDTO::new).toList());
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(
            summary = "Realiza upload de arquivos",
            description = "Envia uma imagem ou áudio e retorna a URL gerada no armazenamento em nuvem."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "500", description = "Erro ao fazer upload")
    })
    public ResponseEntity<UploadResponseDTO> upload(@RequestParam MultipartFile file) throws IOException {
        String url = uploadService.uploadFile(file);
        return ResponseEntity.ok(new UploadResponseDTO(url));
    }
}
