package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/**
 * Botão apenas com ícone (fechar avisos, recolher a barra lateral).
 * O ícone sempre acompanha uma dica de texto acessível.
 */
public class BotaoIcone extends JButton {

    private final Icone icone;
    private final int tamanho;
    private Color cor;
    private boolean sobre;

    public BotaoIcone(Icone icone, String dica, int tamanho) {
        this.icone = icone;
        this.tamanho = tamanho;
        this.cor = Estilo.paleta().textoSuave;

        setToolTipText(dica);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        instalarFeedback();
    }

    public void definirCor(Color novaCor) {
        cor = novaCor;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(tamanho + 10, tamanho + 10);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);

        if (sobre) {
            Pintura.preencher(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO,
                    Estilo.paleta().superficieAlt);
        }

        g2.translate((getWidth() - tamanho) / 2, (getHeight() - tamanho) / 2);
        icone.pintar(g2, tamanho, cor);
        g2.dispose();
    }

    private void instalarFeedback() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent evento) {
                sobre = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                sobre = false;
                repaint();
            }
        });
    }
}
