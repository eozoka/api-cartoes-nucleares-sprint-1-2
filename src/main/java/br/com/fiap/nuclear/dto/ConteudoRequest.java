package br.com.fiap.nuclear.dto;

import br.com.fiap.nuclear.entities.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ConteudoRequest(
        @NotBlank @Size(max = 150)
        @Schema(example = "Tecnologia nuclear na agricultura") String titulo,
        @NotNull @Schema(example = "AGRICULTURA") Categoria categoria,
        @NotBlank @Size(max = 500)
        @Schema(example = "Conheça aplicações no campo.") String resumo,
        @NotBlank @Size(max = 5000)
        @Schema(example = "Técnicas isotópicas ajudam a estudar o solo e a água.") String texto,
        @NotBlank @URL(protocol = "https") @Size(max = 1000)
        @Schema(example = "https://www.iaea.org/topics/food-and-agriculture") String fonteUrl
) {
}
