package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Campo de texto com rótulo, dica de preenchimento e mensagem de erro.
 */
public class CampoTexto extends JPanel {

    private final Base campo = new Base();
    private final JLabel rotulo;
    private final JLabel apoio;
    private final String dica;
    private boolean comErro;

    public CampoTexto(String rotulo, String dica) {
        this.rotulo = Estilo.rotulo(rotulo);
        this.dica = dica;
        this.apoio = dica == null ? null : Estilo.suave(dica);

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.rotulo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        add(this.rotulo);
        add(Estilo.espaco(6));

        configurarCampo();
        campo.setPreferredSize(new Dimension(120, Estilo.ALTURA_CAMPO));
        campo.setMinimumSize(new Dimension(80, Estilo.ALTURA_CAMPO));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, Estilo.ALTURA_CAMPO));
        add(campo);

        if (apoio != null) {
            apoio.setAlignmentX(LEFT_ALIGNMENT);
            apoio.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            add(apoio);
        }
    }

    public CampoTexto(String rotulo) {
        this(rotulo, null);
    }

    public String texto() {
        return campo.getText().trim();
    }

    public boolean vazio() {
        return texto().isEmpty();
    }

    public void definirTexto(String texto) {
        campo.setText(texto);
    }

    public void limpar() {
        campo.setText("");
        limparErro();
    }

    public void focar() {
        campo.requestFocusInWindow();
    }

    public void aoConfirmar(Runnable acao) {
        campo.addActionListener(evento -> acao.run());
    }

    public void aoAlterar(Runnable acao) {
        campo.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent evento) {
                reagir();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                reagir();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                reagir();
            }

            private void reagir() {
                limparErro();
                acao.run();
            }
        });
    }

    public void marcarErro(String mensagem) {
        comErro = true;

        if (apoio == null) {
            campo.repaint();
            return;
        }

        apoio.setText(mensagem);
        apoio.setFont(Estilo.fonteRegular(12f));
        apoio.setForeground(Estilo.paleta().perigo);
        campo.repaint();
    }

    public void limparErro() {
        if (!comErro) {
            return;
        }

        comErro = false;

        if (apoio != null) {
            apoio.setText(dica);
            apoio.setFont(Estilo.fonteRegular(12f));
            apoio.setForeground(Estilo.paleta().textoSuave);
        }

        campo.repaint();
    }

    private final class Base extends JTextField {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);
            Paleta paleta = Estilo.paleta();
            boolean focado = isFocusOwner();
            Color borda = comErro ? paleta.perigo : focado ? paleta.primaria : paleta.borda;

            Pintura.caixa(g2, 0.5, 0.5, getWidth() - 1, getHeight() - 1, Estilo.RAIO,
                    paleta.superficie, borda);

            if (focado) {
                Pintura.contornar(g2, 2.5, 2.5, getWidth() - 5, getHeight() - 5, Estilo.RAIO - 1,
                        Cores.comAlfa(paleta.primaria, 75), 1.6f);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void configurarCampo() {
        Paleta paleta = Estilo.paleta();

        campo.setOpaque(false);
        campo.setFont(Estilo.fonteRegular(13f));
        campo.setForeground(paleta.texto);
        campo.setCaretColor(paleta.primaria);
        campo.setSelectionColor(paleta.primariaSuave);
        campo.setSelectedTextColor(paleta.texto);
        campo.setDisabledTextColor(paleta.textoSuave);
        campo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
    }
}
