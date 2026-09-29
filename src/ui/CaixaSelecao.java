package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 * Caixa de seleção quadrada (raio 4) usada nas preferências do sistema.
 */
public class CaixaSelecao extends JCheckBox {

    private static final int LADO = 16;

    public CaixaSelecao(String texto, boolean selecionado) {
        super(texto, selecionado);

        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFont(Estilo.fonteRegular(13f));
        setForeground(Estilo.paleta().texto);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIconTextGap(10);
        setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        setIcon(new Marca(false));
        setSelectedIcon(new Marca(true));
    }

    private final class Marca implements Icon {

        private final boolean marcada;

        private Marca(boolean marcada) {
            this.marcada = marcada;
        }

        @Override
        public void paintIcon(Component componente, Graphics g, int x, int y) {
            Graphics2D g2 = Pintura.preparar(g);
            Paleta paleta = Estilo.paleta();
            boolean sobre = getModel().isRollover();

            Color fundo = marcada ? paleta.primaria : paleta.superficie;
            Color borda = marcada
                    ? paleta.primaria
                    : Cores.misturar(paleta.borda, paleta.texto, sobre ? 0.30f : 0.16f);

            Pintura.caixa(g2, x + 0.5, y + 0.5, LADO - 1, LADO - 1, 4, fundo, borda);

            if (marcada) {
                Path2D marca = new Path2D.Double();
                marca.moveTo(x + 4.4, y + 8.3);
                marca.lineTo(x + 7.0, y + 10.9);
                marca.lineTo(x + 11.7, y + 5.6);

                g2.setColor(paleta.sobrePrimaria);
                g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(marca);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return LADO;
        }

        @Override
        public int getIconHeight() {
            return LADO;
        }
    }
}
