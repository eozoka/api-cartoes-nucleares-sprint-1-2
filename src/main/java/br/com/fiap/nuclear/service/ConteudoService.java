package br.com.fiap.nuclear.service;

import br.com.fiap.nuclear.dto.ConteudoRequest;
import br.com.fiap.nuclear.dto.ConteudoResponse;
import br.com.fiap.nuclear.entities.Categoria;
import br.com.fiap.nuclear.entities.Conteudo;
import br.com.fiap.nuclear.exception.ConteudoNaoEncontradoException;
import br.com.fiap.nuclear.repository.ConteudoRepository;
import br.com.fiap.nuclear.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConteudoService {

    private final ConteudoRepository repository;
    private final PerguntaRepository perguntaRepository;

    public ConteudoService(ConteudoRepository repository, PerguntaRepository perguntaRepository) {
        this.repository = repository;
        this.perguntaRepository = perguntaRepository;
    }

    @Transactional(readOnly = true)
    public List<ConteudoResponse> listar(String titulo, Categoria categoria) {
        List<Conteudo> conteudos;
        if (titulo == null || titulo.isBlank()) {
            conteudos = categoria == null ? repository.findAll() : repository.findByCategoria(categoria);
        } else if (categoria == null) {
            conteudos = repository.findByTituloContainingIgnoreCase(titulo.trim());
        } else {
            conteudos = repository.findByTituloContainingIgnoreCaseAndCategoria(titulo.trim(), categoria);
        }
        return conteudos.stream().map(this::resposta).toList();
    }

    @Transactional(readOnly = true)
    public ConteudoResponse buscar(Long id) {
        return resposta(buscarEntidade(id));
    }

    @Transactional
    public ConteudoResponse cadastrar(ConteudoRequest dados) {
        Conteudo conteudo = preencher(new Conteudo(), dados);
        return resposta(repository.save(conteudo));
    }

    @Transactional
    public ConteudoResponse atualizar(Long id, ConteudoRequest dados) {
        Conteudo conteudo = preencher(buscarEntidade(id), dados);
        return resposta(repository.save(conteudo));
    }

    @Transactional
    public void excluir(Long id) {
        Conteudo conteudo = buscarEntidade(id);
        perguntaRepository.deleteAllByConteudoId(id);
        repository.delete(conteudo);
    }

    private Conteudo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new ConteudoNaoEncontradoException(id));
    }

    private Conteudo preencher(Conteudo conteudo, ConteudoRequest dados) {
        conteudo.setTitulo(dados.titulo().trim());
        conteudo.setCategoria(dados.categoria());
        conteudo.setResumo(dados.resumo().trim());
        conteudo.setTexto(dados.texto().trim());
        conteudo.setFonteUrl(dados.fonteUrl());
        return conteudo;
    }

    private ConteudoResponse resposta(Conteudo conteudo) {
        return new ConteudoResponse(
                conteudo.getId(),
                conteudo.getTitulo(),
                conteudo.getCategoria(),
                conteudo.getResumo(),
                conteudo.getTexto(),
                conteudo.getFonteUrl()
        );
    }
}
