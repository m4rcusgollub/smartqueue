package model;

public class Consumidor {

    private int id;
    private String nome;
    private Prioridade prioridade;

    private static int count = 1;

    public Consumidor(String nome, Prioridade prioridade) {
        this.id = count++;
        this.nome = nome;
        this.prioridade = prioridade;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }
}