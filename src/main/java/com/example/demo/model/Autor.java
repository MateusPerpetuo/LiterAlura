package com.example.demo.model;

import com.example.demo.model.records.DadosAutor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    private Integer anoNascimento;

    private Integer anoMorte;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Livro> livro = new ArrayList<>();

    public Autor() {}

    public Autor(String nome, Integer anoNascimento, Integer anoMorte, Long id){
        this.nome = nome;
        this.anoNascimento = anoNascimento;
        this.anoMorte = anoMorte;
        this.id = id;
    }

    public Autor(DadosAutor dadosAutor) {
        this.nome = dadosAutor.nome().toLowerCase();
        this.anoNascimento = dadosAutor.anoNascimento();
        this.anoMorte = dadosAutor.anoMorte();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnoNascimento() {
        return anoNascimento;
    }

    public void setAnoNascimento(Integer anoNascimento) {
        this.anoNascimento = anoNascimento;
    }

    public Integer getAnoMorte() {
        return anoMorte;
    }

    public void setAnoMorte(Integer anoMorte) {
        this.anoMorte = anoMorte;
    }

    public List<Livro> getLivro() {
        return livro;
    }

    public void setLivro(List<Livro> livro) {
        this.livro.forEach( l -> {
            l.setAutor(this);
        });
        this.livro = livro;
    }

    @Override
    public String toString() {
        return  "\n----------------------------" +
                "\nAutor: " + nome +
                ", Ano de Nascimento: " + anoNascimento +
                ", Ano de Falecimento: " + anoMorte +
                "\nLivros :" + livro.stream().map(Livro::getTitulo)
                                        .collect(Collectors.toList()) +
                "\n----------------------------";
    }
}
