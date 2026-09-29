package ui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import service.FilaService;

/**
 * Janela da aplicação: define tamanho inicial, fechamento e reação ao
 * redimensionamento. Todo o conteúdo fica em {@link PainelAplicacao}, o que
 * permite montar a mesma interface sem uma janela (pré-visualização).
 */
public class JanelaPrincipal extends JFrame {

    private static final Dimension TAMANHO_INICIAL = new Dimension(1280, 800);

    private final PainelAplicacao aplicacao;

    public JanelaPrincipal(FilaService servico, Preferencias preferencias) {
        super("Smart Queue");

        aplicacao = new PainelAplicacao(servico, preferencias);
        aplicacao.definirOrigemDeDialogo(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(TAMANHO_INICIAL);
        setLocationRelativeTo(null);
        setContentPane(aplicacao);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent evento) {
                aplicacao.reagirRedimensionamento();
            }
        });
    }
}
