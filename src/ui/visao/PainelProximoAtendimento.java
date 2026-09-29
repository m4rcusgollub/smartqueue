package ui.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Consumidor;
import service.FilaService;
import ui.Apresentacao;
import ui.Botao;
import ui.ChipPrioridade;
import ui.Estilo;
import ui.Icone;
import ui.PainelSuperficie;

/**
 * Painel de destaque com quem será atendido e a ação de chamada. É usado em
 * duas escalas: resumo no dashboard e área principal na tela de atendimento.
 */
public class PainelProximoAtendimento extends PainelSuperficie {

    private final boolean destaque;
    private final JLabel identificacao = new JLabel();
    private final JLabel nome = new JLabel();
    private final JLabel espera = new JLabel();
    private final JPanel areaDaEtiqueta = Estilo.painel();
    private final Botao chamar;

    public PainelProximoAtendimento(boolean destaque, Runnable aoChamar) {
        this.destaque = destaque;

        setLayout(new BorderLayout());

        identificacao.setFont(Estilo.fonteMono(destaque ? 15f : 13f));
        identificacao.setForeground(Estilo.paleta().textoSecundario);
        identificacao.setAlignmentX(LEFT_ALIGNMENT);

        nome.setFont(Estilo.fonteForte(destaque ? 24f : 18f));
        nome.setForeground(Estilo.paleta().texto);
        nome.setAlignmentX(LEFT_ALIGNMENT);

        areaDaEtiqueta.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        espera.setFont(Estilo.fonteRegular(12f));
        espera.setForeground(Estilo.paleta().textoSuave);

        JPanel linhaDaEtiqueta = Estilo.painel();
        linhaDaEtiqueta.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        linhaDaEtiqueta.setAlignmentX(LEFT_ALIGNMENT);
        linhaDaEtiqueta.add(areaDaEtiqueta);
        linhaDaEtiqueta.add(espera);

        JLabel titulo = Estilo.secao("Próximo atendimento");
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JPanel bloco = Estilo.painel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));
        bloco.add(titulo);
        bloco.add(Estilo.espaco(destaque ? 18 : 14));
        bloco.add(identificacao);
        bloco.add(Estilo.espaco(4));
        bloco.add(nome);
        bloco.add(Estilo.espaco(destaque ? 14 : 10));
        bloco.add(linhaDaEtiqueta);

        chamar = new Botao("Chamar próximo", Icone.SETA, Botao.Variante.PRIMARIO);
        chamar.definirAltura(destaque ? 44 : Estilo.ALTURA_BOTAO);
        chamar.addActionListener(evento -> aoChamar.run());

        JPanel lugarDoBotao = Estilo.painel();
        lugarDoBotao.setLayout(new BorderLayout());
        lugarDoBotao.setBorder(BorderFactory.createEmptyBorder(destaque ? 20 : 16, 0, 0, 0));

        if (destaque) {
            lugarDoBotao.add(chamar, BorderLayout.WEST);
        } else {
            chamar.ocuparLargura();
            lugarDoBotao.add(chamar, BorderLayout.CENTER);
        }

        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout());
        conteudo.setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
        conteudo.add(bloco, BorderLayout.NORTH);
        conteudo.add(lugarDoBotao, BorderLayout.SOUTH);

        add(conteudo, BorderLayout.CENTER);
    }

    public void atualizar(FilaService servico) {
        Consumidor proximo = servico.proximo();

        areaDaEtiqueta.removeAll();

        if (proximo == null) {
            identificacao.setText("—");
            nome.setText("A fila está vazia.");
            nome.setFont(Estilo.fonteForte(destaque ? 20f : 16f));
            nome.setForeground(Estilo.paleta().textoSecundario);
            espera.setText("Nenhuma pessoa aguardando.");
            chamar.setEnabled(false);
        } else {
            identificacao.setText(Apresentacao.id(proximo.getId()));
            nome.setText(proximo.getNome());
            nome.setFont(Estilo.fonteForte(destaque ? 24f : 18f));
            nome.setForeground(Estilo.paleta().texto);
            areaDaEtiqueta.add(new ChipPrioridade(proximo.getPrioridade(), destaque));
            espera.setText("Aguardando há "
                    + Apresentacao.espera(servico.minutosDeEspera(proximo)));
            chamar.setEnabled(true);
        }

        areaDaEtiqueta.revalidate();
        areaDaEtiqueta.repaint();
        revalidate();
        repaint();
    }
}
