package ui.visao;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import model.Consumidor;
import model.Prioridade;
import service.FilaService;
import ui.AcoesJanela;
import ui.Apresentacao;
import ui.Botao;
import ui.EstadoVazio;
import ui.Estilo;
import ui.Icone;
import ui.MensagemInline;
import ui.Pagina;
import ui.PainelSecao;
import ui.PainelSuperficie;
import ui.Pilha;
import ui.Pintura;
import ui.PontoColorido;
import ui.Preferencias;
import ui.Renderizadores;
import ui.Tabela;
import ui.TipoMensagem;
import ui.modelo.ModeloFila;

/**
 * Dashboard Gerencial: 4 cartões de KPIs modernos (pessoas na fila, tempo médio de espera,
 * atendimentos do dia, próximo da fila), composição por prioridade com percentuais e tabela dinâmica.
 */
public class VisaoDashboard extends Visao {

    private static final String CARTAO_TABELA = "tabela";
    private static final String CARTAO_VAZIO = "vazio";

    private static final Prioridade[] ORDEM_DE_PRIORIDADE = {
            Prioridade.URGENTE, Prioridade.PRIORIDADE, Prioridade.NORMAL, Prioridade.BAIXA};

    private final ModeloFila modelo;
    private final PainelProximoAtendimento painelProximo;
    private final MensagemInline alerta = new MensagemInline(false);

    private final JLabel totalNaFila = Estilo.valor("0");
    private final JLabel tempoMedioEspera = Estilo.valor("0 min");
    private final JLabel totalAtendidos = Estilo.valor("0");
    private final JLabel nomeDoProximo = rotuloDeValor("—", 16f);
    private final JLabel detalheDoProximo = Estilo.suave("Nenhuma pessoa aguardando");

    private final Map<Prioridade, JLabel> contagens = new EnumMap<>(Prioridade.class);
    private final Map<Prioridade, JLabel> percentuais = new EnumMap<>(Prioridade.class);
    private final Map<Prioridade, Barra> barras = new EnumMap<>(Prioridade.class);

    private PainelSecao secaoDaFila;
    private JPanel areaDaFila;

    public VisaoDashboard(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                          boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        modelo = new ModeloFila(servico);
        AcaoChamar chamar = new AcaoChamar(servico, preferencias, acoes, this);
        painelProximo = new PainelProximoAtendimento(false, chamar::chamar);

        montar(construirConteudo());
        atualizar();
    }

    private static JLabel rotuloDeValor(String texto, float tamanho) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Estilo.fonteForte(tamanho));
        rotulo.setForeground(Estilo.paleta().texto);
        return rotulo;
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout());

        Pilha topo = new Pilha();
        topo.adicionar(alerta);
        topo.adicionar(construirResumoKPIs(), 0, 0, Estilo.ESPACO);
        conteudo.add(topo, BorderLayout.NORTH);

        if (compacta) {
            Pilha coluna = new Pilha();
            coluna.adicionar(painelProximo, 0, 0, Estilo.ESPACO);
            coluna.adicionar(construirComposicao(), 0, 0, Estilo.ESPACO);
            coluna.adicionar(construirSecaoDaFila(), 1);
            conteudo.add(coluna, BorderLayout.CENTER);
        } else {
            JPanel linha = Estilo.painel();
            linha.setLayout(new BorderLayout(Estilo.ESPACO, 0));
            linha.add(construirSecaoDaFila(), BorderLayout.CENTER);

            Pilha lateral = new Pilha();
            lateral.setPreferredSize(new Dimension(Estilo.LARGURA_COLUNA_LATERAL, 0));
            lateral.adicionar(painelProximo, 0, 0, Estilo.ESPACO);
            lateral.adicionar(construirComposicao());
            lateral.sobra();
            linha.add(lateral, BorderLayout.EAST);

            conteudo.add(linha, BorderLayout.CENTER);
        }

        return conteudo;
    }

    private JComponent construirResumoKPIs() {
        PainelSuperficie painel = new PainelSuperficie();
        painel.setLayout(new GridBagLayout());

        painel.add(blocoDeResumo("Pessoas na fila", totalNaFila, Estilo.suave("aguardando chamada"), Icone.FILA, Estilo.paleta().primaria),
                colunaDeResumo(0, 1));
        painel.add(divisorDeResumo(), colunaDeResumo(1, 0));

        painel.add(blocoDeResumo("Tempo médio de espera", tempoMedioEspera, Estilo.suave("estimativa calculada"), Icone.RELOGIO, Estilo.paleta().alerta),
                colunaDeResumo(2, 1));
        painel.add(divisorDeResumo(), colunaDeResumo(3, 0));

        painel.add(blocoDeResumo("Atendimentos realizados", totalAtendidos, Estilo.suave("registrados nesta sessão"), Icone.FINALIZAR, Estilo.paleta().sucesso),
                colunaDeResumo(4, 1));
        painel.add(divisorDeResumo(), colunaDeResumo(5, 0));

        painel.add(blocoDeResumo("Próximo da fila", nomeDoProximo, detalheDoProximo, Icone.ATENDIMENTO, Estilo.paleta().primaria),
                colunaDeResumo(6, 1.2));

        return painel;
    }

    private GridBagConstraints colunaDeResumo(int coluna, double peso) {
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.gridx = coluna;
        restricoes.gridy = 0;
        restricoes.weightx = peso;
        restricoes.weighty = 1;
        restricoes.fill = peso > 0 ? GridBagConstraints.BOTH : GridBagConstraints.VERTICAL;
        restricoes.anchor = GridBagConstraints.NORTH;
        return restricoes;
    }

    private JComponent blocoDeResumo(String rotulo, JLabel valor, JLabel apoio, Icone icone, Color corIcone) {
        valor.setPreferredSize(new Dimension(valor.getPreferredSize().width, 34));

        JLabel iconeRotulo = new JLabel(icone.comoIcone(18, corIcone));

        JPanel linhaRotulo = Estilo.painel();
        linhaRotulo.setLayout(new BorderLayout(8, 0));
        linhaRotulo.add(Estilo.rotulo(rotulo), BorderLayout.WEST);
        linhaRotulo.add(iconeRotulo, BorderLayout.EAST);

        Pilha pilha = new Pilha();
        pilha.adicionar(linhaRotulo, 0, 0, 8);
        pilha.adicionar(valor, 0, 0, 4);
        pilha.adicionar(apoio);

        JPanel moldura = Estilo.painel();
        moldura.setLayout(new BorderLayout());
        moldura.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        moldura.add(pilha, BorderLayout.NORTH);

        return moldura;
    }

    private JComponent divisorDeResumo() {
        JPanel divisor = Estilo.painel();
        divisor.setPreferredSize(new Dimension(1, 0));
        divisor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18, 0, 18, 0),
                BorderFactory.createMatteBorder(0, 1, 0, 0, Estilo.paleta().bordaSuave)));
        return divisor;
    }

    private JComponent construirSecaoDaFila() {
        secaoDaFila = new PainelSecao("Fila de Atendimento em Tempo Real");

        Botao abrirFila = new Botao("Gerenciar Fila", Botao.Variante.SUTIL);
        abrirFila.addActionListener(evento -> acoes.irPara(Pagina.FILA));
        secaoDaFila.adicionarAcao(abrirFila);

        JTable tabela = Tabela.criar();
        tabela.setModel(modelo);
        Tabela.renderizador(tabela, 0, new Renderizadores.Codigo(16, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 1, new Renderizadores.Codigo(16, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 2, new Renderizadores.Texto(16));
        Tabela.renderizador(tabela, 3, new Renderizadores.PrioridadeChip());
        Tabela.renderizador(tabela, 4, new Renderizadores.Texto(16, false,
                Renderizadores.Tom.SECUNDARIO, SwingConstants.LEFT));
        Tabela.renderizador(tabela, 5, new Renderizadores.Situacao());
        Tabela.larguras(tabela, 48, 64, 240, 130, 84, 120);

        areaDaFila = new JPanel(new CardLayout());
        areaDaFila.setOpaque(false);
        areaDaFila.add(Tabela.emRolagem(tabela), CARTAO_TABELA);
        areaDaFila.add(new EstadoVazio(Icone.CAIXA_VAZIA, "A fila está vazia no momento.",
                "Emita senhas pelo Totem ou cadastre consumidores na tela de Fila."), CARTAO_VAZIO);

        secaoDaFila.corpo().add(areaDaFila, BorderLayout.CENTER);

        return secaoDaFila;
    }

    private JComponent construirComposicao() {
        PainelSecao secao = new PainelSecao("Composição da Fila");
        secao.definirInformacao("por prioridade");

        Pilha pilha = new Pilha();

        for (int indice = 0; indice < ORDEM_DE_PRIORIDADE.length; indice++) {
            Prioridade prioridade = ORDEM_DE_PRIORIDADE[indice];
            Color cor = Estilo.prioridade(prioridade).getTexto();

            JLabel rotulo = new JLabel(Apresentacao.prioridade(prioridade));
            rotulo.setFont(Estilo.fonteRegular(13f));
            rotulo.setForeground(Estilo.paleta().texto);
            rotulo.setIcon(new PontoColorido(cor, 8));
            rotulo.setIconTextGap(10);

            JLabel contagem = new JLabel("0");
            contagem.setFont(Estilo.fonteMono(12.5f));
            contagem.setForeground(Estilo.paleta().textoSecundario);

            JLabel percentual = new JLabel("0%");
            percentual.setFont(Estilo.fonteChip(11f));
            percentual.setForeground(Estilo.paleta().textoSuave);

            JPanel infoDireita = Estilo.painel();
            infoDireita.setLayout(new BorderLayout(8, 0));
            infoDireita.add(contagem, BorderLayout.WEST);
            infoDireita.add(percentual, BorderLayout.EAST);

            JPanel linha = Estilo.painel();
            linha.setLayout(new BorderLayout(12, 0));
            linha.add(rotulo, BorderLayout.WEST);
            linha.add(infoDireita, BorderLayout.EAST);

            Barra barra = new Barra(cor);
            contagens.put(prioridade, contagem);
            percentuais.put(prioridade, percentual);
            barras.put(prioridade, barra);

            pilha.adicionar(linha, 0, 0, 6);
            pilha.adicionar(barra, 0, 0, indice == ORDEM_DE_PRIORIDADE.length - 1 ? 0 : 16);
        }

        JPanel moldura = Estilo.painel();
        moldura.setLayout(new BorderLayout());
        moldura.setBorder(BorderFactory.createEmptyBorder(16, 20, 18, 20));
        moldura.add(pilha, BorderLayout.NORTH);

        secao.corpo().add(moldura, BorderLayout.CENTER);

        return secao;
    }

    @Override
    public void atualizar() {
        List<Consumidor> aguardando = servico.aguardando();
        Map<Prioridade, Integer> distribuicao = servico.distribuicaoAguardando();
        int total = aguardando.size();
        Consumidor proximo = aguardando.isEmpty() ? null : aguardando.get(0);

        totalNaFila.setText(String.valueOf(total));
        totalAtendidos.setText(String.valueOf(servico.totalAtendimentos()));

        int mediaMinutos = servico.tempoMedioEsperaMinutos();
        tempoMedioEspera.setText(Apresentacao.esperaCurta(mediaMinutos));

        if (proximo == null) {
            nomeDoProximo.setText("—");
            detalheDoProximo.setText("Nenhuma pessoa aguardando");
        } else {
            nomeDoProximo.setText(proximo.getNome());
            detalheDoProximo.setText(Apresentacao.id(proximo.getId()) + " · "
                    + Apresentacao.prioridadeDestacada(proximo.getPrioridade()));
        }

        int maiorContagem = distribuicao.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        for (Prioridade prioridade : ORDEM_DE_PRIORIDADE) {
            int valor = distribuicao.getOrDefault(prioridade, 0);
            int pct = total > 0 ? Math.round((valor * 100.0f) / total) : 0;

            contagens.get(prioridade).setText(String.valueOf(valor));
            percentuais.get(prioridade).setText(pct + "%");
            barras.get(prioridade).definir(valor, maiorContagem);
        }

        modelo.atualizar();
        painelProximo.atualizar(servico);

        secaoDaFila.definirInformacao(total == 0
                ? "nenhuma pessoa aguardando"
                : total == 1 ? "1 pessoa aguardando" : total + " pessoas aguardando");

        ((CardLayout) areaDaFila.getLayout()).show(areaDaFila,
                total == 0 ? CARTAO_VAZIO : CARTAO_TABELA);

        atualizarAlerta(distribuicao.getOrDefault(Prioridade.URGENTE, 0));
    }

    private void atualizarAlerta(int urgentes) {
        if (!preferencias.isAlertarUrgente() || urgentes == 0) {
            alerta.limpar();
            return;
        }

        alerta.mostrar(urgentes == 1
                        ? "Atenção: 1 consumidor com prioridade URGENTE aguardando atendimento imediato."
                        : "Atenção: " + urgentes + " consumidores com prioridade URGENTE aguardando atendimento imediato.",
                TipoMensagem.ALERTA);
    }

    /** Barra proporcional moderna com cantos arredondados. */
    private static final class Barra extends JComponent {

        private final Color cor;
        private int valor;
        private int maiorValor = 1;

        private Barra(Color cor) {
            this.cor = cor;
            setOpaque(false);
        }

        private void definir(int valor, int maiorValor) {
            this.valor = valor;
            this.maiorValor = Math.max(1, maiorValor);
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 6);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, 6);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);

            Pintura.preencher(g2, 0, 0, getWidth(), getHeight(), 3, Estilo.paleta().superficieAlt);

            if (valor > 0) {
                int largura = Math.max(10, Math.round(getWidth() * (valor / (float) maiorValor)));
                Pintura.preencher(g2, 0, 0, largura, getHeight(), 3, cor);
            }

            g2.dispose();
        }
    }
}
