package br.com.fiap.nuclear.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 500)
    private String resumo;

    @Column(nullable = false, length = 5000)
    private String texto;

    @Column(nullable = false, length = 1000)
    private String fonteUrl;

    @OneToMany(mappedBy = "conteudo")
    private List<Pergunta> perguntas = new ArrayList<>();

    public Conteudo() {
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getFonteUrl() {
        return fonteUrl;
    }

    public void setFonteUrl(String fonteUrl) {
        this.fonteUrl = fonteUrl;
    }

    public List<Pergunta> getPerguntas() {
        return perguntas;
    }
}
