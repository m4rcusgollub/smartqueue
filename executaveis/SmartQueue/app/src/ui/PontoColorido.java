package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.Icon;

/** Ponto colorido usado como indicador de prioridade ou de situação. */
public class PontoColorido implements Icon {

    private final Color cor;
    private final int diametro;

    public PontoColorido(Color cor, int diametro) {
        this.cor = cor;
        this.diametro = diametro;
    }

    @Override
    public void paintIcon(Component componente, Graphics g, int x, int y) {
        Graphics2D g2 = Pintura.preparar(g);
        g2.setColor(cor);
        g2.fill(new Ellipse2D.Double(x, y + (getIconHeight() - diametro) / 2.0, diametro, diametro));
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return diametro;
    }

    @Override
    public int getIconHeight() {
        return diametro + 3;
    }
}
