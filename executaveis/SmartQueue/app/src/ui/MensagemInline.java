package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Aviso de retorno das ações da tela. Mensagens de confirmação desaparecem
 * sozinhas; avisos de atenção permanecem até serem resolvidos.
 */
public class MensagemInline extends JPanel {

    private static final int ALTURA = 38;
    private static final int MARGEM_INFERIOR = 14;
    private static final int DURACAO = 5000;

    private final JLabel icone = new JLabel();
    private final JLabel texto = new JLabel();
    private final BotaoIcone fechar;
    private final Timer temporizador;

    private TipoMensagem tipo = TipoMensagem.INFO;

    public MensagemInline(boolean fechavel) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(0, ALTURA + MARGEM_INFERIOR));
        setVisible(false);

        texto.setFont(Estilo.fonteRegular(12.5f));
        texto.setVerticalAlignment(SwingConstants.CENTER);
        icone.setVerticalAlignment(SwingConstants.CENTER);

        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout(9, 0));
        conteudo.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, fechavel ? 6 : 13));
        conteudo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA));
        conteudo.setPreferredSize(new Dimension(0, ALTURA));
        conteudo.add(icone, BorderLayout.WEST);
        conteudo.add(texto, BorderLayout.CENTER);

        if (fechavel) {
            fechar = new BotaoIcone(Icone.FECHAR, "Fechar aviso", 12);
            fechar.addActionListener(evento -> limpar());
            conteudo.add(fechar, BorderLayout.EAST);
        } else {
            fechar = null;
        }

        add(conteudo);

        temporizador = new Timer(DURACAO, evento -> limpar());
        temporizador.setRepeats(false);
    }

    public MensagemInline() {
        this(true);
    }

    public void mostrar(String mensagem, TipoMensagem novoTipo) {
        tipo = novoTipo;
        texto.setText(mensagem);
        texto.setForeground(corDoTexto());
        icone.setIcon(iconeDoTipo());

        if (fechar != null) {
            fechar.definirCor(corDoTexto());
        }

        setVisible(true);
        temporizador.stop();

        if (tipo == TipoMensagem.SUCESSO || tipo == TipoMensagem.INFO) {
            temporizador.restart();
        }

        revalidate();
        repaint();
    }

    public void limpar() {
        temporizador.stop();
        setVisible(false);
        texto.setText("");
        icone.setIcon(null);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);

        Pintura.caixa(g2, 0.5, 0.5, getWidth() - 1, ALTURA - 1, Estilo.RAIO,
                corDoFundo(), corDaBorda());

        g2.dispose();
    }

    private Icon iconeDoTipo() {
        return switch (tipo) {
            case INFO -> null;
            case SUCESSO -> Icone.OK.comoIcone(16, corDoTexto());
            case ALERTA, ERRO -> Icone.ALERTA.comoIcone(16, corDoTexto());
        };
    }

    private Color corDoFundo() {
        Paleta paleta = Estilo.paleta();

        return switch (tipo) {
            case INFO -> paleta.superficieAlt;
            case SUCESSO -> paleta.sucessoSuave;
            case ALERTA -> paleta.alertaSuave;
            case ERRO -> paleta.perigoSuave;
        };
    }

    private Color corDaBorda() {
        Paleta paleta = Estilo.paleta();

        return tipo == TipoMensagem.INFO
                ? paleta.borda
                : Cores.misturar(corDoFundo(), corDoTexto(), 0.28f);
    }

    private Color corDoTexto() {
        Paleta paleta = Estilo.paleta();

        return switch (tipo) {
            case INFO -> paleta.textoSecundario;
            case SUCESSO -> paleta.sucesso;
            case ALERTA -> paleta.alerta;
            case ERRO -> paleta.perigo;
        };
    }
}
