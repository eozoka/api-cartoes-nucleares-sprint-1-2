package br.com.fiap.nuclear.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerguntaRequest(
        @NotBlank @Size(max = 500)
        @Schema(example = "Para que servem técnicas isotópicas na agricultura?") String enunciado,
        @NotBlank @Size(max = 300)
        @Schema(example = "Estudar o uso da água e dos nutrientes.") String alternativaCorreta,
        @NotBlank @Size(max = 1000)
        @Schema(example = "Isótopos ajudam a acompanhar a água e os nutrientes no solo.") String explicacao
) {
}
