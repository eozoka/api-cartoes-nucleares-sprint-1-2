package br.com.fiap.nuclear.exception;

public class ConteudoNaoEncontradoException extends RuntimeException {

    public ConteudoNaoEncontradoException(Long id) {
        super("Conteúdo não encontrado: " + id);
    }
}
