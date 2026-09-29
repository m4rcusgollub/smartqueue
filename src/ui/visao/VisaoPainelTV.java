package ui.visao;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import model.Consumidor;
import model.Prioridade;
import service.Atendimento;
import service.FilaService;
import ui.AcoesJanela;
import ui.Apresentacao;
import ui.Botao;
import ui.ChipPrioridade;
import ui.Cores;
import ui.Estilo;
import ui.Icone;
import ui.JanelaPainelTV;
import ui.PainelSuperficie;
import ui.Pilha;
import ui.Pintura;
import ui.Preferencias;

/**
 * Painel de chamada para TV e monitores de sala de espera.
 * Apresenta a senha atual em destaque gigante, indicação do guichê, últimas senhas
 * chamadas, relógio em tempo real, data e efeito animado de pulso na chamada.
 */
public class VisaoPainelTV extends Visao {

    private static final DateTimeFormatter FORMATO_HORA_SEGUNDOS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_EXTENSO =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));

    private final PainelChamadaDestaque painelDestaque = new PainelChamadaDestaque();
    private final PainelUltimasChamadas painelUltimas = new PainelUltimasChamadas();
    private final JLabel relogioDigital = new JLabel();
    private final JLabel dataCompleta = new JLabel();

    private Timer timerRelogio;
    private long ultimoTimestampAnimado = 0;

    public VisaoPainelTV(FilaService servico, Preferencias preferencias, AcoesJanela acoes, boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        montar(construirConteudo());
        iniciarRelogio();
        atualizar();
    }

    private void iniciarRelogio() {
        atualizarHorario();
        timerRelogio = new Timer(1000, e -> atualizarHorario());
        timerRelogio.start();
    }

    private void atualizarHorario() {
        LocalDateTime agora = LocalDateTime.now();
        relogioDigital.setText(FORMATO_HORA_SEGUNDOS.format(agora));

        String dataFormatada = FORMATO_DATA_EXTENSO.format(agora);
        dataFormatada = dataFormatada.substring(0, 1).toUpperCase(Locale.ROOT) + dataFormatada.substring(1);
        dataCompleta.setText(dataFormatada);
    }

    @Override
    public JComponent acoesDoCabecalho() {
        Botao btnJanelaTV = new Botao("Abrir em Janela Dedicada (TV)", Icone.EXPANDIR, Botao.Variante.PRIMARIO);
        btnJanelaTV.addActionListener(e -> {
            JanelaPainelTV janela = new JanelaPainelTV(servico, preferencias);
            janela.setVisible(true);
        });
        return btnJanelaTV;
    }

    @Override
    public void atualizar() {
        Atendimento atual = servico.getAtendimentoAtual();
        if (atual == null) {
            atual = servico.ultimoAtendimento();
        }

        painelDestaque.atualizar(atual);
        painelUltimas.atualizar(servico.historico(), atual);

        // Se uma nova chamada ocorreu, disparar animação de pulso no destaque
        long ts = servico.getUltimaChamadaTimestamp();
        if (ts > 0 && ts != ultimoTimestampAnimado) {
            ultimoTimestampAnimado = ts;
            painelDestaque.dispararAnimacaoChamada();
        }
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout(0, Estilo.ESPACO));

        // Área principal da TV: Centro (Destaque Gigante) + Direita (Últimas Chamadas)
        if (compacta) {
            Pilha coluna = new Pilha();
            coluna.adicionar(painelDestaque, 0, 0, Estilo.ESPACO);
            coluna.adicionar(painelUltimas, 0, 0, Estilo.ESPACO);
            coluna.adicionar(construirRodapeTV());
            conteudo.add(coluna, BorderLayout.CENTER);
        } else {
            JPanel grid = Estilo.painel();
            grid.setLayout(new BorderLayout(Estilo.ESPACO, 0));
            grid.add(painelDestaque, BorderLayout.CENTER);

            painelUltimas.setPreferredSize(new Dimension(380, 0));
            grid.add(painelUltimas, BorderLayout.EAST);

            conteudo.add(grid, BorderLayout.CENTER);
            conteudo.add(construirRodapeTV(), BorderLayout.SOUTH);
        }

        return conteudo;
    }

    private JComponent construirRodapeTV() {
        PainelSuperficie rodape = new PainelSuperficie();
        rodape.setLayout(new BorderLayout(20, 0));
        rodape.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        // Lado esquerdo: Mensagem de orientação
        JLabel msg = Estilo.dado("Por favor, atente-se ao painel sonoro e dirija-se ao guichê indicado.");
        msg.setIcon(Icone.SOM.comoIcone(18, Estilo.paleta().primaria));
        msg.setIconTextGap(10);

        // Lado direito: Relógio e Data em tempo real
        relogioDigital.setFont(Estilo.fonteMono(18f));
        relogioDigital.setForeground(Estilo.paleta().texto);

        dataCompleta.setFont(Estilo.fonteRegular(13f));
        dataCompleta.setForeground(Estilo.paleta().textoSecundario);

        JPanel ladoHorario = Estilo.painel();
        ladoHorario.setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        ladoHorario.add(dataCompleta);
        ladoHorario.add(new JLabel("·"));
        ladoHorario.add(relogioDigital);

        rodape.add(msg, BorderLayout.WEST);
        rodape.add(ladoHorario, BorderLayout.EAST);

        return rodape;
    }

    /**
     * Card gigante de chamada com animação pulsante ao chamar nova senha.
     */
    public static final class PainelChamadaDestaque extends PainelSuperficie {

        private final JLabel labelSenha = new JLabel("—");
        private final JLabel labelNome = new JLabel("Aguardando próxima chamada...");
        private final JLabel labelGuiche = new JLabel("GUICHÊ —");
        private final JPanel areaChip = Estilo.painel();

        private Timer timerPulso;
        private float pulsoAlfa = 0f;
        private boolean pulsoCrescendo = true;

        public PainelChamadaDestaque() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

            labelSenha.setFont(Estilo.fonteDisplay(76f));
            labelSenha.setForeground(Estilo.paleta().primaria);
            labelSenha.setHorizontalAlignment(SwingConstants.CENTER);

            labelNome.setFont(Estilo.fonteForte(28f));
            labelNome.setForeground(Estilo.paleta().texto);
            labelNome.setHorizontalAlignment(SwingConstants.CENTER);

            labelGuiche.setFont(Estilo.fonteDisplay(32f));
            labelGuiche.setForeground(Estilo.paleta().sobrePrimaria);
            labelGuiche.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel bannerGuiche = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = Pintura.preparar(g);
                    Pintura.preencher(g2, 0, 0, getWidth(), getHeight(), 12, Estilo.paleta().primaria);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            bannerGuiche.setOpaque(false);
            bannerGuiche.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
            bannerGuiche.add(labelGuiche, BorderLayout.CENTER);

            JLabel tagTitulo = new JLabel("SENHA CHAMADA");
            tagTitulo.setFont(Estilo.fonteChip(13f));
            tagTitulo.setForeground(Estilo.paleta().textoSecundario);
            tagTitulo.setHorizontalAlignment(SwingConstants.CENTER);

            areaChip.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

            JPanel centro = Estilo.painel();
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

            tagTitulo.setAlignmentX(CENTER_ALIGNMENT);
            labelSenha.setAlignmentX(CENTER_ALIGNMENT);
            areaChip.setAlignmentX(CENTER_ALIGNMENT);
            labelNome.setAlignmentX(CENTER_ALIGNMENT);
            bannerGuiche.setAlignmentX(CENTER_ALIGNMENT);

            centro.add(tagTitulo);
            centro.add(Box.createRigidArea(new Dimension(0, 10)));
            centro.add(labelSenha);
            centro.add(Box.createRigidArea(new Dimension(0, 10)));
            centro.add(areaChip);
            centro.add(Box.createRigidArea(new Dimension(0, 16)));
            centro.add(labelNome);
            centro.add(Box.createRigidArea(new Dimension(0, 24)));
            centro.add(bannerGuiche);

            add(centro, BorderLayout.CENTER);
        }

        public void dispararAnimacaoChamada() {
            if (timerPulso != null && timerPulso.isRunning()) {
                timerPulso.stop();
            }
            pulsoAlfa = 1.0f;
            pulsoCrescendo = false;

            timerPulso = new Timer(40, e -> {
                if (pulsoCrescendo) {
                    pulsoAlfa += 0.08f;
                    if (pulsoAlfa >= 1.0f) {
                        pulsoAlfa = 1.0f;
                        pulsoCrescendo = false;
                    }
                } else {
                    pulsoAlfa -= 0.04f;
                    if (pulsoAlfa <= 0.0f) {
                        pulsoAlfa = 0.0f;
                        timerPulso.stop();
                    }
                }
                repaint();
            });
            timerPulso.start();
        }

        public void atualizar(Atendimento atendimento) {
            areaChip.removeAll();

            if (atendimento == null) {
                labelSenha.setText("—");
                labelNome.setText("Nenhuma senha chamada no momento");
                labelGuiche.setText("AGUARDANDO ATENDIMENTO");
            } else {
                Consumidor c = atendimento.getConsumidor();
                labelSenha.setText(Apresentacao.id(c.getId()));
                labelNome.setText(c.getNome());
                labelGuiche.setText(atendimento.getGuiche().toUpperCase(Locale.ROOT));

                ChipPrioridade chip = new ChipPrioridade(c.getPrioridade(), true);
                areaChip.add(chip);
            }

            areaChip.revalidate();
            areaChip.repaint();
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (pulsoAlfa > 0.01f) {
                Graphics2D g2 = Pintura.preparar(g);
                int w = getWidth();
                int h = getHeight();

                Color corNeon = Cores.comAlfa(Estilo.paleta().primaria, Math.round(pulsoAlfa * 140));
                g2.setColor(corNeon);
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, w - 5, h - 5, Estilo.RAIO_PAINEL, Estilo.RAIO_PAINEL);

                g2.dispose();
            }
        }
    }

    /**
     * Lista com as últimas senhas chamadas (histórico recente).
     */
    public static final class PainelUltimasChamadas extends PainelSuperficie {

        private final Pilha listaCards = new Pilha();

        public PainelUltimasChamadas() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titulo = Estilo.secao("Últimas Chamadas");
            titulo.setIcon(Icone.HISTORICO.comoIcone(18, Estilo.paleta().textoSecundario));
            titulo.setIconTextGap(8);

            JPanel cabecalho = Estilo.painel();
            cabecalho.setLayout(new BorderLayout());
            cabecalho.setBorder(BorderFactory.createCompoundBorder(
                    Estilo.bordaInferiorSuave(),
                    BorderFactory.createEmptyBorder(0, 0, 12, 0)
            ));
            cabecalho.add(titulo, BorderLayout.WEST);

            add(cabecalho, BorderLayout.NORTH);
            add(listaCards, BorderLayout.CENTER);
        }

        public void atualizar(List<Atendimento> historico, Atendimento atual) {
            listaCards.removeAll();

            if (historico.isEmpty()) {
                JLabel vazio = Estilo.suave("Nenhuma chamada anterior.");
                vazio.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                listaCards.adicionar(vazio);
            } else {
                // Exibe as últimas até 5 chamadas (ignorando a atual se estiver em destaque)
                int exibidos = 0;
                for (Atendimento at : historico) {
                    if (at == atual && exibidos == 0 && historico.size() > 1) {
                        continue;
                    }
                    listaCards.adicionar(linhaDeChamada(at), 0, 0, 8);
                    exibidos++;
                    if (exibidos >= 5) break;
                }
            }

            listaCards.revalidate();
            listaCards.repaint();
        }

        private JComponent linhaDeChamada(Atendimento at) {
            JPanel card = new JPanel(new BorderLayout(0, 4)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = Pintura.preparar(g);
                    Pintura.caixa(g2, 0, 0, getWidth(), getHeight(), 8,
                            Estilo.paleta().superficieAlt, Estilo.paleta().bordaSuave);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            JLabel senha = new JLabel(Apresentacao.id(at.getConsumidor().getId()));
            senha.setFont(Estilo.fonteDisplay(16f));
            senha.setForeground(Estilo.paleta().primaria);

            JLabel nome = new JLabel(at.getConsumidor().getNome());
            nome.setFont(Estilo.fonteForte(13.5f));
            nome.setForeground(Estilo.paleta().texto);

            JPanel linhaCimaEsq = Estilo.painel();
            linhaCimaEsq.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
            linhaCimaEsq.add(senha);
            linhaCimaEsq.add(nome);

            JPanel linhaCima = Estilo.painel();
            linhaCima.setLayout(new BorderLayout());
            linhaCima.add(linhaCimaEsq, BorderLayout.WEST);
            linhaCima.add(new ChipPrioridade(at.getConsumidor().getPrioridade()), BorderLayout.EAST);

            JLabel guiche = new JLabel(at.getGuiche());
            guiche.setFont(Estilo.fonteForte(12f));
            guiche.setForeground(Estilo.paleta().textoSecundario);

            JLabel hora = new JLabel("Chamado às " + Apresentacao.hora(at.getHorario()));
            hora.setFont(Estilo.fonteMono(11.5f));
            hora.setForeground(Estilo.paleta().textoSuave);

            JPanel linhaBaixo = Estilo.painel();
            linhaBaixo.setLayout(new BorderLayout());
            linhaBaixo.add(guiche, BorderLayout.WEST);
            linhaBaixo.add(hora, BorderLayout.EAST);

            card.add(linhaCima, BorderLayout.NORTH);
            card.add(linhaBaixo, BorderLayout.SOUTH);

            return card;
        }
    }
}
