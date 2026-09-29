package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Controle segmentado de escolha única (usado para a preferência de tema).
 */
public class SeletorSegmentado extends JPanel {

    private final List<Segmento> segmentos = new ArrayList<>();
    private IntConsumer aoAlterar;
    private int selecionado;

    public SeletorSegmentado(String[] rotulos, int indiceInicial) {
        setOpaque(false);
        setLayout(new GridLayout(1, rotulos.length, 8, 0));

        ButtonGroup grupo = new ButtonGroup();

        for (int i = 0; i < rotulos.length; i++) {
            Segmento segmento = new Segmento(rotulos[i], i);
            segmento.setSelected(i == indiceInicial);
            grupo.add(segmento);
            segmentos.add(segmento);
            add(segmento);
        }

        selecionado = indiceInicial;
    }

    public void aoAlterar(IntConsumer observador) {
        aoAlterar = observador;
    }

    private void selecionarSegmento(int indice) {
        if (indice == selecionado) {
            return;
        }

        selecionado = indice;

        if (aoAlterar != null) {
            aoAlterar.accept(indice);
        }
    }

    private final class Segmento extends JToggleButton {

        private final int indice;
        private boolean sobre;

        private Segmento(String rotulo, int indice) {
            super(rotulo);
            this.indice = indice;

            setFont(Estilo.fonteForte(12.5f));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(88, Estilo.ALTURA_CAMPO));

            addActionListener(evento -> selecionarSegmento(indice));
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);
            Paleta paleta = Estilo.paleta();
            boolean ativo = isSelected();

            Color fundo = ativo ? paleta.primariaSuave
                    : sobre ? paleta.superficieAlt : paleta.superficie;
            Color borda = ativo ? Cores.comAlfa(paleta.primaria, 120) : paleta.borda;

            Pintura.caixa(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO, fundo, borda);

            if (isFocusOwner()) {
                Pintura.contornar(g2, 2.5, 2.5, getWidth() - 5, getHeight() - 5, Estilo.RAIO - 1,
                        Cores.comAlfa(paleta.primaria, 90), 1.4f);
            }

            g2.dispose();

            setForeground(ativo ? paleta.primariaTexto : paleta.textoSecundario);
            super.paintComponent(g);
        }
    }
}
