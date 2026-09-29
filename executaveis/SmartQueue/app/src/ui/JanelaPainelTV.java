package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import service.FilaService;
import ui.visao.VisaoPainelTV;

/**
 * Janela dedicada para o Painel de TV / Monitor da sala de espera.
 * Pode ser maximizada ou movida para uma segunda tela/projetor.
 */
public class JanelaPainelTV extends JFrame {

    private final VisaoPainelTV visao;

    public JanelaPainelTV(FilaService servico, Preferencias preferencias) {
        super("Smart Queue · Painel de Chamada (TV)");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
        setSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        AcoesJanela acoesDummy = new AcoesJanela() {
            @Override public void irPara(Pagina pagina) {}
            @Override public void definirTema(Tema tema) {}
            @Override public void registrarMensagem(String texto) {}
            @Override public boolean confirmar(String mensagem) { return true; }
            @Override public void atualizarInterface() { visao.atualizar(); }
        };

        visao = new VisaoPainelTV(servico, preferencias, acoesDummy, false);
        servico.observar(visao::atualizar);

        getContentPane().setBackground(Estilo.paleta().fundo);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(visao, BorderLayout.CENTER);
    }
}
