package br.com.fiap.nuclear.controller;

import br.com.fiap.nuclear.dto.PerguntaRequest;
import br.com.fiap.nuclear.dto.PerguntaResponse;
import br.com.fiap.nuclear.service.PerguntaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conteudos/{conteudoId}/perguntas")
@Tag(name = "perguntas", description = "perguntas educativas vinculadas a um conteúdo")
public class PerguntaController {

    private final PerguntaService service;

    public PerguntaController(PerguntaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "listar perguntas de um conteúdo")
    @ApiResponse(responseCode = "200", description = "lista de perguntas; pode estar vazia", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PerguntaResponse.class))))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<PerguntaResponse> listar(
            @Parameter(description = "id do conteúdo") @PathVariable Long conteudoId) {
        return service.listar(conteudoId);
    }

    @PostMapping
    @Operation(summary = "cadastrar pergunta em um conteúdo")
    @ApiResponse(responseCode = "201", description = "pergunta criada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PerguntaResponse.class)))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<PerguntaResponse> cadastrar(
            @Parameter(description = "id do conteúdo") @PathVariable Long conteudoId,
            @Valid @RequestBody PerguntaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(conteudoId, dados));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "excluir pergunta de um conteúdo")
    @ApiResponse(responseCode = "204", description = "pergunta excluída", content = @Content)
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo ou pergunta não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> excluir(
            @Parameter(description = "id do conteúdo") @PathVariable Long conteudoId,
            @Parameter(description = "id da pergunta pertencente ao conteúdo") @PathVariable Long id) {
        service.excluir(conteudoId, id);
        return ResponseEntity.noContent().build();
    }
}
