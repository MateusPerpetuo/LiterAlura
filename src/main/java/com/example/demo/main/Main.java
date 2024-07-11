package com.example.demo.main;

import com.example.demo.model.Autor;
import com.example.demo.model.Livro;
import com.example.demo.model.records.DadosLivro;
import com.example.demo.repository.AutorRepository;
import com.example.demo.repository.LivroRepositoy;
import com.example.demo.service.ConsumoAPI;
import com.example.demo.service.Conversor;
import com.example.demo.model.records.DadosResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private Scanner scan = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String ENDERECO = "https://gutendex.com/books/?search=";
    private Conversor conversor = new Conversor();
    private LivroRepositoy repositorio;
    private AutorRepository autorRepository;

    public Main (){}

    public Main(LivroRepositoy repositorio, AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
        this.repositorio = repositorio;
    }

    public void showMenu(){
        var opcao = -1;
        while (opcao != 0) {

            var start = """
                    \n====================================================
                                 BEM-VINDO À LITERALURA!
                    ====================================================
                    """;

            var menu = """
                    Selecione uma opção
                    
                    1 - Cadastrar um novo livro
                    2 - Listar livros cadastrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros de um determinado idioma
                    \n0 - Sair
                    """;

            System.out.println(start);
            System.out.println(menu);
            opcao = scan.nextInt();
            scan.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("""
                            Opção selecionada => 1) Cadastrar um novo Livro
                            """);
                    buscarLivroPeloTitulo();
                    break;

                case 2:
                    System.out.println("""
                            Opção selecionada => 2) Listar livros cadastrados
                            """);
                    listarLivrosCadastrados();
                    break;

                case 3:
                    System.out.println("""
                             Opção selecionada => 3) Listar autores registrados
                            """);
                    listarAutoresRegistrados();
                    break;

                case 4:
                    System.out.println("""
                            Opção selecionada => 4) Listar autores vivos em um determinado ano
                            """);
                    autoresVivos();
                    break;

                case 5:
                    System.out.println("""
                            Opção selecionada => 5) Listar livro em um determinado idioma
                            """);
                    listarLivroIdioma();
                    break;

                case 0:
                    opcao = 0;
                    break;

                default:
                    System.out.println("Opção invalida. Selecione uma opção valida");
                    break;
            }
        }
    }

    public void buscarLivroPeloTitulo() {

        System.out.println("Informe um título: ");
        var titulo = scan.nextLine().toLowerCase().replace(" ", "%20");

        boolean livroCadastrado =  this.verificaLivroCadastrado(titulo.replace("%20", " "));

        if(!livroCadastrado) {
            String json = consumoAPI.resultadoApi(ENDERECO + titulo);
            var dadosLivro = conversor.obterDados(json, DadosResult.class);

            if(dadosLivro.livros().isEmpty()) {
                System.out.println("Livro não encontrado");
            } else {
                this.verificaAutorCadastrado(dadosLivro.livros().get(0));
            }


        }
    }

    public Boolean verificaLivroCadastrado(String titulo) {
        var tituloJaCadastrado = repositorio.existsByTitulo(titulo);
//
        if(tituloJaCadastrado) {
            var livroDb = repositorio.findByTitulo(titulo);
            System.out.println("LIVRO JÁ CADASTRADO");
            System.out.println(livroDb);
            return true;
        }

        return false;

    }

    public void verificaAutorCadastrado(DadosLivro dadosLivro) {
        System.out.println(dadosLivro.autores().get(0).nome());
        Autor autor = autorRepository.findByNome(dadosLivro.autores().get(0).nome().toLowerCase());


        if(autor == null) {
            System.out.println("O autor não existe: " + dadosLivro.autores().get(0).nome());

            Autor autor3 = new Autor(dadosLivro.autores().get(0));

            Livro livro2 = new Livro(dadosLivro);
            livro2.setAutor(autor3);
            repositorio.save(livro2);

            System.out.println("Livro encontrado e salvo no banco de dados!");
            System.out.println(livro2);
        } else {
            Livro livro = new Livro(dadosLivro);
            livro.setAutor(autor);
            List<Livro> listaLivros = new ArrayList<>();
            listaLivros.add(livro);

            System.out.println("O autor já existe: " + autor.getNome());
            Autor autor1 = new Autor(autor.getNome(), autor.getAnoNascimento(), autor.getAnoMorte(), autor.getId());
            autor1.setLivro(listaLivros);
            autorRepository.save(autor1);

            System.out.println("Livro encontrado e salvo no banco de dados!");
            System.out.println(livro);
        }
    }

    private void listarLivrosCadastrados() {
        List<Livro> livros = repositorio.findAll();

        if(livros.isEmpty()) {
            System.out.println("Não há livros cadastrados no banco de dados");
        } else {
            System.out.println("""
                    ------------------------------------------------
                                LIVROS CADASTRADOS
                    ------------------------------------------------
                    """);
            livros.forEach(System.out::println);
        }


    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if(autores.isEmpty()) {
            System.out.println("Não há autores cadastrados no banco de dados");
        } else {
            System.out.println("""
                    ------------------------------------------------
                                AUTORES CADASTRADOS
                    ------------------------------------------------
                    """);
            autores.forEach(System.out::println);
        }
    }

    public void autoresVivos() {
        System.out.println("Digite o ano: ");
        var anoInput = scan.nextLine();

        try {
            Integer ano = Integer.valueOf(anoInput);
            List<Autor> autores = autorRepository.findAutoresVivo(ano);
            if(autores.isEmpty()) {
                System.out.println("Autor não encontrado.");
            } else {
                System.out.println("\nAutores");
                autores.forEach(System.out::println);
            }
        } catch (NumberFormatException n) {
            System.out.println("O valor informado é inválido => [" + n.getMessage() + "]");
        }

    }

    public void listarLivroIdioma() {
        System.out.println("""
                Insira o idioma para reaizar a busca:
                es - espanhou
                en - inglês
                fr - francês
                pt - portugês
                """);
        var idioma = scan.nextLine();

        List<Livro> livros = repositorio.findIdiomaLivros(idioma);

        if(livros.isEmpty()) {
            System.out.println("Nenhum Livro encontrado para o idioma informado");
        } else {
            livros.forEach(System.out::println);
        }
    }

}