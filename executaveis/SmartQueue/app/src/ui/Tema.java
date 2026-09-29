package ui;

public enum Tema {

    CLARO("Claro"),
    ESCURO("Escuro");

    private final String rotulo;

    Tema(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
