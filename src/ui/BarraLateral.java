package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Navegação principal. Em janelas estreitas a barra recolhe para uma faixa
 * apenas com ícones; a marca da aplicação alterna entre os dois estados.
 */
public class BarraLateral extends JPanel {

    private static final int PADDING = 10;

    private final Map<Pagina, ItemNavegacao> itens = new EnumMap<>(Pagina.class);
    private final Consumer<Pagina> aoSelecionar;
    private final Runnable aoAlternarRecolhimento;
    private final JLabel nomeDaMarca = new JLabel("Smart Queue");
    private final JLabel rodape = new JLabel("Versão 1.0.0 · Dados em memória");
    private final Marca marca = new Marca();

    private Pagina atual;
    private boolean compacta;

    public BarraLateral(Consumer<Pagina> aoSelecionar, Runnable aoAlternarRecolhimento) {
        this.aoSelecionar = aoSelecionar;
        this.aoAlternarRecolhimento = aoAlternarRecolhimento;

        setLayout(new BorderLayout());

        nomeDaMarca.setFont(Estilo.fonteForte(14f));
        nomeDaMarca.setForeground(Estilo.paleta().texto);

        JPanel identificacao = Estilo.painel();
        identificacao.setLayout(new BorderLayout(9, 0));
        identificacao.add(marca, BorderLayout.WEST);
        identificacao.add(nomeDaMarca, BorderLayout.CENTER);

        JPanel cabecalho = Estilo.painel();
        cabecalho.setLayout(new BorderLayout());
        cabecalho.setBorder(BorderFactory.createEmptyBorder(15, 14, 15, 14));
        cabecalho.add(identificacao, BorderLayout.WEST);

        JPanel navegacao = Estilo.painel();
        navegacao.setLayout(new BoxLayout(navegacao, BoxLayout.Y_AXIS));
        navegacao.setBorder(BorderFactory.createEmptyBorder(2, PADDING, 0, PADDING));

        for (Pagina pagina : Pagina.values()) {
            ItemNavegacao item = new ItemNavegacao(pagina);
            itens.put(pagina, item);
            navegacao.add(item);
            navegacao.add(Estilo.espaco(4));
        }

        rodape.setFont(Estilo.fonteRegular(11f));
        rodape.setForeground(Estilo.paleta().textoSuave);
        rodape.setBorder(BorderFactory.createEmptyBorder(13, 16, 15, 16));

        JPanel base = Estilo.painel();
        base.setLayout(new BorderLayout());
        base.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.paleta().bordaSuave));
        base.add(rodape, BorderLayout.CENTER);

        add(cabecalho, BorderLayout.NORTH);
        add(navegacao, BorderLayout.CENTER);
        add(base, BorderLayout.SOUTH);

        definirPagina(Pagina.DASHBOARD);
    }

    public void definirPagina(Pagina pagina) {
        if (atual != null) {
            itens.get(atual).definirAtivo(false);
        }

        atual = pagina;
        itens.get(pagina).definirAtivo(true);
    }

    public void definirContagem(int aguardando) {
        itens.get(Pagina.FILA).definirContagem(aguardando);
    }

    public void definirCompacta(boolean compacta) {
        this.compacta = compacta;

        nomeDaMarca.setVisible(!compacta);
        rodape.setVisible(!compacta);
        marca.definirCompacta(compacta);

        for (ItemNavegacao item : itens.values()) {
            item.definirCompacta(compacta);
        }

        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                compacta ? Estilo.LARGURA_LATERAL_COMPACTA : Estilo.LARGURA_LATERAL,
                Math.max(super.getPreferredSize().height, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);

        g2.setColor(Estilo.paleta().lateral);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Estilo.paleta().borda);
        g2.fillRect(getWidth() - 1, 0, 1, getHeight());

        g2.dispose();
    }

    /** Marca da aplicação: também alterna o estado recolhido da navegação. */
    private final class Marca extends JPanel {

        private Marca() {
            setOpaque(false);
            setPreferredSize(new Dimension(28, 28));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Recolher navegação");

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent evento) {
                    aoAlternarRecolhimento.run();
                }
            });
        }

        private void definirCompacta(boolean compacta) {
            setToolTipText(compacta ? "Expandir navegação" : "Recolher navegação");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);

            Pintura.preencher(g2, 1, 1, 26, 26, 7, Estilo.paleta().primaria);

            g2.setColor(Estilo.paleta().sobrePrimaria);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double[] linhas = {9.5, 14.0, 18.5};
            double[] larguras = {9.0, 14.0, 8.0};

            for (int i = 0; i < linhas.length; i++) {
                g2.draw(new Line2D.Double(9.0, linhas[i], 5.0 + larguras[i], linhas[i]));
            }

            g2.dispose();
        }
    }

    /** Item de navegação com ícone, rótulo e contagem de pessoas aguardando. */
    private final class ItemNavegacao extends JButton {

        private final Pagina pagina;
        private final String rotulo;
        private int contagem = -1;
        private boolean ativo;
        private boolean sobre;
        private boolean compacta;

        private ItemNavegacao(Pagina pagina) {
            this.pagina = pagina;
            this.rotulo = pagina.getTitulo();

            setText(rotulo);
            setFont(Estilo.fonteRegular(13f));
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(11);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 11, 0, 11));
            setPreferredSize(new Dimension(120, Estilo.ALTURA_ITEM_NAVEGACAO));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Estilo.ALTURA_ITEM_NAVEGACAO));

            addActionListener(evento -> aoSelecionar.accept(this.pagina));
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

            aplicarCores();
        }

        private void definirAtivo(boolean ativo) {
            this.ativo = ativo;
            aplicarCores();
            repaint();
        }

        private void definirContagem(int valor) {
            contagem = valor;
            repaint();
        }

        private void definirCompacta(boolean compacta) {
            this.compacta = compacta;

            setText(compacta ? "" : rotulo);
            setHorizontalAlignment(compacta ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, compacta ? 0 : 11, 0, compacta ? 0 : 11));
            setToolTipText(compacta ? rotulo : null);
            repaint();
        }

        private void aplicarCores() {
            Paleta paleta = Estilo.paleta();
            Color cor = ativo ? paleta.primariaTexto : paleta.textoSecundario;

            setForeground(cor);
            setIcon(pagina.getIcone().comoIcone(18, cor));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);
            Paleta paleta = Estilo.paleta();

            if (ativo) {
                Pintura.preencher(g2, 0, 0, getWidth(), getHeight(), Estilo.RAIO,
                        paleta.primariaSuave);
                Pintura.preencher(g2, 0, (getHeight() - 18) / 2.0, 3, 18, 1.5, paleta.primaria);
            } else if (sobre) {
                Pintura.preencher(g2, 0, 0, getWidth(), getHeight(), Estilo.RAIO,
                        paleta.superficieAlt);
            }

            g2.dispose();
            super.paintComponent(g);

            if (contagem >= 0 && !compacta) {
                pintarContagem((Graphics2D) g);
            }
        }

        private void pintarContagem(Graphics2D g) {
            Graphics2D g2 = Pintura.preparar(g);
            Paleta paleta = Estilo.paleta();
            Font fonte = Estilo.fonteChip(11f);
            String valor = String.valueOf(contagem);

            g2.setFont(fonte);

            FontMetrics medidas = g2.getFontMetrics();
            int largura = medidas.stringWidth(valor) + 14;
            int altura = 18;
            int x = getWidth() - largura - 11;
            int y = (getHeight() - altura) / 2;

            Pintura.preencher(g2, x, y, largura, altura, 9,
                    ativo ? paleta.superficie : paleta.superficieAlt);

            g2.setColor(ativo ? paleta.primariaTexto : paleta.textoSecundario);
            g2.drawString(valor, x + 7,
                    y + (altura + medidas.getAscent() - medidas.getDescent()) / 2);

            g2.dispose();
        }
    }
}
