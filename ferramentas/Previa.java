package ferramentas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import model.Prioridade;
import service.FilaService;
import ui.AcoesJanela;
import ui.Estilo;
import ui.PainelAplicacao;
import ui.Pagina;
import ui.Preferencias;
import ui.Tema;
import ui.TipoMensagem;
import ui.visao.VisaoFila;

/**
 * Ferramenta de desenvolvimento: monta as telas do Smart Queue fora de uma
 * janela e grava imagens em {@code previsoes/} para revisão visual.
 *
 * Não faz parte da aplicação: fica fora de {@code src/} e não é compilada junto
 * com o produto.
 */
public final class Previa {

    private static final File PASTA = new File("previsoes");

    private Previa() {
    }

    public static void main(String[] args) throws Exception {
        PASTA.mkdirs();

        boolean comDepuracao = args.length > 0 && "--dump".equals(args[0]);

        gerar("01-dashboard-claro", 1280, 800, Tema.CLARO, Pagina.DASHBOARD, true, comDepuracao);
        gerar("02-fila-claro", 1280, 800, Tema.CLARO, Pagina.FILA, true);
        gerar("03-atendimento-claro", 1280, 800, Tema.CLARO, Pagina.ATENDIMENTO, true);
        gerar("04-historico-claro", 1280, 800, Tema.CLARO, Pagina.HISTORICO, true);
        gerar("05-configuracoes-claro", 1280, 800, Tema.CLARO, Pagina.CONFIGURACOES, true);
        gerar("06-dashboard-escuro", 1280, 800, Tema.ESCURO, Pagina.DASHBOARD, true);
        gerar("07-dashboard-compacto", 980, 820, Tema.CLARO, Pagina.DASHBOARD, true);
        gerar("08-fila-compacta", 980, 820, Tema.CLARO, Pagina.FILA, true);
        gerar("09-fila-vazia", 1280, 800, Tema.CLARO, Pagina.FILA, false);
        gerar("11-totem-claro", 1280, 800, Tema.CLARO, Pagina.TOTEM, true);
        gerar("12-painel-tv-claro", 1280, 800, Tema.CLARO, Pagina.PAINEL_TV, true);
        gerar("13-painel-tv-escuro", 1280, 800, Tema.ESCURO, Pagina.PAINEL_TV, true);
        gerar("14-atendimento-escuro", 1280, 800, Tema.ESCURO, Pagina.ATENDIMENTO, true);
        gerar("15-totem-escuro", 1280, 800, Tema.ESCURO, Pagina.TOTEM, true);
        gerarMensagem();

        System.out.println("Previsões gravadas em " + PASTA.getAbsolutePath());
        System.exit(0);
    }

    private static void gerar(String nome, int largura, int altura, Tema tema, Pagina pagina,
                              boolean comDados) throws Exception {
        gerar(nome, largura, altura, tema, pagina, comDados, false);
    }

    private static void gerar(String nome, int largura, int altura, Tema tema, Pagina pagina,
                              boolean comDados, boolean comDepuracao) throws Exception {
        FilaService servico = new FilaService();

        if (comDados) {
            semear(servico);
        }

        Preferencias preferencias = new Preferencias();
        preferencias.setTema(tema);

        PainelAplicacao aplicacao = new PainelAplicacao(servico, preferencias);
        aplicacao.setSize(largura, altura);
        aplicacao.reconstruir();
        aplicacao.irPara(pagina);

        if (comDepuracao) {
            pintar(aplicacao, largura, altura, arquivo(nome));
            System.out.println("=== " + nome + " após layout ===");
            desenharArvore(aplicacao, 0);
            return;
        }

        pintar(aplicacao, largura, altura, arquivo(nome));
    }

    private static void desenharArvore(Component componente, int nivel) {
        if (!(componente instanceof Container container)) {
            return;
        }

        StringBuilder recuo = new StringBuilder();

        for (int i = 0; i < nivel; i++) {
            recuo.append("  ");
        }

        String rotulo = switch (componente) {
            case javax.swing.JLabel rotulo2 -> " " + rotulo2.getText();
            default -> "";
        };

        System.out.printf("%s%s%s (%d,%d %dx%d) pref=%dx%d%n",
                recuo,
                componente.getClass().getSimpleName(),
                rotulo,
                componente.getX(), componente.getY(),
                componente.getWidth(), componente.getHeight(),
                componente.getPreferredSize().width, componente.getPreferredSize().height);

        for (Component filho : container.getComponents()) {
            desenharArvore(filho, nivel + 1);
        }
    }

    /** Cenário com histórico e fila preenchida, para ver a tela em uso real. */
    private static void semear(FilaService servico) {
        servico.adicionar("Mariana Lopes", Prioridade.NORMAL);
        servico.adicionar("Rafael Duarte", Prioridade.BAIXA);
        servico.adicionar("Carla Menezes", Prioridade.PRIORIDADE);
        servico.adicionar("João Pereira", Prioridade.URGENTE);
        servico.adicionar("Fernanda Alves", Prioridade.NORMAL);
        servico.adicionar("Diego Martins", Prioridade.BAIXA);
        servico.adicionar("Patrícia Ramos", Prioridade.PRIORIDADE);
        servico.adicionar("Bruno Teixeira", Prioridade.NORMAL);
        servico.adicionar("Sofia Nogueira", Prioridade.URGENTE);

        servico.chamarProximo();
        servico.chamarProximo();
        servico.chamarProximo();
    }

    private static void gerarMensagem() throws Exception {
        FilaService servico = new FilaService();
        semear(servico);

        Preferencias preferencias = new Preferencias();
        preferencias.setTema(Tema.CLARO);
        Estilo.definirTema(Tema.CLARO);

        PainelAplicacao aplicacao = new PainelAplicacao(servico, preferencias);

        VisaoComAviso visao = new VisaoComAviso(servico, preferencias, aplicacao);
        visao.mostrarAviso("Consumidor adicionado à fila: #010 · Ana Beatriz · Normal",
                TipoMensagem.SUCESSO);

        pintar(visao, 1040, 620, arquivo("10-mensagem-sucesso"));
    }

    /**
     * Visão de fila com acesso ao aviso inline normalmente reservado às ações
     * do formulário, para conferir a faixa de retorno visualmente.
     */
    private static final class VisaoComAviso extends VisaoFila {

        private VisaoComAviso(FilaService servico, Preferencias preferencias,
                              AcoesJanela acoes) {
            super(servico, preferencias, acoes, false);
        }

        private void mostrarAviso(String texto, TipoMensagem tipo) {
            avisar(texto, tipo);
        }
    }

    private static void pintar(Component raiz, int largura, int altura, File destino)
            throws Exception {
        layoutRecursivo(raiz);

        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        g.setColor(Estilo.paleta().fundo);
        g.fillRect(0, 0, largura, altura);
        raiz.printAll(g);
        g.dispose();

        ImageIO.write(imagem, "png", destino);
    }

    private static void layoutRecursivo(Component componente) {
        if (!(componente instanceof Container container)) {
            return;
        }

        container.doLayout();

        for (Component filho : container.getComponents()) {
            layoutRecursivo(filho);
        }
    }

    private static File arquivo(String nome) {
        return new File(PASTA, nome + ".png");
    }
}
