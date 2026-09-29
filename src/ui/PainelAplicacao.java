package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import service.FilaService;
import ui.visao.Visao;
import ui.visao.VisaoAtendimento;
import ui.visao.VisaoConfiguracoes;
import ui.visao.VisaoDashboard;
import ui.visao.VisaoFila;
import ui.visao.VisaoHistorico;
import ui.visao.VisaoPainelTV;
import ui.visao.VisaoTotem;

/**
 * Conteúdo da aplicação: navegação lateral, cabeçalho da página, área de
 * conteúdo e rodapé de status. Vive dentro de {@link JanelaPrincipal} e também
 * pode ser montado fora dela (pré-visualização), já que não depende de janela.
 */
public class PainelAplicacao extends JPanel implements AcoesJanela {

    private static final int LARGURA_COMPACTA = 1240;

    private final FilaService servico;
    private final Preferencias preferencias;
    private final Map<Pagina, Visao> visoes = new EnumMap<>(Pagina.class);

    private BarraLateral barraLateral;
    private RodapeStatus rodape;
    private JPanel areaDeVisao;
    private JLabel tituloPagina;
    private JLabel descricaoPagina;
    private JPanel areaDeAcoes;
    private Component origemDeDialogo;

    private Pagina paginaAtual = Pagina.DASHBOARD;
    private boolean compacta;
    private boolean recolhida;

    public PainelAplicacao(FilaService servico, Preferencias preferencias) {
        this.servico = servico;
        this.preferencias = preferencias;

        setLayout(new BorderLayout());

        compacta = recomputarModoCompacto();

        servico.observar(this::atualizarInterface);
        reconstruir();
    }

    public void definirOrigemDeDialogo(Component origem) {
        origemDeDialogo = origem;
    }

    @Override
    public void irPara(Pagina pagina) {
        mostrarPagina(pagina);
    }

    @Override
    public void definirTema(Tema tema) {
        preferencias.setTema(tema);
        reconstruir();
    }

    @Override
    public void registrarMensagem(String texto) {
        if (rodape != null) {
            rodape.definirMensagem(texto);
        }
    }

    @Override
    public boolean confirmar(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(origemDeDialogo, mensagem, "Smart Queue · Confirmação",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        return resposta == JOptionPane.OK_OPTION;
    }

    @Override
    public void atualizarInterface() {
        for (Visao visao : visoes.values()) {
            visao.atualizar();
        }

        atualizarContadores();
    }

    public void reagirRedimensionamento() {
        boolean novoModo = recomputarModoCompacto();

        if (novoModo != compacta) {
            reconstruir();
        }
    }

    public void reconstruir() {
        Estilo.definirTema(preferencias.getTema());
        compacta = recomputarModoCompacto();

        removeAll();
        setLayout(new BorderLayout());
        setBackground(Estilo.paleta().fundo);
        setOpaque(true);

        barraLateral = new BarraLateral(this::irPara, this::alternarNavegacao);
        barraLateral.definirCompacta(compacta);
        barraLateral.definirPagina(paginaAtual);
        add(barraLateral, BorderLayout.WEST);

        JPanel centro = Estilo.painel();
        centro.setOpaque(true);
        centro.setBackground(Estilo.paleta().fundo);
        centro.setLayout(new BorderLayout());
        centro.add(construirCabecalho(), BorderLayout.NORTH);

        areaDeVisao = Estilo.painel();
        areaDeVisao.setOpaque(true);
        areaDeVisao.setBackground(Estilo.paleta().fundo);
        areaDeVisao.setLayout(new BorderLayout());
        centro.add(areaDeVisao, BorderLayout.CENTER);

        rodape = new RodapeStatus();
        centro.add(rodape, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        visoes.clear();
        mostrarPagina(paginaAtual);

        revalidate();
        repaint();
        atualizarContadores();
    }

    private void alternarNavegacao() {
        recolhida = !recolhida;
        reconstruir();
    }

    private boolean recomputarModoCompacto() {
        int largura = getWidth() > 0 ? getWidth() : LARGURA_COMPACTA * 2;

        return recolhida || largura < LARGURA_COMPACTA;
    }

    private JPanel construirCabecalho() {
        JPanel cabecalho = Estilo.painel();
        cabecalho.setOpaque(true);
        cabecalho.setBackground(Estilo.paleta().fundo);
        cabecalho.setLayout(new BorderLayout(24, 0));
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                Estilo.bordaInferior(),
                BorderFactory.createEmptyBorder(16, Estilo.PADDING_CONTEUDO,
                        14, Estilo.PADDING_CONTEUDO)));

        tituloPagina = Estilo.titulo(paginaAtual.getTitulo());
        descricaoPagina = Estilo.subtitulo(paginaAtual.getDescricao());

        Pilha textos = new Pilha();
        textos.adicionar(tituloPagina, 0, 0, 3);
        textos.adicionar(descricaoPagina);

        JPanel bloco = Estilo.painel();
        bloco.setLayout(new BorderLayout());
        bloco.add(textos, BorderLayout.CENTER);

        areaDeAcoes = Estilo.painel();
        areaDeAcoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        cabecalho.add(bloco, BorderLayout.WEST);
        cabecalho.add(areaDeAcoes, BorderLayout.EAST);

        return cabecalho;
    }

    private void mostrarPagina(Pagina pagina) {
        paginaAtual = pagina;

        tituloPagina.setText(pagina.getTitulo());
        descricaoPagina.setText(pagina.getDescricao());

        Visao visao = visaoDe(pagina);

        areaDeVisao.removeAll();
        areaDeVisao.add(visao, BorderLayout.CENTER);

        barraLateral.definirPagina(pagina);
        mostrarAcoesDaPagina(visao);
        visao.atualizar();

        areaDeVisao.revalidate();
        areaDeVisao.repaint();
    }

    private void mostrarAcoesDaPagina(Visao visao) {
        areaDeAcoes.removeAll();

        JComponent acoesDaPagina = visao.acoesDoCabecalho();

        if (acoesDaPagina != null) {
            areaDeAcoes.add(acoesDaPagina);
        }

        areaDeAcoes.revalidate();
        areaDeAcoes.repaint();
    }

    private Visao visaoDe(Pagina pagina) {
        return visoes.computeIfAbsent(pagina, this::criarVisao);
    }

    private Visao criarVisao(Pagina pagina) {
        return switch (pagina) {
            case DASHBOARD -> new VisaoDashboard(servico, preferencias, this, compacta);
            case TOTEM -> new VisaoTotem(servico, preferencias, this, compacta);
            case ATENDIMENTO -> new VisaoAtendimento(servico, preferencias, this, compacta);
            case PAINEL_TV -> new VisaoPainelTV(servico, preferencias, this, compacta);
            case FILA -> new VisaoFila(servico, preferencias, this, compacta);
            case HISTORICO -> new VisaoHistorico(servico, preferencias, this, compacta);
            case CONFIGURACOES -> new VisaoConfiguracoes(servico, preferencias, this, compacta);
        };
    }

    private void atualizarContadores() {
        int aguardando = servico.totalAguardando();
        int atendidos = servico.totalAtendimentos();

        barraLateral.definirContagem(aguardando);
        rodape.definirNumeros(aguardando, atendidos);
    }
}
