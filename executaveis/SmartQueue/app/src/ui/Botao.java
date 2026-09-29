package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Botão plano com três variantes de uso (ação principal, secundária e ação
 * discreta). Cantos discretos, sem sombra e com feedback apenas no hover/pressão.
 */
public class Botao extends JButton {

    public enum Variante {
        PRIMARIO,
        SECUNDARIO,
        SUTIL
    }

    private final Variante variante;
    private Icone icone;
    private int altura = Estilo.ALTURA_BOTAO;
    private int larguraMinima;
    private boolean ocuparLargura;
    private boolean sobre;

    public Botao(String texto, Icone icone, Variante variante) {
        super(texto);
        this.variante = variante;
        this.icone = icone;

        setFont(Estilo.fonteForte(13f));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setRolloverEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIconTextGap(9);
        setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        setVerticalAlignment(SwingConstants.CENTER);

        instalarFeedback();
        aplicarCores();
    }

    public Botao(String texto, Variante variante) {
        this(texto, null, variante);
    }

    public Botao(String texto) {
        this(texto, null, Variante.SECUNDARIO);
    }

    public void definirIcone(Icone novoIcone) {
        definirIcone(novoIcone, false);
    }

    public void definirIcone(Icone novoIcone, boolean aposTexto) {
        icone = novoIcone;
        setHorizontalTextPosition(aposTexto ? SwingConstants.RIGHT : SwingConstants.LEFT);
        aplicarCores();
    }

    public void definirAltura(int novaAltura) {
        altura = novaAltura;
        revalidate();
        repaint();
    }

    public void definirLarguraMinima(int novaLargura) {
        larguraMinima = novaLargura;
        revalidate();
    }

    public void ocuparLargura() {
        ocuparLargura = true;
        revalidate();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension padrao = super.getPreferredSize();

        return new Dimension(Math.max(padrao.width, larguraMinima), Math.max(padrao.height, altura));
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension padrao = super.getMaximumSize();

        if (!ocuparLargura) {
            return padrao;
        }

        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);
        int largura = getWidth();
        int alturaReal = getHeight();
        Color fundo = corDeFundo();
        Color borda = corDaBorda();

        if (fundo.getAlpha() > 0) {
            Pintura.preencher(g2, 0.5, 0.5, largura - 1, alturaReal - 1, Estilo.RAIO, fundo);
        }

        if (borda.getAlpha() > 0) {
            Pintura.contornar(g2, 0.5, 0.5, largura - 1, alturaReal - 1, Estilo.RAIO, borda, 1f);
        }

        if (isFocusOwner() && isEnabled()) {
            Color anel = variante == Variante.PRIMARIO
                    ? Cores.comAlfa(Color.WHITE, 130)
                    : Cores.comAlfa(Estilo.paleta().primaria, 90);

            Pintura.contornar(g2, 2.5, 2.5, largura - 5, alturaReal - 5, Estilo.RAIO - 1, anel, 1.4f);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private boolean pressionado() {
        return getModel().isPressed() && getModel().isArmed();
    }

    private Color corDeFundo() {
        Paleta paleta = Estilo.paleta();

        if (!isEnabled()) {
            return variante == Variante.PRIMARIO ? paleta.borda : Cores.comAlfa(Color.WHITE, 0);
        }

        return switch (variante) {
            case PRIMARIO -> pressionado() ? paleta.primariaAtiva
                    : sobre ? paleta.primariaHover : paleta.primaria;
            case SECUNDARIO -> sobre || pressionado() ? paleta.superficieAlt : paleta.superficie;
            case SUTIL -> sobre || pressionado() ? paleta.primariaSuave : Cores.comAlfa(Color.WHITE, 0);
        };
    }

    private Color corDaBorda() {
        Paleta paleta = Estilo.paleta();

        return switch (variante) {
            case PRIMARIO -> Cores.comAlfa(Color.WHITE, 0);
            case SECUNDARIO -> isEnabled()
                    ? sobre ? Cores.misturar(paleta.borda, paleta.texto, 0.20f) : paleta.borda
                    : paleta.bordaSuave;
            case SUTIL -> Cores.comAlfa(Color.WHITE, 0);
        };
    }

    private void aplicarCores() {
        Paleta paleta = Estilo.paleta();

        Color cor = !isEnabled()
                ? paleta.textoSuave
                : switch (variante) {
                    case PRIMARIO -> paleta.sobrePrimaria;
                    case SECUNDARIO -> paleta.texto;
                    case SUTIL -> paleta.primariaTexto;
                };

        setForeground(cor);
        setIcon(icone == null ? null : icone.comoIcone(17, cor));
    }

    @Override
    public void setEnabled(boolean habilitado) {
        super.setEnabled(habilitado);
        aplicarCores();
        repaint();
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
