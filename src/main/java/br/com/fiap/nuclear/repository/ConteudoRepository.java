package br.com.fiap.nuclear.repository;

import br.com.fiap.nuclear.entities.Categoria;
import br.com.fiap.nuclear.entities.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    List<Conteudo> findByTituloContainingIgnoreCase(String titulo);

    List<Conteudo> findByCategoria(Categoria categoria);

    List<Conteudo> findByTituloContainingIgnoreCaseAndCategoria(String titulo, Categoria categoria);
}
