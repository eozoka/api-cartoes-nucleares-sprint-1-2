package br.com.fiap.nuclear.service;

import br.com.fiap.nuclear.dto.PerguntaRequest;
import br.com.fiap.nuclear.dto.PerguntaResponse;
import br.com.fiap.nuclear.entities.Conteudo;
import br.com.fiap.nuclear.entities.Pergunta;
import br.com.fiap.nuclear.exception.ConteudoNaoEncontradoException;
import br.com.fiap.nuclear.exception.PerguntaNaoEncontradaException;
import br.com.fiap.nuclear.repository.ConteudoRepository;
import br.com.fiap.nuclear.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerguntaService {

    private final PerguntaRepository repository;
    private final ConteudoRepository conteudoRepository;

    public PerguntaService(PerguntaRepository repository, ConteudoRepository conteudoRepository) {
        this.repository = repository;
        this.conteudoRepository = conteudoRepository;
    }

    @Transactional(readOnly = true)
    public List<PerguntaResponse> listar(Long conteudoId) {
        buscarConteudo(conteudoId);
        return repository.findByConteudoIdOrderByIdAsc(conteudoId).stream().map(this::resposta).toList();
    }

    @Transactional
    public PerguntaResponse cadastrar(Long conteudoId, PerguntaRequest dados) {
        Conteudo conteudo = buscarConteudo(conteudoId);
        Pergunta pergunta = new Pergunta();
        pergunta.setConteudo(conteudo);
        pergunta.setEnunciado(dados.enunciado().trim());
        pergunta.setAlternativaCorreta(dados.alternativaCorreta().trim());
        pergunta.setExplicacao(dados.explicacao().trim());
        return resposta(repository.save(pergunta));
    }

    @Transactional
    public void excluir(Long conteudoId, Long id) {
        buscarConteudo(conteudoId);
        Pergunta pergunta = repository.findByIdAndConteudoId(id, conteudoId)
                .orElseThrow(() -> new PerguntaNaoEncontradaException(id));
        repository.delete(pergunta);
    }

    private Conteudo buscarConteudo(Long id) {
        return conteudoRepository.findById(id).orElseThrow(() -> new ConteudoNaoEncontradoException(id));
    }

    private PerguntaResponse resposta(Pergunta pergunta) {
        return new PerguntaResponse(
                pergunta.getId(),
                pergunta.getConteudo().getId(),
                pergunta.getEnunciado(),
                pergunta.getAlternativaCorreta(),
                pergunta.getExplicacao()
        );
    }
}
