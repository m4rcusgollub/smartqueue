package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public final class Pintura {

    private Pintura() {
    }

    public static Graphics2D preparar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    public static void preencher(Graphics2D g2, double x, double y, double largura, double altura,
                                 double raio, Color cor) {
        g2.setColor(cor);
        g2.fill(forma(x, y, largura, altura, raio));
    }

    public static void contornar(Graphics2D g2, double x, double y, double largura, double altura,
                                 double raio, Color cor, float espessura) {
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(espessura));
        g2.draw(forma(x + espessura / 2d, y + espessura / 2d,
                largura - espessura, altura - espessura, raio));
    }

    public static void caixa(Graphics2D g2, double x, double y, double largura, double altura,
                             double raio, Color fundo, Color borda) {
        preencher(g2, x, y, largura, altura, raio, fundo);
        contornar(g2, x, y, largura, altura, raio, borda, 1f);
    }

    /**
     * Elevação sutil: camadas translúcidas com deslocamento crescente para
     * baixo, simulando uma sombra leve sem blur. Usada apenas para dar
     * profundidade discreta às superfícies, nunca como efeito decorativo.
     */
    public static void sombra(Graphics2D g2, double x, double y, double largura, double altura,
                              double raio, Color cor) {
        if (cor.getAlpha() == 0) {
            return;
        }

        double[] deslocamento = {2.5, 1.6, 0.8};
        double[] intensidade = {0.14, 0.22, 0.32};

        for (int camada = 0; camada < deslocamento.length; camada++) {
            int alfa = (int) Math.round(cor.getAlpha() * intensidade[camada]);
            Color tom = new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), alfa);

            preencher(g2, x + 0.6, y + deslocamento[camada], largura - 1.2,
                    altura - deslocamento[camada], raio, tom);
        }
    }

    private static RoundRectangle2D forma(double x, double y, double largura, double altura,
                                          double raio) {
        return new RoundRectangle2D.Double(x, y, largura, altura, raio * 2d, raio * 2d);
    }
}
