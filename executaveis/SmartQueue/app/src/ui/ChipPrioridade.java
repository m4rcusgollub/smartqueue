package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import model.Prioridade;

/**
 * Etiqueta de prioridade. Usada nas tabelas e nos painéis de destaque,
 * mantendo a mesma leitura de cor em toda a aplicação.
 */
public class ChipPrioridade extends JComponent {

    private static final int PADDING = 11;
    private static final int PONTO = 7;
    private static final int ESPACO = 8;

    private final Prioridade prioridade;
    private final boolean grande;
    private Color corDeFundoLinha;

    public ChipPrioridade(Prioridade prioridade, boolean grande) {
        this.prioridade = prioridade;
        this.grande = grande;

        setOpaque(false);
        setFocusable(false);
    }

    public ChipPrioridade(Prioridade prioridade) {
        this(prioridade, false);
    }

    public void definirFundoLinha(Color cor) {
        corDeFundoLinha = cor;
    }

    private Font fonteDoChip() {
        return grande ? Estilo.fonteChip(12f) : Estilo.fonteChip(11f);
    }

    private int alturaDoChip() {
        return grande ? 26 : 22;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics medidas = getFontMetrics(fonteDoChip());
        int largura = PADDING * 2 + PONTO + ESPACO
                + medidas.stringWidth(Apresentacao.prioridadeDestacada(prioridade));

        return new Dimension(largura, alturaDoChip());
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);

        if (corDeFundoLinha != null) {
            g2.setColor(corDeFundoLinha);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        Paleta paleta = Estilo.paleta();
        CoresPrioridade cores = paleta.prioridade(prioridade);
        String texto = Apresentacao.prioridadeDestacada(prioridade);

        g2.setFont(fonteDoChip());

        FontMetrics medidas = g2.getFontMetrics();
        int larguraChip = getPreferredSize().width;
        int alturaChip = alturaDoChip();
        int x = PADDING;
        int y = (getHeight() - alturaChip) / 2;

        Pintura.caixa(g2, x + 0.5, y + 0.5, larguraChip - 1, alturaChip - 1, Estilo.RAIO_CHIP,
                cores.getFundo(), cores.getBorda());

        g2.setColor(cores.getTexto());
        g2.fill(new Ellipse2D.Double(x + PADDING, y + alturaChip / 2.0 - PONTO / 2.0,
                PONTO, PONTO));

        int baseDoTexto = y + (alturaChip + medidas.getAscent() - medidas.getDescent()) / 2;
        g2.drawString(texto, x + PADDING + PONTO + ESPACO, baseDoTexto);

        g2.dispose();
    }
}
