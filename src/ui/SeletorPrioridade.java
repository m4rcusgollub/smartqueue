package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import model.Prioridade;

/**
 * Escolha de prioridade em quatro opções fixas, cada uma com a cor de
 * sinalização correspondente. O texto da regra vem da própria ordem do enum.
 */
public class SeletorPrioridade extends JPanel {

    private final Map<Prioridade, Segmento> segmentos = new EnumMap<>(Prioridade.class);

    private Prioridade selecionada = Prioridade.NORMAL;
    private Runnable aoAlterar;

    public SeletorPrioridade() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel rotulo = Estilo.rotulo("Prioridade");
        rotulo.setAlignmentX(LEFT_ALIGNMENT);
        add(rotulo);
        add(Estilo.espaco(6));

        JPanel grade = Estilo.painel();
        grade.setLayout(new GridLayout(2, 2, 8, 8));
        grade.setAlignmentX(LEFT_ALIGNMENT);
        grade.setMaximumSize(new Dimension(Integer.MAX_VALUE, Estilo.ALTURA_CAMPO * 2 + 8));

        ButtonGroup grupo = new ButtonGroup();

        for (Prioridade prioridade : Prioridade.values()) {
            Segmento segmento = new Segmento(prioridade);
            segmento.setSelected(prioridade == selecionada);
            segmento.addActionListener(evento -> definirSelecionada(prioridade));
            grupo.add(segmento);
            segmentos.put(prioridade, segmento);
            grade.add(segmento);
        }

        add(grade);

        JLabel dica = Estilo.suave("Urgente é chamado antes de Prioridade, Normal e Baixa.");
        dica.setAlignmentX(LEFT_ALIGNMENT);
        dica.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        add(dica);
    }

    public Prioridade selecionada() {
        return selecionada;
    }

    public void selecionar(Prioridade prioridade) {
        segmentos.get(prioridade).setSelected(true);
        definirSelecionada(prioridade);
    }

    public void aoAlterar(Runnable observador) {
        aoAlterar = observador;
    }

    private void definirSelecionada(Prioridade prioridade) {
        if (prioridade == selecionada) {
            return;
        }

        selecionada = prioridade;

        if (aoAlterar != null) {
            aoAlterar.run();
        }
    }

    private final class Segmento extends JToggleButton {

        private final Prioridade prioridade;
        private boolean sobre;

        private Segmento(Prioridade prioridade) {
            super(Apresentacao.prioridade(prioridade));
            this.prioridade = prioridade;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, Estilo.ALTURA_CAMPO));

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
            CoresPrioridade cores = paleta.prioridade(prioridade);
            boolean ativo = isSelected();

            Color fundo = ativo ? cores.getFundo()
                    : sobre ? paleta.superficieAlt : paleta.superficie;
            Color borda = ativo
                    ? Cores.misturar(cores.getBorda(), cores.getTexto(), 0.18f)
                    : paleta.borda;

            Pintura.caixa(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO, fundo, borda);

            if (isFocusOwner()) {
                Pintura.contornar(g2, 2.5, 2.5, getWidth() - 5, getHeight() - 5, Estilo.RAIO - 1,
                        Cores.comAlfa(paleta.primaria, 90), 1.4f);
            }

            g2.setFont(Estilo.fonteForte(12.5f));

            FontMetrics medidas = g2.getFontMetrics();
            String texto = getText();
            int ponto = 7;
            int espaco = 8;
            int larguraTotal = ponto + espaco + medidas.stringWidth(texto);
            int inicio = (getWidth() - larguraTotal) / 2;
            int meio = getHeight() / 2;
            Color corDoTexto = ativo ? cores.getTexto() : paleta.textoSecundario;

            g2.setColor(ativo ? cores.getTexto() : paleta.textoSuave);
            g2.fill(new Ellipse2D.Double(inicio, meio - ponto / 2.0, ponto, ponto));

            g2.setColor(corDoTexto);
            g2.drawString(texto, inicio + ponto + espaco,
                    meio + (medidas.getAscent() - medidas.getDescent()) / 2);

            g2.dispose();
        }
    }
}
