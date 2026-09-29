package service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Consumidor;
import model.FilaPrioridade;
import model.Prioridade;

/**
 * Camada de serviço usada pela interface: expõe a fila e o histórico de
 * atendimentos sem reimplementar a regra de prioridade, que permanece em
 * FilaPrioridade (PriorityQueue + Comparator).
 */
public class FilaService {

    private final FilaPrioridade fila = new FilaPrioridade();
    private final List<Atendimento> historico = new ArrayList<>();
    private final Map<Integer, LocalDateTime> entradaPorId = new HashMap<>();
    private final List<Runnable> observadores = new ArrayList<>();

    private Atendimento atendimentoAtual;
    private String guicheAtual = "Guichê 01";
    private long ultimaChamadaTimestamp = 0;

    public Consumidor adicionar(String nome, Prioridade prioridade) {
        Consumidor consumidor = new Consumidor(nome, prioridade);

        entradaPorId.put(consumidor.getId(), LocalDateTime.now());
        fila.adicionar(consumidor);
        notificar();

        return consumidor;
    }

    public Consumidor chamarProximo() {
        return chamarProximo(guicheAtual);
    }

    public Consumidor chamarProximo(String guiche) {
        Consumidor consumidor = fila.proximo();

        if (consumidor == null) {
            return null;
        }

        String guicheEfetivo = (guiche != null && !guiche.isBlank()) ? guiche : guicheAtual;
        entradaPorId.remove(consumidor.getId());

        atendimentoAtual = new Atendimento(consumidor, LocalDateTime.now(), guicheEfetivo, "Em Atendimento");
        historico.add(0, atendimentoAtual);
        ultimaChamadaTimestamp = System.currentTimeMillis();
        notificar();

        return consumidor;
    }

    public void rechamar() {
        if (atendimentoAtual != null) {
            ultimaChamadaTimestamp = System.currentTimeMillis();
            notificar();
        }
    }

    public void finalizarAtendimento() {
        if (atendimentoAtual != null) {
            int indice = historico.indexOf(atendimentoAtual);
            Atendimento concluido = new Atendimento(
                    atendimentoAtual.getConsumidor(),
                    atendimentoAtual.getHorario(),
                    atendimentoAtual.getGuiche(),
                    "Concluído"
            );
            if (indice >= 0) {
                historico.set(indice, concluido);
            }
            atendimentoAtual = null;
            notificar();
        }
    }

    public void pularAtual() {
        if (atendimentoAtual != null) {
            int indice = historico.indexOf(atendimentoAtual);
            Atendimento pulado = new Atendimento(
                    atendimentoAtual.getConsumidor(),
                    atendimentoAtual.getHorario(),
                    atendimentoAtual.getGuiche(),
                    "Não Compareceu"
            );
            if (indice >= 0) {
                historico.set(indice, pulado);
            }
            atendimentoAtual = null;
            notificar();
        }
    }

    public Atendimento getAtendimentoAtual() {
        return atendimentoAtual;
    }

    public String getGuicheAtual() {
        return guicheAtual;
    }

    public void setGuicheAtual(String guiche) {
        if (guiche != null && !guiche.isBlank()) {
            this.guicheAtual = guiche;
            notificar();
        }
    }

    public long getUltimaChamadaTimestamp() {
        return ultimaChamadaTimestamp;
    }

    public List<Consumidor> aguardando() {
        return fila.listar();
    }

    public Consumidor proximo() {
        List<Consumidor> aguardando = fila.listar();

        return aguardando.isEmpty() ? null : aguardando.get(0);
    }

    public List<Atendimento> historico() {
        return List.copyOf(historico);
    }

    public Atendimento ultimoAtendimento() {
        return historico.isEmpty() ? null : historico.get(0);
    }

    public boolean filaVazia() {
        return fila.estaVazia();
    }

    public int totalAguardando() {
        return fila.tamanho();
    }

    public int totalAtendimentos() {
        return historico.size();
    }

    public Map<Prioridade, Integer> distribuicaoAguardando() {
        Map<Prioridade, Integer> distribuicao = new EnumMap<>(Prioridade.class);

        for (Prioridade prioridade : Prioridade.values()) {
            distribuicao.put(prioridade, 0);
        }

        for (Consumidor consumidor : fila.listar()) {
            distribuicao.merge(consumidor.getPrioridade(), 1, Integer::sum);
        }

        return distribuicao;
    }

    public long minutosDeEspera(Consumidor consumidor) {
        LocalDateTime entrada = entradaPorId.get(consumidor.getId());

        if (entrada == null) {
            return 0;
        }

        return Duration.between(entrada, LocalDateTime.now()).toMinutes();
    }

    public int tempoMedioEsperaMinutos() {
        List<Consumidor> aguardando = fila.listar();
        if (aguardando.isEmpty()) {
            return 0;
        }

        long soma = 0;
        for (Consumidor c : aguardando) {
            soma += minutosDeEspera(c);
        }
        return (int) Math.max(1, Math.round((double) soma / aguardando.size()));
    }

    public void observar(Runnable observador) {
        observadores.add(observador);
    }

    private void notificar() {
        for (Runnable observador : observadores) {
            observador.run();
        }
    }
}
