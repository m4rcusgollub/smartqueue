package ui;

import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Estado vazio de uma listagem: ícone da família visual, título objetivo e
 * uma linha explicando o que fazer em seguida.
 */
public class EstadoVazio extends JPanel {

    public EstadoVazio(Icone icone, String titulo, String descricao) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel bloco = Estilo.painel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));

        JLabel marca = new JLabel(icone.comoIcone(30, Estilo.paleta().textoSuave));
        marca.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tituloRotulo = new JLabel(titulo);
        tituloRotulo.setFont(Estilo.fonteForte(13f));
        tituloRotulo.setForeground(Estilo.paleta().textoSecundario);
        tituloRotulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel descricaoRotulo = new JLabel(descricao);
        descricaoRotulo.setFont(Estilo.fonteRegular(12f));
        descricaoRotulo.setForeground(Estilo.paleta().textoSuave);
        descricaoRotulo.setAlignmentX(CENTER_ALIGNMENT);

        bloco.add(marca);
        bloco.add(Estilo.espaco(12));
        bloco.add(tituloRotulo);
        bloco.add(Estilo.espaco(5));
        bloco.add(descricaoRotulo);

        add(bloco);
    }
}
