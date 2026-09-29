package ui.visao;

import model.Consumidor;
import service.FilaService;
import ui.AcoesJanela;
import ui.Apresentacao;
import ui.Preferencias;
import ui.TipoMensagem;

/**
 * Ação de chamar o próximo consumidor, usada pelo dashboard, pela fila e pela
 * tela de atendimento. A ordem de chamada continua sendo decidida pela
 * PriorityQueue em FilaPrioridade.
 */
public class AcaoChamar {

    private final FilaService servico;
    private final Preferencias preferencias;
    private final AcoesJanela acoes;
    private final Visao visao;

    public AcaoChamar(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                      Visao visao) {
        this.servico = servico;
        this.preferencias = preferencias;
        this.acoes = acoes;
        this.visao = visao;
    }

    public void chamar() {
        Consumidor proximo = servico.proximo();

        if (proximo == null) {
            visao.avisar("A fila está vazia.", TipoMensagem.INFO);
            return;
        }

        if (preferencias.isConfirmarAoChamar()
                && !acoes.confirmar("Chamar " + Apresentacao.id(proximo.getId())
                + " — " + proximo.getNome() + "?")) {
            return;
        }

        Consumidor atendido = servico.chamarProximo();

        if (atendido != null) {
            visao.avisar("Atendendo: " + atendido.getNome()
                    + " · " + Apresentacao.id(atendido.getId()), TipoMensagem.SUCESSO);
        }
    }
}
