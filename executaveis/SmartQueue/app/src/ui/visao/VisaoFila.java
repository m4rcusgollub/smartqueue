package ui.visao;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import model.Consumidor;
import model.Prioridade;
import service.FilaService;
import ui.AcoesJanela;
import ui.Apresentacao;
import ui.Botao;
import ui.CampoTexto;
import ui.EstadoVazio;
import ui.Estilo;
import ui.Icone;
import ui.PainelSecao;
import ui.Pilha;
import ui.Preferencias;
import ui.Renderizadores;
import ui.SeletorPrioridade;
import ui.Tabela;
import ui.TipoMensagem;
import ui.modelo.ModeloFila;

/**
 * Cadastro de consumidores e leitura completa da fila de espera.
 */
public class VisaoFila extends Visao {

    private static final String CARTAO_TABELA = "tabela";
    private static final String CARTAO_VAZIO = "vazio";
    private static final int LARGURA_FORMULARIO = 344;

    private final ModeloFila modelo;
    private final CampoTexto campoNome = new CampoTexto("Nome do consumidor",
            "Nome usado na chamada do atendimento.");
    private final SeletorPrioridade seletor = new SeletorPrioridade();
    private final Botao adicionar = new Botao("Adicionar à fila", Icone.MAIS,
            Botao.Variante.PRIMARIO);
    private final Botao chamarProximo = new Botao("Chamar próximo", Icone.SETA,
            Botao.Variante.SECUNDARIO);
    private final AcaoChamar acaoChamar;

    private PainelSecao secaoDaFila;
    private JPanel areaDaFila;

    public VisaoFila(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                     boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        modelo = new ModeloFila(servico);
        acaoChamar = new AcaoChamar(servico, preferencias, acoes, this);

        campoNome.aoConfirmar(this::adicionarConsumidor);
        adicionar.addActionListener(evento -> adicionarConsumidor());
        adicionar.ocuparLargura();
        chamarProximo.addActionListener(evento -> acaoChamar.chamar());

        montar(construirConteudo());
        atualizar();
    }

    @Override
    public JComponent acoesDoCabecalho() {
        return chamarProximo;
    }

    @Override
    public void atualizar() {
        modelo.atualizar();

        int total = servico.totalAguardando();

        secaoDaFila.definirInformacao(total == 0
                ? "nenhuma pessoa aguardando"
                : total == 1 ? "1 pessoa aguardando" : total + " pessoas aguardando");

        ((CardLayout) areaDaFila.getLayout()).show(areaDaFila,
                total == 0 ? CARTAO_VAZIO : CARTAO_TABELA);

        chamarProximo.setEnabled(total > 0);
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout(Estilo.ESPACO, 0));

        if (compacta) {
            Pilha coluna = new Pilha();
            coluna.adicionar(construirFormulario(), 0, 0, Estilo.ESPACO);
            coluna.adicionar(construirSecaoDaFila(), 1);
            conteudo.add(coluna, BorderLayout.CENTER);
        } else {
            conteudo.add(construirFormulario(), BorderLayout.WEST);
            conteudo.add(construirSecaoDaFila(), BorderLayout.CENTER);
        }

        return conteudo;
    }

    private JComponent construirFormulario() {
        PainelSecao formulario = new PainelSecao("Adicionar consumidor");
        formulario.definirInformacao("entra na fila");

        Pilha pilha = new Pilha();
        pilha.adicionar(campoNome, 0, 0, 14);
        pilha.adicionar(seletor, 0, 0, 18);
        pilha.adicionar(adicionar);

        JPanel interior = Estilo.interior();
        interior.add(pilha, BorderLayout.NORTH);
        formulario.corpo().add(interior, BorderLayout.CENTER);

        if (compacta) {
            return formulario;
        }

        // A coluna mantém o cartão no topo, com o fundo da aplicação abaixo,
        // em vez de esticar o branco até o rodapé.
        JPanel coluna = Estilo.painel();
        coluna.setLayout(new BorderLayout());
        coluna.add(formulario, BorderLayout.NORTH);
        coluna.setPreferredSize(new Dimension(LARGURA_FORMULARIO,
                formulario.getPreferredSize().height));

        return coluna;
    }

    private JComponent construirSecaoDaFila() {
        secaoDaFila = new PainelSecao("Fila de espera");

        JTable tabela = Tabela.criar();
        tabela.setModel(modelo);
        Tabela.renderizador(tabela, 0, new Renderizadores.Codigo(16, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 1, new Renderizadores.Codigo(16, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 2, new Renderizadores.Texto(16));
        Tabela.renderizador(tabela, 3, new Renderizadores.PrioridadeChip());
        Tabela.renderizador(tabela, 4, new Renderizadores.Texto(16, false,
                Renderizadores.Tom.SECUNDARIO, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 5, new Renderizadores.Situacao());
        Tabela.larguras(tabela, 44, 60, 240, 128, 78, 118);

        areaDaFila = new JPanel(new CardLayout());
        areaDaFila.setOpaque(false);
        areaDaFila.add(Tabela.emRolagem(tabela), CARTAO_TABELA);
        areaDaFila.add(new EstadoVazio(Icone.CAIXA_VAZIA, "A fila está vazia.",
                compacta
                        ? "Cadastre um consumidor no formulário acima para iniciar o atendimento."
                        : "Cadastre um consumidor no formulário ao lado para iniciar o atendimento."),
                CARTAO_VAZIO);

        secaoDaFila.corpo().add(areaDaFila, BorderLayout.CENTER);

        return secaoDaFila;
    }

    private void adicionarConsumidor() {
        if (campoNome.vazio()) {
            campoNome.marcarErro("Informe o nome do consumidor.");
            campoNome.focar();
            avisar("Informe o nome do consumidor.", TipoMensagem.ERRO);
            return;
        }

        Prioridade prioridade = seletor.selecionada();
        Consumidor consumidor = servico.adicionar(campoNome.texto(), prioridade);

        avisar("Consumidor adicionado à fila: " + Apresentacao.id(consumidor.getId())
                        + " · " + consumidor.getNome() + " · " + Apresentacao.prioridade(prioridade),
                TipoMensagem.SUCESSO);

        if (preferencias.isLimparNomeAoAdicionar()) {
            campoNome.limpar();
        } else {
            campoNome.definirTexto("");
        }

        campoNome.focar();
    }
}
