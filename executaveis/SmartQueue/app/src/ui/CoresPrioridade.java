package ui;

import java.awt.Color;

/**
 * Cores de sinalização de prioridade: somente o par texto/fundo é definido,
 * a borda é derivada para manter a escala discreta em qualquer tema.
 */
public final class CoresPrioridade {

    private final Color texto;
    private final Color fundo;
    private final Color borda;

    public CoresPrioridade(Color texto, Color fundo) {
        this.texto = texto;
        this.fundo = fundo;
        this.borda = Cores.misturar(fundo, texto, 0.30f);
    }

    public Color getTexto() {
        return texto;
    }

    public Color getFundo() {
        return fundo;
    }

    public Color getBorda() {
        return borda;
    }
}
