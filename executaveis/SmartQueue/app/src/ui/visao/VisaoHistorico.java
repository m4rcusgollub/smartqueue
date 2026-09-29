package ui.visao;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import service.FilaService;
import ui.AcoesJanela;
import ui.EstadoVazio;
import ui.Estilo;
import ui.Icone;
import ui.PainelSecao;
import ui.Preferencias;
import ui.Renderizadores;
import ui.Tabela;
import ui.modelo.ModeloHistorico;

/**
 * Histórico dos atendimentos registrados, do mais recente para o mais antigo,
 * com indicação do guichê responsável e situação final do atendimento.
 */
public class VisaoHistorico extends Visao {

    private static final String CARTAO_TABELA = "tabela";
    private static final String CARTAO_VAZIO = "vazio";

    private final ModeloHistorico modelo;
    private final PainelSecao secao = new PainelSecao("Atendimentos Registrados");
    private final JPanel area = new JPanel(new CardLayout());

    public VisaoHistorico(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                          boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        modelo = new ModeloHistorico(servico);

        JTable tabela = Tabela.criar();
        tabela.setModel(modelo);
        Tabela.renderizador(tabela, 0, new Renderizadores.Codigo(16, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 1, new Renderizadores.Texto(16));
        Tabela.renderizador(tabela, 2, new Renderizadores.PrioridadeChip());
        Tabela.renderizador(tabela, 3, new Renderizadores.Texto(16, true,
                Renderizadores.Tom.NORMAL, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 4, new Renderizadores.Texto(16, false,
                Renderizadores.Tom.SECUNDARIO, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 5, new Renderizadores.Texto(16, false,
                Renderizadores.Tom.SECUNDARIO, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 6, new Renderizadores.Situacao());
        Tabela.larguras(tabela, 70, 240, 130, 110, 100, 90, 120);

        area.setOpaque(false);
        area.add(Tabela.emRolagem(tabela), CARTAO_TABELA);
        area.add(new EstadoVazio(Icone.HISTORICO, "Nenhum atendimento registrado nesta sessão.",
                "Cada chamada realizada nos guichês aparecerá aqui com guichê, horário e status."),
                CARTAO_VAZIO);

        secao.corpo().add(area, BorderLayout.CENTER);
        secao.definirInformacao("sessão atual, em memória");

        montar(construirConteudo());
        atualizar();
    }

    @Override
    public void atualizar() {
        modelo.atualizar();

        int total = servico.totalAtendimentos();

        secao.definirInformacao(total == 0
                ? "sessão atual, em memória"
                : total == 1 ? "1 atendimento registrado" : total + " atendimentos registrados");

        ((CardLayout) area.getLayout()).show(area, total == 0 ? CARTAO_VAZIO : CARTAO_TABELA);
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout());
        conteudo.add(secao, BorderLayout.CENTER);

        return conteudo;
    }
}
