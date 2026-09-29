package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class FilaPrioridade {

    private final Comparator<Consumidor> comparador = (a, b) ->
            b.getPrioridade().ordinal() - a.getPrioridade().ordinal();

    private final Queue<Consumidor> fila =
            new PriorityQueue<>(comparador);

    public void adicionar(Consumidor consumidor) {
        fila.add(consumidor);
    }

    public Consumidor proximo() {
        return fila.poll();
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }

    public int tamanho() {
        return fila.size();
    }

    /**
     * Devolve os consumidores em ordem de atendimento sem alterar a fila.
     * A cópia usa o mesmo Comparator da PriorityQueue original, então a ordem
     * exibida na interface é a mesma ordem em que os consumidores serão chamados.
     */
    public List<Consumidor> listar() {
        Queue<Consumidor> copia = new PriorityQueue<>(comparador);
        copia.addAll(fila);

        List<Consumidor> ordenados = new ArrayList<>(copia.size());

        while (!copia.isEmpty()) {
            ordenados.add(copia.poll());
        }

        return ordenados;
    }
}