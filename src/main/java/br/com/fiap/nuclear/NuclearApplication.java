package br.com.fiap.nuclear;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API cartoes nucleares",
        description = "api de conteúdos e perguntas educativas sobre tecnologia nuclear",
        version = "1.0"
))
public class NuclearApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuclearApplication.class, args);
    }
}
