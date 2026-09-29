package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Rodapé persistente: última ação registrada, contadores da sessão e hora atual.
 */
public class RodapeStatus extends JPanel {

    private static final int ALTURA = 32;
    private static final int INTERVALO_RELOGIO = 15000;

    private final JLabel mensagem = new JLabel("");
    private final JLabel contadores = new JLabel("");
    private final Timer relogio;

    private int aguardando;
    private int atendidos;

    public RodapeStatus() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setPreferredSize(new Dimension(0, ALTURA));
        setBorder(BorderFactory.createEmptyBorder(0, Estilo.PADDING_CONTEUDO, 0,
                Estilo.PADDING_CONTEUDO));

        mensagem.setFont(Estilo.fonteRegular(12f));
        mensagem.setForeground(Estilo.paleta().textoSecundario);

        contadores.setFont(Estilo.fonteRegular(12f));
        contadores.setForeground(Estilo.paleta().textoSuave);

        add(mensagem, BorderLayout.WEST);
        add(contadores, BorderLayout.EAST);

        relogio = new Timer(INTERVALO_RELOGIO, evento -> atualizarContadores());
        relogio.start();
        atualizarContadores();
    }

    public void definirMensagem(String texto) {
        mensagem.setText(texto);
    }

    public void definirNumeros(int aguardando, int atendidos) {
        this.aguardando = aguardando;
        this.atendidos = atendidos;
        atualizarContadores();
    }

    private void atualizarContadores() {
        contadores.setText("Aguardando " + aguardando
                + "   ·   Atendidos " + atendidos
                + "   ·   " + Apresentacao.hora(LocalDateTime.now()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Pintura.preparar(g);

        g2.setColor(Estilo.paleta().superficie);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Estilo.paleta().borda);
        g2.fillRect(0, 0, getWidth(), 1);

        g2.dispose();
    }
}
