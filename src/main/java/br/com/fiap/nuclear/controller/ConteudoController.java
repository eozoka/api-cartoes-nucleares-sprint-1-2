package br.com.fiap.nuclear.controller;

import br.com.fiap.nuclear.dto.ConteudoRequest;
import br.com.fiap.nuclear.dto.ConteudoResponse;
import br.com.fiap.nuclear.entities.Categoria;
import br.com.fiap.nuclear.service.ConteudoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/conteudos")
@Tag(name = "conteúdos", description = "conteúdos educativos dos cartões interativos")
public class ConteudoController {

    private final ConteudoService service;

    public ConteudoController(ConteudoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "listar conteúdos com filtros opcionais de título e categoria")
    @ApiResponse(responseCode = "200", description = "lista de conteúdos; pode estar vazia", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConteudoResponse.class))))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<ConteudoResponse> listar(
            @Parameter(description = "trecho do título, sem diferenciar maiúsculas e minúsculas")
            @RequestParam(required = false) String titulo,
            @Parameter(description = "categoria: SAUDE, AGRICULTURA, INDUSTRIA, ENERGIA ou PESQUISA")
            @RequestParam(required = false) Categoria categoria) {
        return service.listar(titulo, categoria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "consultar conteúdo por id")
    @ApiResponse(responseCode = "200", description = "conteúdo encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConteudoResponse.class)))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ConteudoResponse buscar(@Parameter(description = "id do conteúdo") @PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "cadastrar conteúdo")
    @ApiResponse(responseCode = "201", description = "conteúdo criado; endereço no cabeçalho location", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConteudoResponse.class)))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ConteudoResponse> cadastrar(@Valid @RequestBody ConteudoRequest dados) {
        ConteudoResponse conteudo = service.cadastrar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conteudo.id())
                .toUri();
        return ResponseEntity.created(location).body(conteudo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "atualizar todos os campos do conteúdo")
    @ApiResponse(responseCode = "200", description = "conteúdo atualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConteudoResponse.class)))
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ConteudoResponse atualizar(
            @Parameter(description = "id do conteúdo") @PathVariable Long id,
            @Valid @RequestBody ConteudoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "excluir conteúdo e suas perguntas")
    @ApiResponse(responseCode = "204", description = "conteúdo e perguntas excluídos", content = @Content)
    @ApiResponse(responseCode = "400", description = "dados ou parâmetros inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "conteúdo não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> excluir(@Parameter(description = "id do conteúdo") @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
