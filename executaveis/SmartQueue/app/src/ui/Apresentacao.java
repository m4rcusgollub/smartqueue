package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import model.Prioridade;

/**
 * Formatação dos dados do domínio para exibição (não altera nenhuma regra de negócio).
 */
public final class Apresentacao {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Apresentacao() {
    }

    public static String id(int id) {
        return String.format("#%03d", id);
    }

    public static String prioridade(Prioridade prioridade) {
        return switch (prioridade) {
            case BAIXA -> "Baixa";
            case NORMAL -> "Normal";
            case PRIORIDADE -> "Prioridade";
            case URGENTE -> "Urgente";
        };
    }

    public static String prioridadeDestacada(Prioridade prioridade) {
        return prioridade(prioridade).toUpperCase(Locale.ROOT);
    }

    public static String hora(LocalDateTime momento) {
        return HORA.format(momento);
    }

    public static String data(LocalDateTime momento) {
        return DATA.format(momento);
    }

    public static String dataHora(LocalDateTime momento) {
        return DATA.format(momento) + " às " + HORA.format(momento);
    }

    public static String espera(long minutos) {
        if (minutos < 1) {
            return "menos de 1 min";
        }

        if (minutos < 60) {
            return minutos + " min";
        }

        long horas = minutos / 60;
        long restantes = minutos % 60;

        return restantes == 0
                ? horas + " h"
                : horas + " h " + String.format("%02d", restantes) + " min";
    }

    /** Versão curta usada nas colunas da tabela, onde o espaço é limitado. */
    public static String esperaCurta(long minutos) {
        if (minutos < 1) {
            return "<1 min";
        }

        if (minutos < 60) {
            return minutos + " min";
        }

        long horas = minutos / 60;
        long restantes = minutos % 60;

        return restantes == 0 ? horas + " h" : horas + " h " + String.format("%02d", restantes);
    }
}
