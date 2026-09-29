package ui.visao;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import model.Consumidor;
import model.Prioridade;
import service.FilaService;
import ui.AcoesJanela;
import ui.Apresentacao;
import ui.Botao;
import ui.ChipPrioridade;
import ui.Cores;
import ui.Estilo;
import ui.Icone;
import ui.PainelSuperficie;
import ui.Pilha;
import ui.Pintura;
import ui.Preferencias;
import ui.TipoMensagem;

/**
 * Totem de Autoatendimento para emissão de senhas de toque único.
 * Design premium inspirado em quiosques de hospitais e bancos digitais modernos,
 * com cartões grandes, feedback tátil/visual e simulação de comprovante impresso.
 */
public class VisaoTotem extends Visao {

    private final JTextField campoNome = new JTextField();
    private final JPanel areaComprovante = Estilo.painel();
    private final JLabel statusFilaGeral = Estilo.suave("");

    private CartaoTotem cardNormal;
    private CartaoTotem cardPreferencial;
    private CartaoTotem cardUrgente;
    private Timer timerAutoReset;

    public VisaoTotem(FilaService servico, Preferencias preferencias, AcoesJanela acoes, boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        montar(construirConteudo());
        atualizar();
    }

    @Override
    public void atualizar() {
        int total = servico.totalAguardando();
        var dist = servico.distribuicaoAguardando();

        statusFilaGeral.setText(total == 0
                ? "Atendimento imediato · Nenhuma pessoa aguardando na fila"
                : total + (total == 1 ? " pessoa aguardando atendimento agora" : " pessoas aguardando atendimento agora"));

        if (cardNormal != null) {
            cardNormal.definirAguardando(dist.getOrDefault(Prioridade.NORMAL, 0));
        }
        if (cardPreferencial != null) {
            cardPreferencial.definirAguardando(dist.getOrDefault(Prioridade.PRIORIDADE, 0));
        }
        if (cardUrgente != null) {
            cardUrgente.definirAguardando(dist.getOrDefault(Prioridade.URGENTE, 0));
        }
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout(0, Estilo.ESPACO));

        // Topo com banner acolhedor
        JPanel banner = new PainelSuperficie();
        banner.setLayout(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel tituloBanner = new JLabel("Totem de Autoatendimento");
        tituloBanner.setFont(Estilo.fonteDisplay(22f));
        tituloBanner.setForeground(Estilo.paleta().texto);

        JLabel subtituloBanner = Estilo.subtitulo("Selecione abaixo a categoria de atendimento para retirar sua senha impressa.");

        Pilha textosBanner = new Pilha();
        textosBanner.adicionar(tituloBanner, 0, 0, 4);
        textosBanner.adicionar(subtituloBanner, 0, 0, 8);
        textosBanner.adicionar(statusFilaGeral);

        banner.add(textosBanner, BorderLayout.WEST);

        // Campo de nome opcional estilizado
        JPanel inputPanel = new PainelSuperficie();
        inputPanel.setLayout(new BorderLayout(14, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel labelNome = Estilo.secao("Identificação (opcional):");
        labelNome.setIcon(Icone.USUARIO.comoIcone(18, Estilo.paleta().primaria));
        labelNome.setIconTextGap(8);

        campoNome.setFont(Estilo.fonteRegular(14f));
        campoNome.setForeground(Estilo.paleta().texto);
        campoNome.setBackground(Estilo.paleta().superficieAlt);
        campoNome.setCaretColor(Estilo.paleta().primaria);
        campoNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.paleta().borda, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        campoNome.setPreferredSize(new Dimension(320, Estilo.ALTURA_CAMPO));

        JLabel dicaNome = Estilo.suave("Se desejar, informe seu nome para ser chamado pelo nome no painel.");

        JPanel ladoNome = Estilo.painel();
        ladoNome.setLayout(new BorderLayout(12, 0));
        ladoNome.add(labelNome, BorderLayout.WEST);
        ladoNome.add(campoNome, BorderLayout.CENTER);

        inputPanel.add(ladoNome, BorderLayout.WEST);
        inputPanel.add(dicaNome, BorderLayout.EAST);

        // Grade com 3 cartões grandes de autoatendimento
        JPanel gradeCartoes = Estilo.painel();
        gradeCartoes.setLayout(new GridLayout(compacta ? 3 : 1, compacta ? 1 : 3, 20, 20));

        cardNormal = new CartaoTotem(
                "Atendimento Geral",
                "Consultas normais, serviços administrativos e rotina",
                "Normal",
                Prioridade.NORMAL,
                Icone.USUARIO,
                Estilo.prioridade(Prioridade.NORMAL).getTexto(),
                () -> emitirSenha(Prioridade.NORMAL)
        );

        cardPreferencial = new CartaoTotem(
                "Atendimento Preferencial",
                "Idosos (60+), gestantes, lactantes, PCD ou mobilidade reduzida",
                "Prioridade Legal",
                Prioridade.PRIORIDADE,
                Icone.ESTRELA,
                Estilo.prioridade(Prioridade.PRIORIDADE).getTexto(),
                () -> emitirSenha(Prioridade.PRIORIDADE)
        );

        cardUrgente = new CartaoTotem(
                "Atendimento Urgente",
                "Casos graves, emergências prioritárias e suporte imediato",
                "Prioridade Máxima",
                Prioridade.URGENTE,
                Icone.RAIO,
                Estilo.prioridade(Prioridade.URGENTE).getTexto(),
                () -> emitirSenha(Prioridade.URGENTE)
        );

        gradeCartoes.add(cardNormal);
        gradeCartoes.add(cardPreferencial);
        gradeCartoes.add(cardUrgente);

        // Painel central de ticket / feedback
        areaComprovante.setLayout(new BorderLayout());
        areaComprovante.setVisible(false);

        Pilha pilhaPrincipal = new Pilha();
        pilhaPrincipal.adicionar(banner, 0, 0, 16);
        pilhaPrincipal.adicionar(inputPanel, 0, 0, 16);
        pilhaPrincipal.adicionar(gradeCartoes, 0, 0, 16);
        pilhaPrincipal.adicionar(areaComprovante);

        conteudo.add(pilhaPrincipal, BorderLayout.CENTER);

        return conteudo;
    }

    private void emitirSenha(Prioridade prioridade) {
        String nomeDigitado = campoNome.getText().trim();
        String nomeConsumidor = nomeDigitado.isEmpty() ? "Visitante (" + Apresentacao.prioridade(prioridade) + ")" : nomeDigitado;

        Consumidor consumidor = servico.adicionar(nomeConsumidor, prioridade);
        Estilo.tocarAlertaChamada();

        avisar("Senha emitida com sucesso: " + Apresentacao.id(consumidor.getId()) + " (" + Apresentacao.prioridade(prioridade) + ")", TipoMensagem.SUCESSO);

        // Exibir comprovante de ticket impresso
        exibirTicketEmitido(consumidor);

        campoNome.setText("");
    }

    private void exibirTicketEmitido(Consumidor consumidor) {
        areaComprovante.removeAll();

        PainelTicketImpresso ticket = new PainelTicketImpresso(consumidor, servico, this::fecharTicket);
        areaComprovante.add(ticket, BorderLayout.CENTER);
        areaComprovante.setVisible(true);
        areaComprovante.revalidate();
        areaComprovante.repaint();

        // Rolagem / auto-fechamento do comprovante em 10 segundos
        if (timerAutoReset != null && timerAutoReset.isRunning()) {
            timerAutoReset.stop();
        }
        timerAutoReset = new Timer(10000, evento -> fecharTicket());
        timerAutoReset.setRepeats(false);
        timerAutoReset.start();
    }

    private void fecharTicket() {
        if (timerAutoReset != null) {
            timerAutoReset.stop();
        }
        areaComprovante.setVisible(false);
        areaComprovante.removeAll();
        areaComprovante.revalidate();
        areaComprovante.repaint();
    }

    /**
     * Cartão grande e clicável para emissão de senha no totem.
     */
    private static final class CartaoTotem extends JPanel {

        private final String titulo;
        private final String descricao;
        private final String badge;
        private final Prioridade prioridade;
        private final Icone icone;
        private final Color corTema;
        private final Runnable aoClicar;

        private int aguardando = 0;
        private boolean sobre;

        private CartaoTotem(String titulo, String descricao, String badge,
                            Prioridade prioridade, Icone icone, Color corTema, Runnable aoClicar) {
            this.titulo = titulo;
            this.descricao = descricao;
            this.badge = badge;
            this.prioridade = prioridade;
            this.icone = icone;
            this.corTema = corTema;
            this.aoClicar = aoClicar;

            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(24, 22, 22, 22));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobre = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    aoClicar.run();
                }
            });

            montarConteudo();
        }

        private void definirAguardando(int quantidade) {
            this.aguardando = quantidade;
            repaint();
        }

        private void montarConteudo() {
            removeAll();

            // Topo do card: Badge e Ícone
            JPanel topo = Estilo.painel();
            topo.setLayout(new BorderLayout());

            JLabel badgeLabel = new JLabel("  " + badge.toUpperCase() + "  ");
            badgeLabel.setFont(Estilo.fonteChip(10.5f));
            badgeLabel.setForeground(corTema);
            badgeLabel.setOpaque(true);
            badgeLabel.setBackground(Cores.comAlfa(corTema, 26));
            badgeLabel.setBorder(BorderFactory.createLineBorder(Cores.comAlfa(corTema, 60), 1));

            JLabel iconeLabel = new JLabel(icone.comoIcone(26, corTema));

            topo.add(iconeLabel, BorderLayout.WEST);
            topo.add(badgeLabel, BorderLayout.EAST);

            // Centro: Título e Descrição
            JLabel labelTitulo = new JLabel(titulo);
            labelTitulo.setFont(Estilo.fonteForte(17f));
            labelTitulo.setForeground(Estilo.paleta().texto);

            JLabel labelDesc = new JLabel("<html><body style='width: 170px;'>" + descricao + "</body></html>");
            labelDesc.setFont(Estilo.fonteRegular(12.5f));
            labelDesc.setForeground(Estilo.paleta().textoSecundario);

            JPanel centro = Estilo.painel();
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
            centro.add(Box.createRigidArea(new Dimension(0, 16)));
            centro.add(labelTitulo);
            centro.add(Box.createRigidArea(new Dimension(0, 8)));
            centro.add(labelDesc);
            centro.add(Box.createRigidArea(new Dimension(0, 18)));

            // Rodapé: Botão de toque
            Botao botaoEmitir = new Botao("Retirar Senha", Icone.TICKET, Botao.Variante.PRIMARIO);
            botaoEmitir.definirAltura(44);
            botaoEmitir.setFont(Estilo.fonteForte(14f));
            botaoEmitir.ocuparLargura();
            botaoEmitir.addActionListener(e -> aoClicar.run());

            add(topo, BorderLayout.NORTH);
            add(centro, BorderLayout.CENTER);
            add(botaoEmitir, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Pintura.preparar(g);
            int w = getWidth();
            int h = getHeight();

            Color bg = sobre ? Estilo.paleta().superficieAlt : Estilo.paleta().superficie;
            Color borda = sobre ? corTema : Estilo.paleta().borda;

            // Sombra suave
            Pintura.sombra(g2, 0, 0, w, h, Estilo.RAIO_CARD_TOTEM, Estilo.paleta().sombra);

            // Fundo e borda com destaque
            Pintura.preencher(g2, 0, 0, w, h, Estilo.RAIO_CARD_TOTEM, bg);
            Pintura.contornar(g2, 0, 0, w, h, Estilo.RAIO_CARD_TOTEM, borda, sobre ? 2f : 1f);

            // Faixa superior iluminada
            g2.setColor(corTema);
            g2.fillRoundRect(1, 1, w - 2, 4, 4, 4);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Comprovante de senha impresso simulado, com estilo de cupom térmico e código de barras.
     */
    private static final class PainelTicketImpresso extends PainelSuperficie {

        private PainelTicketImpresso(Consumidor consumidor, FilaService servico, Runnable aoConcluir) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.paleta().primaria, 2),
                    BorderFactory.createEmptyBorder(22, 28, 22, 28)
            ));

            JPanel container = Estilo.painel();
            container.setLayout(new BorderLayout(24, 0));

            // Coluna esquerda: bilhete impresso
            JPanel ticketStub = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = Pintura.preparar(g);
                    g2.setColor(Estilo.paleta().superficieAlt);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(Estilo.paleta().borda);
                    g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{4, 4}, 0));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                    // Efeito de código de barras simulado
                    g2.setColor(Estilo.paleta().textoSecundario);
                    int barX = 20;
                    int barY = getHeight() - 28;
                    int[] pads = {2, 4, 1, 3, 5, 2, 1, 4, 3, 2, 5, 1, 3, 2, 4, 1, 5, 2, 3};
                    for (int p : pads) {
                        g2.fillRect(barX, barY, p, 16);
                        barX += p + 3;
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            ticketStub.setOpaque(false);
            ticketStub.setLayout(new BoxLayout(ticketStub, BoxLayout.Y_AXIS));
            ticketStub.setBorder(BorderFactory.createEmptyBorder(18, 20, 36, 20));
            ticketStub.setPreferredSize(new Dimension(280, 180));

            JLabel headerStub = new JLabel("SMART QUEUE · SENHA");
            headerStub.setFont(Estilo.fonteChip(11f));
            headerStub.setForeground(Estilo.paleta().textoSecundario);
            headerStub.setAlignmentX(CENTER_ALIGNMENT);

            JLabel senhaStub = new JLabel(Apresentacao.id(consumidor.getId()));
            senhaStub.setFont(Estilo.fonteDisplay(42f));
            senhaStub.setForeground(Estilo.paleta().primaria);
            senhaStub.setAlignmentX(CENTER_ALIGNMENT);

            ChipPrioridade chip = new ChipPrioridade(consumidor.getPrioridade(), true);
            chip.setAlignmentX(CENTER_ALIGNMENT);

            JLabel horaStub = new JLabel(Apresentacao.dataHora(LocalDateTime.now()));
            horaStub.setFont(Estilo.fonteMono(11f));
            horaStub.setForeground(Estilo.paleta().textoSuave);
            horaStub.setAlignmentX(CENTER_ALIGNMENT);

            ticketStub.add(headerStub);
            ticketStub.add(Box.createRigidArea(new Dimension(0, 6)));
            ticketStub.add(senhaStub);
            ticketStub.add(Box.createRigidArea(new Dimension(0, 6)));
            ticketStub.add(chip);
            ticketStub.add(Box.createRigidArea(new Dimension(0, 8)));
            ticketStub.add(horaStub);

            // Coluna direita: Orientações e botão de conclusão
            JPanel infoPanel = Estilo.painel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            JLabel sucessoTitulo = new JLabel("Sua senha foi emitida com sucesso!");
            sucessoTitulo.setFont(Estilo.fonteForte(20f));
            sucessoTitulo.setForeground(Estilo.paleta().sucesso);
            sucessoTitulo.setIcon(Icone.OK.comoIcone(22, Estilo.paleta().sucesso));
            sucessoTitulo.setIconTextGap(10);

            JLabel nomeCliente = new JLabel("Consumidor: " + consumidor.getNome());
            nomeCliente.setFont(Estilo.fonteForte(15f));
            nomeCliente.setForeground(Estilo.paleta().texto);

            int posicao = servico.aguardando().indexOf(consumidor) + 1;
            int esperaMedia = servico.tempoMedioEsperaMinutos();

            JLabel detalhesFila = new JLabel(String.format(
                    "Posição atual na fila: %dº lugar · Tempo estimado de espera: ~%d min",
                    Math.max(1, posicao), Math.max(1, esperaMedia)
            ));
            detalhesFila.setFont(Estilo.fonteRegular(13.5f));
            detalhesFila.setForeground(Estilo.paleta().textoSecundario);

            JLabel avisoPainel = new JLabel("Acompanhe a chamada pelo Painel de TV e sinal sonoro da recepção.");
            avisoPainel.setFont(Estilo.fonteRegular(13f));
            avisoPainel.setForeground(Estilo.paleta().textoSuave);

            Botao btnFechar = new Botao("Concluir e Retirar Ticket", Icone.OK, Botao.Variante.PRIMARIO);
            btnFechar.definirAltura(42);
            btnFechar.addActionListener(e -> aoConcluir.run());

            infoPanel.add(sucessoTitulo);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            infoPanel.add(nomeCliente);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            infoPanel.add(detalhesFila);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            infoPanel.add(avisoPainel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 16)));
            infoPanel.add(btnFechar);

            container.add(ticketStub, BorderLayout.WEST);
            container.add(infoPanel, BorderLayout.CENTER);

            add(container, BorderLayout.CENTER);
        }
    }
}
