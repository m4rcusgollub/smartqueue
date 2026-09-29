package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

/**
 * Ícones vetoriais de uma única família visual (traço suave em grade 24x24).
 * Renderizados diretamente pelo Java 2D com antialiasing subpixel.
 */
public enum Icone {

    PAINEL {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(3.5, 3.5, 7.4, 7.4, 2.4, 2.4));
            g.draw(new RoundRectangle2D.Double(13.1, 3.5, 7.4, 7.4, 2.4, 2.4));
            g.draw(new RoundRectangle2D.Double(3.5, 13.1, 7.4, 7.4, 2.4, 2.4));
            g.draw(new RoundRectangle2D.Double(13.1, 13.1, 7.4, 7.4, 2.4, 2.4));
        }
    },

    FILA {
        @Override
        void desenhar(Graphics2D g) {
            double[] linhas = {6.6, 12.0, 17.4};

            for (double linha : linhas) {
                g.fill(new Ellipse2D.Double(4.3, linha - 1.1, 2.2, 2.2));
                g.draw(new Line2D.Double(9.5, linha, 19.7, linha));
            }
        }
    },

    ATENDIMENTO {
        @Override
        void desenhar(Graphics2D g) {
            Path2D sino = new Path2D.Double();
            sino.moveTo(6.5, 16.3);
            sino.lineTo(6.5, 11.2);
            sino.curveTo(6.5, 7.7, 8.9, 5.5, 12.0, 5.5);
            sino.curveTo(15.1, 5.5, 17.5, 7.7, 17.5, 11.2);
            sino.lineTo(17.5, 16.3);

            g.draw(sino);
            g.draw(new Line2D.Double(4.5, 16.3, 19.5, 16.3));
            g.draw(new Arc2D.Double(9.7, 15.9, 4.6, 4.6, 180, -180, Arc2D.OPEN));
        }
    },

    TOTEM {
        @Override
        void desenhar(Graphics2D g) {
            // Totem / quiosque de autoatendimento com tela e ranhura de ticket
            g.draw(new RoundRectangle2D.Double(6.0, 3.0, 12.0, 18.0, 3.0, 3.0));
            g.draw(new RoundRectangle2D.Double(8.5, 5.5, 7.0, 5.0, 1.5, 1.5));
            g.draw(new Line2D.Double(9.0, 13.5, 15.0, 13.5));
            g.draw(new Line2D.Double(4.0, 21.0, 20.0, 21.0));
        }
    },

    TV {
        @Override
        void desenhar(Graphics2D g) {
            // Monitor de TV para sala de espera
            g.draw(new RoundRectangle2D.Double(3.0, 4.0, 18.0, 12.5, 2.5, 2.5));
            g.draw(new Line2D.Double(12.0, 16.5, 12.0, 19.5));
            g.draw(new Line2D.Double(7.5, 19.5, 16.5, 19.5));
            g.fill(new Ellipse2D.Double(11.0, 14.5, 2.0, 1.0));
        }
    },

    TICKET {
        @Override
        void desenhar(Graphics2D g) {
            // Bilhete / senha impressa
            Path2D p = new Path2D.Double();
            p.moveTo(4.0, 5.0);
            p.lineTo(20.0, 5.0);
            p.lineTo(20.0, 10.0);
            p.curveTo(18.0, 10.0, 18.0, 14.0, 20.0, 14.0);
            p.lineTo(20.0, 19.0);
            p.lineTo(4.0, 19.0);
            p.lineTo(4.0, 14.0);
            p.curveTo(6.0, 14.0, 6.0, 10.0, 4.0, 10.0);
            p.closePath();
            g.draw(p);
            g.draw(new Line2D.Double(9.0, 12.0, 15.0, 12.0));
        }
    },

    RECHAMAR {
        @Override
        void desenhar(Graphics2D g) {
            // Seta circular de repetição / recall
            g.draw(new Arc2D.Double(4.5, 4.5, 15.0, 15.0, 45, 270, Arc2D.OPEN));
            Path2D p = new Path2D.Double();
            p.moveTo(12.5, 2.5);
            p.lineTo(16.5, 6.0);
            p.lineTo(12.0, 7.5);
            g.draw(p);
        }
    },

    FINALIZAR {
        @Override
        void desenhar(Graphics2D g) {
            // Checkmark em círculo (concluir atendimento)
            g.draw(new Ellipse2D.Double(3.5, 3.5, 17.0, 17.0));
            Path2D p = new Path2D.Double();
            p.moveTo(8.0, 12.2);
            p.lineTo(11.0, 15.2);
            p.lineTo(16.2, 9.5);
            g.draw(p);
        }
    },

    PULAR {
        @Override
        void desenhar(Graphics2D g) {
            // Seta dupla de avanço / pular
            Path2D p1 = new Path2D.Double();
            p1.moveTo(6.0, 6.5);
            p1.lineTo(12.0, 12.0);
            p1.lineTo(6.0, 17.5);
            g.draw(p1);

            Path2D p2 = new Path2D.Double();
            p2.moveTo(12.5, 6.5);
            p2.lineTo(18.5, 12.0);
            p2.lineTo(12.5, 17.5);
            g.draw(p2);
        }
    },

    RELOGIO {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Ellipse2D.Double(3.5, 3.5, 17.0, 17.0));
            g.draw(new Line2D.Double(12.0, 7.5, 12.0, 12.0));
            g.draw(new Line2D.Double(12.0, 12.0, 15.5, 14.5));
        }
    },

    SOM {
        @Override
        void desenhar(Graphics2D g) {
            Path2D p = new Path2D.Double();
            p.moveTo(4.0, 9.5);
            p.lineTo(8.0, 9.5);
            p.lineTo(13.0, 5.5);
            p.lineTo(13.0, 18.5);
            p.lineTo(8.0, 14.5);
            p.lineTo(4.0, 14.5);
            p.closePath();
            g.draw(p);
            g.draw(new Arc2D.Double(14.0, 8.0, 6.0, 8.0, -60, 120, Arc2D.OPEN));
            g.draw(new Arc2D.Double(16.5, 5.0, 9.0, 14.0, -60, 120, Arc2D.OPEN));
        }
    },

    EXPANDIR {
        @Override
        void desenhar(Graphics2D g) {
            // Janela externa / popout
            g.draw(new RoundRectangle2D.Double(4.0, 8.0, 12.0, 12.0, 2.0, 2.0));
            g.draw(new Line2D.Double(11.0, 13.0, 20.0, 4.0));
            g.draw(new Line2D.Double(15.0, 4.0, 20.0, 4.0));
            g.draw(new Line2D.Double(20.0, 4.0, 20.0, 9.0));
        }
    },

    USUARIO {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Ellipse2D.Double(8.0, 4.5, 8.0, 8.0));
            Path2D p = new Path2D.Double();
            p.moveTo(4.5, 20.0);
            p.curveTo(4.5, 15.5, 7.8, 14.5, 12.0, 14.5);
            p.curveTo(16.2, 14.5, 19.5, 15.5, 19.5, 20.0);
            g.draw(p);
        }
    },

    ESTRELA {
        @Override
        void desenhar(Graphics2D g) {
            // Estrela de 5 pontas para prioridade
            Path2D p = new Path2D.Double();
            p.moveTo(12.0, 3.5);
            p.lineTo(14.6, 8.8);
            p.lineTo(20.5, 9.6);
            p.lineTo(16.2, 13.8);
            p.lineTo(17.2, 19.7);
            p.lineTo(12.0, 16.9);
            p.lineTo(6.8, 19.7);
            p.lineTo(7.8, 13.8);
            p.lineTo(3.5, 9.6);
            p.lineTo(9.4, 8.8);
            p.closePath();
            g.draw(p);
        }
    },

    RAIO {
        @Override
        void desenhar(Graphics2D g) {
            // Raio / Urgente
            Path2D p = new Path2D.Double();
            p.moveTo(13.5, 2.5);
            p.lineTo(6.5, 13.0);
            p.lineTo(12.0, 13.0);
            p.lineTo(10.5, 21.5);
            p.lineTo(17.5, 11.0);
            p.lineTo(12.0, 11.0);
            p.closePath();
            g.draw(p);
        }
    },

    CORACAO {
        @Override
        void desenhar(Graphics2D g) {
            Path2D p = new Path2D.Double();
            p.moveTo(12.0, 20.0);
            p.curveTo(5.0, 14.0, 3.0, 8.5, 6.5, 5.0);
            p.curveTo(9.5, 2.5, 11.5, 5.5, 12.0, 6.5);
            p.curveTo(12.5, 5.5, 14.5, 2.5, 17.5, 5.0);
            p.curveTo(21.0, 8.5, 19.0, 14.0, 12.0, 20.0);
            p.closePath();
            g.draw(p);
        }
    },

    HISTORICO {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Ellipse2D.Double(4.4, 4.6, 16.0, 16.0));
            g.draw(new Line2D.Double(12.4, 12.6, 12.4, 7.6));
            g.draw(new Line2D.Double(12.4, 12.6, 16.4, 14.9));
        }
    },

    CONFIGURACOES {
        @Override
        void desenhar(Graphics2D g) {
            double[] linhas = {7.2, 12.0, 16.8};
            double[] marcas = {15.2, 9.4, 13.6};

            for (int i = 0; i < linhas.length; i++) {
                g.draw(new Line2D.Double(4.2, linesY(i), 19.8, linesY(i)));
                g.fill(new Ellipse2D.Double(marcas[i] - 1.9, linesY(i) - 1.9, 3.8, 3.8));
            }
        }

        private double linesY(int i) {
            return i == 0 ? 7.2 : i == 1 ? 12.0 : 16.8;
        }
    },

    MAIS {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Line2D.Double(12.0, 5.8, 12.0, 18.2));
            g.draw(new Line2D.Double(5.8, 12.0, 18.2, 12.0));
        }
    },

    SETA {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Line2D.Double(4.4, 12.0, 18.4, 12.0));

            Path2D ponta = new Path2D.Double();
            ponta.moveTo(13.2, 7.0);
            ponta.lineTo(18.4, 12.0);
            ponta.lineTo(13.2, 17.0);

            g.draw(ponta);
        }
    },

    OK {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Ellipse2D.Double(3.9, 3.9, 16.2, 16.2));

            Path2D marca = new Path2D.Double();
            marca.moveTo(8.2, 12.4);
            marca.lineTo(11.0, 15.2);
            marca.lineTo(16.0, 9.4);

            g.draw(marca);
        }
    },

    ALERTA {
        @Override
        void desenhar(Graphics2D g) {
            Path2D triangulo = new Path2D.Double();
            triangulo.moveTo(12.0, 4.4);
            triangulo.lineTo(20.6, 19.2);
            triangulo.lineTo(3.4, 19.2);
            triangulo.closePath();

            g.draw(triangulo);
            g.draw(new Line2D.Double(12.0, 9.7, 12.0, 14.0));
            g.fill(new Ellipse2D.Double(11.0, 15.6, 2.0, 2.0));
        }
    },

    CAIXA_VAZIA {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new RoundRectangle2D.Double(3.4, 4.6, 17.2, 14.8, 3.0, 3.0));

            Path2D bandeja = new Path2D.Double();
            bandeja.moveTo(3.4, 13.4);
            bandeja.lineTo(8.6, 13.4);
            bandeja.curveTo(9.2, 15.9, 10.4, 16.6, 12.0, 16.6);
            bandeja.curveTo(13.6, 16.6, 14.8, 15.9, 15.4, 13.4);
            bandeja.lineTo(20.6, 13.4);

            g.draw(bandeja);
        }
    },

    FECHAR {
        @Override
        void desenhar(Graphics2D g) {
            g.draw(new Line2D.Double(6.9, 6.9, 17.1, 17.1));
            g.draw(new Line2D.Double(17.1, 6.9, 6.9, 17.1));
        }
    },

    MENU {
        @Override
        void desenhar(Graphics2D g) {
            double[] linhas = {7.0, 12.0, 17.0};

            for (double linha : linhas) {
                g.draw(new Line2D.Double(4.4, linha, 19.6, linha));
            }
        }
    };

    private static final double GRADE = 24d;
    private static final float ESPESSURA = 1.8f;

    abstract void desenhar(Graphics2D g);

    public void pintar(Graphics2D g, int tamanho, Color cor) {
        Graphics2D g2 = Pintura.preparar(g);
        g2.scale(tamanho / GRADE, tamanho / GRADE);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(ESPESSURA, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        desenhar(g2);
        g2.dispose();
    }

    public Icon comoIcone(int tamanho, Color cor) {
        return new IconePintado(this, tamanho, cor);
    }

    private record IconePintado(Icone icone, int tamanho, Color cor) implements Icon {

        @Override
        public void paintIcon(Component componente, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create(x, y, tamanho, tamanho);
            icone.pintar(g2, tamanho, cor);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return tamanho;
        }

        @Override
        public int getIconHeight() {
            return tamanho;
        }
    }
}
