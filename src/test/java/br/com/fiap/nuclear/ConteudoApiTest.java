package br.com.fiap.nuclear;

import br.com.fiap.nuclear.config.DadosIniciais;
import br.com.fiap.nuclear.dto.ConteudoRequest;
import br.com.fiap.nuclear.dto.PerguntaRequest;
import br.com.fiap.nuclear.entities.Categoria;
import br.com.fiap.nuclear.exception.GlobalExceptionHandler;
import br.com.fiap.nuclear.repository.ConteudoRepository;
import br.com.fiap.nuclear.repository.PerguntaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ConteudoApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ConteudoRepository conteudoRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private DadosIniciais dadosIniciais;

    private String corpo(String titulo, Categoria categoria) throws Exception {
        return mapper.writeValueAsString(new ConteudoRequest(
                titulo,
                categoria,
                "Resumo educativo",
                "Conteúdo educativo",
                "https://www.iaea.org/topics/food-and-agriculture"
        ));
    }

    private long cadastrarConteudo(String titulo, Categoria categoria) throws Exception {
        var resposta = mvc.perform(post("/conteudos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo(titulo, categoria)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.perguntas").doesNotExist())
                .andReturn();
        return mapper.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
    }

    private String corpoPergunta() throws Exception {
        return mapper.writeValueAsString(new PerguntaRequest(
                "O que técnicas isotópicas ajudam a estudar?",
                "A água e os nutrientes.",
                "Isótopos ajudam a acompanhar processos no solo."
        ));
    }

    private long cadastrarPergunta(long conteudoId) throws Exception {
        var resposta = mvc.perform(post("/conteudos/{conteudoId}/perguntas", conteudoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoPergunta()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conteudoId").value(conteudoId))
                .andExpect(jsonPath("$.enunciado").value("O que técnicas isotópicas ajudam a estudar?"))
                .andExpect(jsonPath("$.alternativaCorreta").value("A água e os nutrientes."))
                .andExpect(jsonPath("$.explicacao").value("Isótopos ajudam a acompanhar processos no solo."))
                .andExpect(jsonPath("$.conteudo").doesNotExist())
                .andReturn();
        return mapper.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void cicloCompleto() throws Exception {
        long id = cadastrarConteudo("Cartão teste agricultura", Categoria.AGRICULTURA);
        mvc.perform(get("/conteudos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].titulo").value("Cartão teste agricultura"));
        mvc.perform(get("/conteudos").param("titulo", "CARTÃO TESTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/conteudos").param("titulo", "termo-inexistente-xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(put("/conteudos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Novo título", Categoria.PESQUISA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo título"))
                .andExpect(jsonPath("$.categoria").value("PESQUISA"));
        mvc.perform(get("/conteudos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo título"))
                .andExpect(jsonPath("$.resumo").value("Resumo educativo"))
                .andExpect(jsonPath("$.texto").value("Conteúdo educativo"))
                .andExpect(jsonPath("$.fonteUrl").value("https://www.iaea.org/topics/food-and-agriculture"));
        mvc.perform(delete("/conteudos/{id}", id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/conteudos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
        mvc.perform(put("/conteudos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Outro", Categoria.SAUDE)))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/conteudos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void filtraPorCategoriaETitulo() throws Exception {
        long id = cadastrarConteudo("Filtro exclusivo alfa", Categoria.ENERGIA);
        cadastrarConteudo("Filtro exclusivo beta", Categoria.SAUDE);
        mvc.perform(get("/conteudos").param("categoria", "ENERGIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].categoria", everyItem(is("ENERGIA"))))
                .andExpect(jsonPath("$[?(@.id == " + id + ")].titulo").value("Filtro exclusivo alfa"));
        mvc.perform(get("/conteudos").param("titulo", "  FILTRO EXCLUSIVO  ").param("categoria", "ENERGIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/conteudos").param("titulo", "   ").param("categoria", "ENERGIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].categoria", everyItem(is("ENERGIA"))));
        mvc.perform(get("/conteudos").param("titulo", "Filtro exclusivo").param("categoria", "PESQUISA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void entradasInvalidas() throws Exception {
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON).content(corpo(" ", Categoria.SAUDE)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.campos.titulo").exists());
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Teste", Categoria.SAUDE).replace("https://www.iaea.org/topics/food-and-agriculture", "abc")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.fonteUrl").exists());
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Teste", Categoria.SAUDE).replace("https://", "http://")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/conteudos/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejeitaCategoriaInvalida() throws Exception {
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Teste", Categoria.SAUDE).replace("SAUDE", "INVALIDA")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("Teste", Categoria.SAUDE).replace("\"SAUDE\"", "0")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON).content(corpo("Teste", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.categoria").exists());
        mvc.perform(get("/conteudos").param("categoria", "INVALIDA"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validaCamposDoConteudo() throws Exception {
        String valido = corpo("Teste", Categoria.SAUDE);
        String[] campos = {"titulo", "resumo", "texto", "fonteUrl"};
        int[] limites = {150, 500, 5000, 1000};
        for (int indice = 0; indice < campos.length; indice++) {
            for (String valor : new String[]{" ", "x".repeat(limites[indice] + 1)}) {
                var dados = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(valido);
                dados.put(campos[indice], valor);
                mvc.perform(post("/conteudos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dados)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.campos." + campos[indice]).exists());
            }
        }
    }

    @Test
    void cicloBasicoPergunta() throws Exception {
        long conteudoId = cadastrarConteudo("Quiz teste", Categoria.AGRICULTURA);
        mvc.perform(get("/conteudos/{conteudoId}/perguntas", conteudoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        long id = cadastrarPergunta(conteudoId);
        mvc.perform(get("/conteudos/{conteudoId}/perguntas", conteudoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(delete("/conteudos/{conteudoId}/perguntas/{id}", conteudoId, id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/conteudos/{conteudoId}/perguntas", conteudoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/conteudos/{conteudoId}/perguntas/{id}", conteudoId, id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void perguntaEmConteudoInexistente() throws Exception {
        mvc.perform(get("/conteudos/999999/perguntas"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        mvc.perform(post("/conteudos/999999/perguntas").contentType(MediaType.APPLICATION_JSON).content(corpoPergunta()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mvc.perform(delete("/conteudos/999999/perguntas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoExcluiPerguntaDeOutroConteudo() throws Exception {
        long primeiro = cadastrarConteudo("Primeiro quiz", Categoria.SAUDE);
        long segundo = cadastrarConteudo("Segundo quiz", Categoria.ENERGIA);
        long perguntaId = cadastrarPergunta(primeiro);
        mvc.perform(delete("/conteudos/{conteudoId}/perguntas/{id}", segundo, perguntaId))
                .andExpect(status().isNotFound());
        mvc.perform(get("/conteudos/{conteudoId}/perguntas", primeiro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(perguntaId));
    }

    @Test
    void excluirConteudoRemovePerguntas() throws Exception {
        long conteudoId = cadastrarConteudo("Excluir quiz", Categoria.PESQUISA);
        long perguntaId = cadastrarPergunta(conteudoId);
        mvc.perform(delete("/conteudos/{id}", conteudoId))
                .andExpect(status().isNoContent());
        conteudoRepository.flush();
        assertFalse(perguntaRepository.existsById(perguntaId));
        assertFalse(conteudoRepository.existsById(conteudoId));
    }

    @Test
    void validaPergunta() throws Exception {
        long conteudoId = cadastrarConteudo("Validação quiz", Categoria.SAUDE);
        String[] campos = {"enunciado", "alternativaCorreta", "explicacao"};
        int[] limites = {500, 300, 1000};
        for (int indice = 0; indice < campos.length; indice++) {
            for (String valor : new String[]{" ", "x".repeat(limites[indice] + 1)}) {
                var dados = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(corpoPergunta());
                dados.put(campos[indice], valor);
                mvc.perform(post("/conteudos/{conteudoId}/perguntas", conteudoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dados)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                        .andExpect(jsonPath("$.campos." + campos[indice]).exists());
            }
        }
    }

    @Test
    void metodoNaoPermitido() throws Exception {
        mvc.perform(put("/conteudos").contentType(MediaType.APPLICATION_JSON).content(corpo("Teste", Categoria.SAUDE)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().exists("Allow"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void errosDoFrameworkMantemProblemDetail() throws Exception {
        mvc.perform(get("/rota-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/conteudos").contentType(MediaType.TEXT_PLAIN).content("texto"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(415));
    }

    @Test
    void erroInesperadoNaoExpoeDetalhesInternos() throws Exception {
        var resposta = new GlobalExceptionHandler().erroInesperado(new RuntimeException("segredo interno"));
        assertEquals(500, resposta.getStatusCode().value());
        assertNotNull(resposta.getBody());
        assertEquals("Ocorreu um erro interno. Tente novamente mais tarde.", resposta.getBody().getDetail());
        String json = mapper.writeValueAsString(resposta.getBody());
        assertFalse(json.contains("segredo interno"));
        assertFalse(json.contains("stackTrace"));
    }

    @Test
    void dadosIniciaisNaoDuplicam() {
        assertEquals(6, conteudoRepository.count());
        assertEquals(6, perguntaRepository.count());
        for (Categoria categoria : Categoria.values()) {
            assertFalse(conteudoRepository.findByCategoria(categoria).isEmpty());
        }
        assertTrue(conteudoRepository.findAll().stream()
                .allMatch(conteudo -> perguntaRepository.findByConteudoIdOrderByIdAsc(conteudo.getId()).size() == 1));
        dadosIniciais.run();
        assertEquals(6, conteudoRepository.count());
        assertEquals(6, perguntaRepository.count());
    }

    @Test
    void documentacao() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/conteudos']").exists())
                .andExpect(jsonPath("$.paths['/conteudos/{conteudoId}/perguntas']").exists())
                .andExpect(jsonPath("$.paths['/conteudos/{conteudoId}/perguntas/{id}']").exists())
                .andExpect(jsonPath("$.components.schemas.ConteudoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.PerguntaResponse").exists())
                .andExpect(jsonPath("$.components.schemas.Conteudo").doesNotExist())
                .andExpect(jsonPath("$.components.schemas.Pergunta").doesNotExist())
                .andExpect(jsonPath("$.paths['/conteudos'].get.parameters[0].name").value("titulo"))
                .andExpect(jsonPath("$.paths['/conteudos'].get.parameters[1].name").value("categoria"))
                .andExpect(jsonPath("$.paths['/conteudos'].post.responses['201'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/ConteudoResponse"));
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
