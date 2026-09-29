package ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Superfície padrão da aplicação: elevação discreta (sombra leve), borda de
 * 1px e cantos discretos. Usada quando existe um motivo funcional para agrupar
 * conteúdo.
 */
public class PainelSuperficie extends JPanel {

    public PainelSuperficie() {
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);
        Paleta paleta = Estilo.paleta();

        Pintura.sombra(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO_PAINEL,
                paleta.sombra);
        Pintura.caixa(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO_PAINEL,
                paleta.superficie, paleta.borda);

        g2.dispose();
    }
}
