package ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Empilhamento vertical em que cada elemento mantém a altura natural, exceto
 * o que recebe peso (usado quando um trecho deve ocupar o espaço restante).
 */
public class Pilha extends JPanel {

    private final GridBagLayout layout = new GridBagLayout();

    private int proximaLinha;

    public Pilha() {
        setOpaque(false);
        setLayout(layout);
    }

    public Pilha adicionar(JComponent componente) {
        return adicionar(componente, 0, 0, 0);
    }

    public Pilha adicionar(JComponent componente, double pesoVertical) {
        return adicionar(componente, pesoVertical, 0, 0);
    }

    public Pilha adicionar(JComponent componente, double pesoVertical, int espacoSuperior,
                           int espacoInferior) {
        // Congela o tamanho mínimo no tamanho natural: sem isso o GridBagLayout
        // encolhe a linha abaixo do necessário quando existe uma linha com peso,
        // o que cortava o rodapé dos painéis da coluna lateral.
        componente.setMinimumSize(componente.getPreferredSize());

        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.gridx = 0;
        restricoes.gridy = proximaLinha++;
        restricoes.weightx = 1;
        restricoes.weighty = pesoVertical;
        restricoes.fill = pesoVertical > 0
                ? GridBagConstraints.BOTH
                : GridBagConstraints.HORIZONTAL;
        restricoes.anchor = GridBagConstraints.NORTHWEST;
        restricoes.insets = new Insets(espacoSuperior, 0, espacoInferior, 0);

        add(componente, restricoes);
        return this;
    }

    public Pilha espaco(int altura) {
        JComponent vazio = Estilo.painel();
        vazio.setPreferredSize(new Dimension(0, altura));
        vazio.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));

        return adicionar(vazio, 0, 0, 0);
    }

    /** Absorve o espaço vertical restante, mantendo o conteúdo alinhado ao topo. */
    public Pilha sobra() {
        JComponent vazio = Estilo.painel();
        vazio.setMinimumSize(new Dimension(0, 0));
        vazio.setPreferredSize(new Dimension(0, 0));

        return adicionar(vazio, 1, 0, 0);
    }
}
