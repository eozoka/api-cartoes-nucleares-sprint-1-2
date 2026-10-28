package br.com.fiap.nuclear.repository;

import br.com.fiap.nuclear.entities.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {

    List<Pergunta> findByConteudoIdOrderByIdAsc(Long conteudoId);

    Optional<Pergunta> findByIdAndConteudoId(Long id, Long conteudoId);

    void deleteAllByConteudoId(Long conteudoId);
}
