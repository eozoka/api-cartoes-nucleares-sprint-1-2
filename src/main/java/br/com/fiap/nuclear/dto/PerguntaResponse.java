package br.com.fiap.nuclear.dto;

public record PerguntaResponse(
        Long id,
        Long conteudoId,
        String enunciado,
        String alternativaCorreta,
        String explicacao
) {
}
