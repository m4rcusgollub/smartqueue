package ui.visao;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import service.FilaService;
import ui.AcoesJanela;
import ui.Estilo;
import ui.MensagemInline;
import ui.Preferencias;
import ui.TipoMensagem;

/**
 * Base das telas: recebe o serviço da fila e as operações da janela, monta o
 * conteúdo com a faixa de avisos e permite ações próprias no cabeçalho.
 */
public abstract class Visao extends JPanel {

    protected final FilaService servico;
    protected final Preferencias preferencias;
    protected final AcoesJanela acoes;
    protected final boolean compacta;

    private final MensagemInline mensagem = new MensagemInline();

    protected Visao(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                    boolean compacta) {
        this.servico = servico;
        this.preferencias = preferencias;
        this.acoes = acoes;
        this.compacta = compacta;

        setOpaque(false);
        setLayout(new BorderLayout());
    }

    protected final void montar(JComponent conteudo) {
        JPanel corpo = Estilo.painel();
        corpo.setLayout(new BorderLayout());
        corpo.setBorder(BorderFactory.createEmptyBorder(16, Estilo.PADDING_CONTEUDO,
                Estilo.PADDING_CONTEUDO, Estilo.PADDING_CONTEUDO));
        corpo.add(mensagem, BorderLayout.NORTH);
        corpo.add(conteudo, BorderLayout.CENTER);

        add(corpo, BorderLayout.CENTER);
    }

    protected final void avisar(String texto, TipoMensagem tipo) {
        mensagem.mostrar(texto, tipo);
        acoes.registrarMensagem(texto);
    }

    /** Ações exibidas no cabeçalho da página; nenhuma tela é obrigada a ter. */
    public JComponent acoesDoCabecalho() {
        return null;
    }

    /** Releitura dos dados do serviço. */
    public abstract void atualizar();
}
