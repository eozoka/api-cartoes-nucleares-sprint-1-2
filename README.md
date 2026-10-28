# API cartoes nucleares

## Problema e solução

A tecnologia nuclear participa de áreas como saúde, agricultura e indústria, mas suas aplicações podem parecer distantes do cotidiano. A proposta é apoiar a aprendizagem com cartões de leitura acessível e perguntas que reforçam o conteúdo.

Esta API RESTful gerencia os conteúdos e suas perguntas. Cada cartão apresenta título, categoria, resumo, texto e fonte oficial para aprofundamento. A entrega do primeiro sprint implementa o backend; os cartões podem ser explorados pelo Swagger ou por um cliente HTTP.

## Tecnologias e organização

- Java 21 e Spring Boot 3.5.13.
- Spring Web, Spring Data JPA e Spring Validation.
- H2 em arquivo para execução local e em memória para os testes.
- springdoc OpenAPI 2.8.17, Swagger UI, JUnit e MockMvc.
- Maven Wrapper incluído no projeto.

Os pacotes `controller`, `service`, `repository`, `entities`, `dto` e `exception` separam requisições, regras, persistência, entidades, dados de entrada/saída e tratamento de erros. O pacote `config` contém a carga inicial. As respostas usam DTOs, sem expor entidades JPA.

## Como executar

Instale o JDK 21, configure `JAVA_HOME` e abra o terminal na pasta que contém `pom.xml`. A primeira execução precisa de internet para baixar o Maven e as dependências.

PowerShell no Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

A aplicação inicia em [http://localhost:8080](http://localhost:8080). O H2 mantém os registros em `data/nuclear.mv.db`, relativo à pasta de execução. Reiniciar preserva os dados. Não há autenticação nesta aplicação educativa local.

## Swagger e OpenAPI

- [Swagger UI](http://localhost:8080/swagger-ui/index.html): operações, parâmetros, esquemas, validações e respostas.
- [OpenAPI JSON](http://localhost:8080/v3/api-docs): especificação gerada pela aplicação.

## Modelo de dados

| Entidade | Campos |
|---|---|
| Conteudo | id, titulo, categoria, resumo, texto, fonteUrl |
| Pergunta | id, enunciado, alternativaCorreta, explicacao, conteudo_id |

Um `Conteudo` possui zero ou muitas `Pergunta`; cada pergunta pertence a exatamente um conteúdo. O vínculo usa `@OneToMany(mappedBy = "conteudo")` e `@ManyToOne`, com chave estrangeira obrigatória. Ao excluir um conteúdo, o serviço exclui suas perguntas na mesma transação, antes do conteúdo. Não há cascade configurado.

`Categoria` é um enum gravado como texto: `SAUDE`, `AGRICULTURA`, `INDUSTRIA`, `ENERGIA` e `PESQUISA`. Envie esses valores exatamente em maiúsculas. Categorias desconhecidas e números no JSON retornam 400.

Todos os campos de entrada são obrigatórios. Limites de conteúdo: título 150, resumo 500, texto 5000 e fonteUrl 1000 caracteres. A fonte deve ser uma URL HTTPS. Limites de pergunta: enunciado 500, alternativaCorreta 300 e explicacao 1000 caracteres. `alternativaCorreta` armazena o texto da resposta esperada; esta versão não cadastra alternativas incorretas nem calcula pontuação.

## Endpoints

| Método | Caminho | Resultado |
|---|---|---|
| POST | `/conteudos` | Cadastra conteúdo; 201 com DTO e cabeçalho Location |
| GET | `/conteudos` | Lista conteúdos; 200, inclusive lista vazia |
| GET | `/conteudos?titulo=agricultura` | Busca trecho do título sem diferenciar maiúsculas/minúsculas |
| GET | `/conteudos?categoria=AGRICULTURA` | Filtra categoria |
| GET | `/conteudos?titulo=isótopos&categoria=AGRICULTURA` | Combina os dois filtros com AND |
| GET | `/conteudos/{id}` | Consulta conteúdo; 200 ou 404 |
| PUT | `/conteudos/{id}` | Substitui todos os campos; 200, 400 ou 404 |
| DELETE | `/conteudos/{id}` | Exclui conteúdo e perguntas; 204 ou 404 |
| POST | `/conteudos/{conteudoId}/perguntas` | Cadastra pergunta; 201, 400 ou 404 |
| GET | `/conteudos/{conteudoId}/perguntas` | Lista perguntas por ID crescente; 200 ou 404 |
| DELETE | `/conteudos/{conteudoId}/perguntas/{id}` | Exclui pergunta do conteúdo indicado; 204 ou 404 |

As consultas usam Query Methods do Spring Data, sem JPQL. Título ausente ou em branco não limita a busca. Filtros sem resultados retornam `[]`. Conteúdo inexistente retorna 404 nas três operações de perguntas. Uma pergunta de outro conteúdo também retorna 404 ao tentar excluí-la.

Erros seguem `ProblemDetail` (`application/problem+json`), com `type`, `title`, `status`, `detail` e `instance`. Erros de validação incluem o mapa `campos`. JSON ou parâmetro inválido retorna 400; método não permitido retorna 405 com cabeçalho `Allow`; falha inesperada retorna 500 com mensagem genérica.

## Dados de teste automáticos

`DadosIniciais` insere seis conteúdos educativos e uma pergunta para cada um somente quando `conteudoRepository.count() == 0`. A carga é transacional e cobre as cinco categorias, com dois cartões de agricultura:

1. Medicina nuclear e diagnóstico.
2. Isótopos na agricultura.
3. Tecnologia nuclear na indústria.
4. Da fissão à eletricidade.
5. Reatores de pesquisa.
6. Irradiação de alimentos.

Se já houver qualquer conteúdo no H2, a carga não altera os registros nem insere exemplos adicionais. Se todos os conteúdos forem excluídos, a próxima inicialização recria os exemplos. Os IDs são gerados pelo banco; consulte a lista para obter os IDs reais.

Os arquivos `exemplos/conteudo.json` e `exemplos/pergunta.json` podem ser copiados para o Swagger. Cadastre um conteúdo, copie seu ID e use esse ID na rota de perguntas. Também é possível usar um dos conteúdos da carga inicial.

## Como rodar os testes

PowerShell:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

`ConteudoApiTest` usa SpringBootTest, MockMvc e H2 em memória, com rollback de cada teste. Verifica CRUD de conteúdo, busca textual, filtros combinados, validações, categorias inválidas, cadastro/listagem/exclusão de perguntas, conteúdo inexistente, vínculo das perguntas, exclusão com dependências, carga inicial sem duplicação, ProblemDetail e documentação. O handler de erro 500 também é verificado diretamente para garantir a mensagem genérica.

## Pesquisa e fontes

Textos autorais em português, baseados em fontes oficiais consultadas em 27/10/2026. Cada conteúdo inclui a referência em `fonteUrl`.

| Tema | Fonte oficial |
|---|---|
| Saúde | [NIH/NIBIB - Nuclear Medicine](https://www.nibib.nih.gov/science-education/science-topics/nuclear-medicine) |
| Agricultura | [IAEA - Land and water management](https://www.iaea.org/topics/land-and-water-management) |
| Agricultura e alimentação | [IAEA/FAO - Food and agriculture](https://www.iaea.org/topics/food-and-agriculture) |
| Indústria | [IAEA - Industrial applications](https://www.iaea.org/topics/industrial-applications) |
| Energia | [U.S. Department of Energy - How does a nuclear reactor work?](https://www.energy.gov/ne/articles/nuclear-101-how-does-nuclear-reactor-work) |
| Pesquisa | [IAEA - Research reactors](https://www.iaea.org/topics/research-reactors) |
| Alimentos | [IAEA - Food irradiation](https://www.iaea.org/topics/food-irradiation) |

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Murillo Kenzo | RM566948 | 2TDSPA |
| Kauan Prado | RM566990 | 2TDSPA |
| Cauã Rocha | RM567593 | 2TDSPR |
| Gustavo Souza | RM567134 | 2TDSPR |
