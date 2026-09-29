package service;

import java.time.LocalDateTime;

import model.Consumidor;

public class Atendimento {

    private final Consumidor consumidor;
    private final LocalDateTime horario;
    private final String guiche;
    private final String status;

    public Atendimento(Consumidor consumidor, LocalDateTime horario) {
        this(consumidor, horario, "Guichê 01", "Concluído");
    }

    public Atendimento(Consumidor consumidor, LocalDateTime horario, String guiche, String status) {
        this.consumidor = consumidor;
        this.horario = horario;
        this.guiche = guiche != null ? guiche : "Guichê 01";
        this.status = status != null ? status : "Concluído";
    }

    public Consumidor getConsumidor() {
        return consumidor;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public String getGuiche() {
        return guiche;
    }

    public String getStatus() {
        return status;
    }
}
