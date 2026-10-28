package br.com.fiap.nuclear.dto;

import br.com.fiap.nuclear.entities.Categoria;

public record ConteudoResponse(
        Long id,
        String titulo,
        Categoria categoria,
        String resumo,
        String texto,
        String fonteUrl
) {
}
