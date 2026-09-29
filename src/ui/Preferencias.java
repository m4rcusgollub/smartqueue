package ui;

/**
 * Preferências da interface mantidas durante a sessão.
 */
public class Preferencias {

    private Tema tema = Tema.CLARO;
    private boolean confirmarAoChamar;
    private boolean alertarUrgente = true;
    private boolean limparNomeAoAdicionar = true;

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public boolean isConfirmarAoChamar() {
        return confirmarAoChamar;
    }

    public void setConfirmarAoChamar(boolean confirmarAoChamar) {
        this.confirmarAoChamar = confirmarAoChamar;
    }

    public boolean isAlertarUrgente() {
        return alertarUrgente;
    }

    public void setAlertarUrgente(boolean alertarUrgente) {
        this.alertarUrgente = alertarUrgente;
    }

    public boolean isLimparNomeAoAdicionar() {
        return limparNomeAoAdicionar;
    }

    public void setLimparNomeAoAdicionar(boolean limparNomeAoAdicionar) {
        this.limparNomeAoAdicionar = limparNomeAoAdicionar;
    }
}
