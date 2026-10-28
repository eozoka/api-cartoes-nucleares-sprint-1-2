package br.com.fiap.nuclear.config;

import br.com.fiap.nuclear.entities.Categoria;
import br.com.fiap.nuclear.entities.Conteudo;
import br.com.fiap.nuclear.entities.Pergunta;
import br.com.fiap.nuclear.repository.ConteudoRepository;
import br.com.fiap.nuclear.repository.PerguntaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final ConteudoRepository conteudoRepository;
    private final PerguntaRepository perguntaRepository;

    public DadosIniciais(ConteudoRepository conteudoRepository, PerguntaRepository perguntaRepository) {
        this.conteudoRepository = conteudoRepository;
        this.perguntaRepository = perguntaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (conteudoRepository.count() != 0) {
            return;
        }

        cadastrar(
                "Medicina nuclear e diagnóstico", Categoria.SAUDE,
                "Moléculas radioativas ajudam a avaliar o funcionamento do corpo.",
                "A medicina nuclear utiliza radiofármacos para investigar funções do organismo. "
                        + "No diagnóstico, equipamentos detectam a radiação emitida pelo material administrado e formam imagens. "
                        + "Essas informações ajudam a identificar doenças. Existem também aplicações terapêuticas, "
                        + "realizadas por equipes especializadas.",
                "https://www.nibib.nih.gov/science-education/science-topics/nuclear-medicine",
                "O que equipamentos de medicina nuclear detectam em exames?",
                "A radiação emitida pelo radiofármaco administrado.",
                "A detecção permite formar imagens e estudar o funcionamento de órgãos e tecidos."
        );
        cadastrar(
                "Isótopos na agricultura", Categoria.AGRICULTURA,
                "Técnicas isotópicas ajudam a estudar o uso da água e dos nutrientes.",
                "Isótopos são formas de um elemento químico com diferentes números de nêutrons. "
                        + "Técnicas isotópicas permitem acompanhar a água e nutrientes no sistema solo-planta. "
                        + "Os resultados ajudam a planejar a irrigação e a adubação com melhor uso dos recursos naturais. "
                        + "Nem todo isótopo é radioativo: também existem isótopos estáveis usados nessas pesquisas.",
                "https://www.iaea.org/topics/land-and-water-management",
                "Todo isótopo usado em pesquisa agrícola é radioativo?",
                "Não. Também são usados isótopos estáveis.",
                "Isótopos estáveis permitem estudar processos ligados à água e aos nutrientes sem emitir radiação por decaimento."
        );
        cadastrar(
                "Tecnologia nuclear na indústria", Categoria.INDUSTRIA,
                "A radiação pode ajudar a analisar materiais e acompanhar processos.",
                "Técnicas nucleares permitem avaliar propriedades de materiais e acompanhar processos industriais. "
                        + "A radiação também pode ser usada para esterilizar componentes e modificar propriedades de materiais. "
                        + "Cada aplicação exige equipamentos adequados e controle de segurança.",
                "https://www.iaea.org/topics/industrial-applications",
                "Qual é uma aplicação industrial da tecnologia nuclear?",
                "Avaliar propriedades de materiais e monitorar processos.",
                "Técnicas nucleares fornecem informações úteis para o controle de processos industriais."
        );
        cadastrar(
                "Da fissão à eletricidade", Categoria.ENERGIA,
                "Uma usina nuclear transforma o calor da fissão em energia elétrica.",
                "Na fissão nuclear, um núcleo atômico se divide e libera energia. "
                        + "Em usinas nucleares, a reação em cadeia é controlada para produzir calor. "
                        + "Esse calor é usado para gerar vapor que movimenta uma turbina ligada a um gerador elétrico. "
                        + "A operação exige sistemas de segurança e gestão do combustível utilizado.",
                "https://www.energy.gov/ne/articles/nuclear-101-how-does-nuclear-reactor-work",
                "O que movimenta a turbina em uma usina nuclear?",
                "O vapor produzido com o calor da fissão.",
                "A turbina aciona um gerador que transforma energia mecânica em energia elétrica."
        );
        cadastrar(
                "Reatores de pesquisa", Categoria.PESQUISA,
                "Reatores de pesquisa apoiam ciência, formação e aplicações práticas.",
                "Reatores de pesquisa são utilizados em investigação científica, desenvolvimento, educação e treinamento. "
                        + "Eles produzem nêutrons que podem ser usados em áreas como medicina, agricultura e indústria. "
                        + "Seu papel principal é apoiar pesquisas e aplicações, em vez de fornecer eletricidade à rede.",
                "https://www.iaea.org/topics/research-reactors",
                "Qual é uma finalidade de um reator de pesquisa?",
                "Produzir nêutrons para pesquisas e aplicações.",
                "Esses reatores também são usados na formação e no treinamento de profissionais."
        );
        cadastrar(
                "Irradiação de alimentos", Categoria.AGRICULTURA,
                "A irradiação pode reduzir microrganismos e pragas nos alimentos.",
                "A irradiação de alimentos utiliza radiação ionizante em condições controladas para reduzir "
                        + "microrganismos e combater pragas. Pode ajudar a retardar a deterioração e preservar a qualidade. "
                        + "O processo não substitui cuidados de higiene, armazenamento e preparo: o alimento ainda precisa ser manipulado corretamente.",
                "https://www.iaea.org/topics/food-irradiation",
                "A irradiação dispensa cuidados de higiene e preparo?",
                "Não. Os cuidados continuam necessários.",
                "A irradiação complementa as medidas de segurança dos alimentos e não substitui a manipulação adequada."
        );
    }

    private void cadastrar(String titulo, Categoria categoria, String resumo, String texto, String fonteUrl,
                           String enunciado, String alternativaCorreta, String explicacao) {
        Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(titulo);
        conteudo.setCategoria(categoria);
        conteudo.setResumo(resumo);
        conteudo.setTexto(texto);
        conteudo.setFonteUrl(fonteUrl);
        conteudoRepository.save(conteudo);

        Pergunta pergunta = new Pergunta();
        pergunta.setConteudo(conteudo);
        pergunta.setEnunciado(enunciado);
        pergunta.setAlternativaCorreta(alternativaCorreta);
        pergunta.setExplicacao(explicacao);
        perguntaRepository.save(pergunta);
    }
}
