package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Superfície com cabeçalho próprio (título, informação de apoio e ações) usada
 * quando existe agrupamento funcional de conteúdo, como a listagem da fila.
 */
public class PainelSecao extends PainelSuperficie {

    private static final int ALTURA_CABECALHO = 46;
    private static final int PADDING = 16;

    private final JLabel titulo;
    private final JLabel informacao;
    private final JPanel acoes = Estilo.painel();
    private final JPanel corpo = Estilo.painel();

    public PainelSecao(String titulo) {
        this.titulo = Estilo.secao(titulo);
        this.informacao = Estilo.suave("");

        setLayout(new BorderLayout());

        JPanel cabecalho = Estilo.painel();
        cabecalho.setLayout(new BorderLayout(16, 0));
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                Estilo.bordaInferiorSuave(),
                BorderFactory.createEmptyBorder(0, PADDING, 0, 12)));
        cabecalho.setPreferredSize(new Dimension(0, ALTURA_CABECALHO));
        cabecalho.add(this.titulo, BorderLayout.WEST);

        JPanel ladoDireito = Estilo.painel();
        ladoDireito.setLayout(new BorderLayout(10, 0));
        ladoDireito.add(informacao, BorderLayout.WEST);
        ladoDireito.add(acoes, BorderLayout.EAST);
        acoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cabecalho.add(ladoDireito, BorderLayout.EAST);

        corpo.setLayout(new BorderLayout());
        corpo.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));

        add(cabecalho, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);
    }

    /** Área de conteúdo do painel (a tabela ou o estado vazio). */
    public JPanel corpo() {
        return corpo;
    }

    public void definirInformacao(String texto) {
        informacao.setText(texto);
    }

    public void definirTitulo(String texto) {
        titulo.setText(texto);
    }

    public void adicionarAcao(JComponent componente) {
        acoes.add(componente);
    }
}
