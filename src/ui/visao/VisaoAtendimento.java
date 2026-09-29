package ui.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
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
import ui.Pagina;
import ui.PainelSecao;
import ui.PainelSuperficie;
import ui.Pilha;
import ui.Pintura;
import ui.Preferencias;
import ui.TipoMensagem;

/**
 * Console do Atendente: controle total do atendimento com botões
 * "Chamar Próxima", "Rechamar", "Finalizar" e "Pular", seleção de guichê,
 * cronômetro de tempo de atendimento em tempo real e visualização da fila.
 */
public class VisaoAtendimento extends Visao {

    private static final int SEGUINTES_EXIBIDOS = 6;

    private final JComboBox<String> seletorGuiche = new JComboBox<>(new String[]{
            "Guichê 01", "Guichê 02", "Guichê 03", "Guichê 04", "Mesa 01", "Mesa 02"
    });

    private final JLabel labelStatusOperador = new JLabel("● Disponível");
    private final PainelAtendimentoAtivo cardAtivo = new PainelAtendimentoAtivo();
    private final PainelSecao secaoFilaSeguinte = new PainelSecao("A seguir na fila");
    private final Pilha listaSeguintes = new Pilha();
    private final JLabel resumoSeguintes = Estilo.suave("");

    private final Botao btnChamarProxima = new Botao("Chamar Próxima", Icone.SETA, Botao.Variante.PRIMARIO);
    private final Botao btnRechamar = new Botao("Rechamar", Icone.RECHAMAR, Botao.Variante.SECUNDARIO);
    private final Botao btnFinalizar = new Botao("Finalizar", Icone.FINALIZAR, Botao.Variante.PRIMARIO);
    private final Botao btnPular = new Botao("Pular", Icone.PULAR, Botao.Variante.SUTIL);

    private Timer timerCronometro;

    public VisaoAtendimento(FilaService servico, Preferencias preferencias, AcoesJanela acoes, boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        configurarAcoes();
        montar(construirConteudo());
        iniciarCronometro();
        atualizar();
    }

    private void configurarAcoes() {
        seletorGuiche.setFont(Estilo.fonteForte(13f));
        seletorGuiche.setForeground(Estilo.paleta().texto);
        seletorGuiche.setBackground(Estilo.paleta().superficieAlt);
        seletorGuiche.setSelectedItem(servico.getGuicheAtual());
        seletorGuiche.addActionListener(e -> {
            String g = (String) seletorGuiche.getSelectedItem();
            servico.setGuicheAtual(g);
            avisar("Guichê ativo alterado para: " + g, TipoMensagem.INFO);
        });

        btnChamarProxima.definirAltura(44);
        btnChamarProxima.addActionListener(e -> executarChamarProxima());

        btnRechamar.definirAltura(44);
        btnRechamar.addActionListener(e -> executarRechamar());

        btnFinalizar.definirAltura(44);
        btnFinalizar.addActionListener(e -> executarFinalizar());

        btnPular.definirAltura(44);
        btnPular.addActionListener(e -> executarPular());
    }

    private void iniciarCronometro() {
        timerCronometro = new Timer(1000, e -> cardAtivo.atualizarCronometro());
        timerCronometro.start();
    }

    @Override
    public JComponent acoesDoCabecalho() {
        JPanel acoesCabecalho = Estilo.painel();
        acoesCabecalho.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JLabel rotuloGuiche = Estilo.rotulo("Seu Guichê:");
        acoesCabecalho.add(rotuloGuiche);
        acoesCabecalho.add(seletorGuiche);

        return acoesCabecalho;
    }

    private void executarChamarProxima() {
        Consumidor proximo = servico.proximo();
        if (proximo == null) {
            avisar("Não há consumidores aguardando na fila.", TipoMensagem.INFO);
            return;
        }

        if (preferencias.isConfirmarAoChamar() && !acoes.confirmar(
                "Chamar " + Apresentacao.id(proximo.getId()) + " · " + proximo.getNome() + " no " + servico.getGuicheAtual() + "?")) {
            return;
        }

        Consumidor chamado = servico.chamarProximo(servico.getGuicheAtual());
        Estilo.tocarAlertaChamada();

        avisar("Chamando: " + Apresentacao.id(chamado.getId()) + " · " + chamado.getNome() + " no " + servico.getGuicheAtual(), TipoMensagem.SUCESSO);
    }

    private void executarRechamar() {
        Atendimento atual = servico.getAtendimentoAtual();
        if (atual == null) {
            avisar("Nenhum atendimento ativo no momento para rechamar.", TipoMensagem.ALERTA);
            return;
        }
        servico.rechamar();
        Estilo.tocarAlertaChamada();
        avisar("Rechamando senha: " + Apresentacao.id(atual.getConsumidor().getId()) + " · " + atual.getConsumidor().getNome(), TipoMensagem.INFO);
    }

    private void executarFinalizar() {
        Atendimento atual = servico.getAtendimentoAtual();
        if (atual == null) {
            avisar("Nenhum atendimento em andamento para finalizar.", TipoMensagem.INFO);
            return;
        }
        String nome = atual.getConsumidor().getNome();
        servico.finalizarAtendimento();
        avisar("Atendimento finalizado com sucesso: " + nome, TipoMensagem.SUCESSO);
    }

    private void executarPular() {
        Atendimento atual = servico.getAtendimentoAtual();
        if (atual == null) {
            avisar("Nenhum atendimento em andamento para pular.", TipoMensagem.INFO);
            return;
        }
        String nome = atual.getConsumidor().getNome();
        servico.pularAtual();
        avisar("Consumidor marcado como Ausente/Não Compareceu: " + nome, TipoMensagem.ALERTA);
    }

    @Override
    public void atualizar() {
        Atendimento atual = servico.getAtendimentoAtual();
        boolean temAtendimento = (atual != null);
        boolean temFila = (servico.totalAguardando() > 0);

        cardAtivo.atualizar(atual, servico.proximo());

        btnChamarProxima.setEnabled(temFila);
        btnRechamar.setEnabled(temAtendimento);
        btnFinalizar.setEnabled(temAtendimento);
        btnPular.setEnabled(temAtendimento);

        if (temAtendimento) {
            labelStatusOperador.setText("● Ocupado · Em Atendimento");
            labelStatusOperador.setForeground(Estilo.paleta().alerta);
        } else {
            labelStatusOperador.setText("● Disponível para Atendimento");
            labelStatusOperador.setForeground(Estilo.paleta().sucesso);
        }

        atualizarFilaSeguinte();
    }

    private void atualizarFilaSeguinte() {
        List<Consumidor> aguardando = servico.aguardando();
        int total = aguardando.size();

        secaoFilaSeguinte.definirInformacao("");

        listaSeguintes.removeAll();

        List<Consumidor> exibidos = aguardando.subList(0, Math.min(SEGUINTES_EXIBIDOS, total));

        if (exibidos.isEmpty()) {
            JLabel vazio = Estilo.suave("A fila de espera está livre no momento.");
            vazio.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            listaSeguintes.adicionar(vazio);
            resumoSeguintes.setText("");
        } else {
            for (int i = 0; i < exibidos.size(); i++) {
                listaSeguintes.adicionar(linhaDeSeguinte(exibidos.get(i), i + 1, i == exibidos.size() - 1));
            }
            int restantes = total - exibidos.size();
            resumoSeguintes.setText(restantes > 0 ? "+ " + restantes + " outras pessoas na fila" : "");
        }

        listaSeguintes.revalidate();
        listaSeguintes.repaint();
    }

    private JComponent construirConteudo() {
        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout(Estilo.ESPACO, 0));

        // Botões de Ação na barra do Atendente
        btnChamarProxima.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        btnRechamar.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        btnFinalizar.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        btnPular.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel barraAcoes = new PainelSuperficie();
        barraAcoes.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        barraAcoes.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        barraAcoes.add(btnChamarProxima);
        barraAcoes.add(btnRechamar);
        barraAcoes.add(btnFinalizar);
        barraAcoes.add(btnPular);

        Pilha colunaEsquerda = new Pilha();
        colunaEsquerda.adicionar(barraAcoes, 0, 0, Estilo.ESPACO);
        colunaEsquerda.adicionar(cardAtivo, 1);

        // Coluna direita: Fila seguinte
        Botao btnVerFila = new Botao("Ver Fila Completa", Botao.Variante.SUTIL);
        btnVerFila.addActionListener(e -> acoes.irPara(Pagina.FILA));
        secaoFilaSeguinte.adicionarAcao(btnVerFila);

        JPanel interiorFila = Estilo.interior();
        interiorFila.add(listaSeguintes, BorderLayout.NORTH);
        interiorFila.add(resumoSeguintes, BorderLayout.SOUTH);
        secaoFilaSeguinte.corpo().add(interiorFila, BorderLayout.CENTER);

        if (compacta) {
            Pilha coluna = new Pilha();
            coluna.adicionar(colunaEsquerda, 0, 0, Estilo.ESPACO);
            coluna.adicionar(secaoFilaSeguinte, 1);
            conteudo.add(coluna, BorderLayout.CENTER);
        } else {
            conteudo.add(colunaEsquerda, BorderLayout.CENTER);

            secaoFilaSeguinte.setPreferredSize(new Dimension(Estilo.LARGURA_COLUNA_LATERAL, 0));
            conteudo.add(secaoFilaSeguinte, BorderLayout.EAST);
        }

        return conteudo;
    }

    private JComponent linhaDeSeguinte(Consumidor c, int pos, boolean ultima) {
        JPanel linha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Pintura.preparar(g);
                Pintura.preencher(g2, 0, 0, getWidth(), getHeight(), 6, Estilo.paleta().superficieAlt);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        linha.setOpaque(false);
        linha.setLayout(new BorderLayout(8, 0));
        linha.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel posLabel = new JLabel(pos + "º");
        posLabel.setFont(Estilo.fonteMono(11.5f));
        posLabel.setForeground(Estilo.paleta().textoSuave);

        JLabel idLabel = new JLabel(Apresentacao.id(c.getId()));
        idLabel.setFont(Estilo.fonteMono(12f));
        idLabel.setForeground(Estilo.paleta().primaria);

        JLabel nomeLabel = new JLabel(c.getNome());
        nomeLabel.setFont(Estilo.fonteForte(13f));
        nomeLabel.setForeground(Estilo.paleta().texto);

        JPanel esq = Estilo.painel();
        esq.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        esq.add(posLabel);
        esq.add(idLabel);
        esq.add(nomeLabel);

        linha.add(esq, BorderLayout.WEST);
        linha.add(new ChipPrioridade(c.getPrioridade()), BorderLayout.EAST);

        return linha;
    }

    /**
     * Card grande com status do atendimento atual em andamento e cronômetro.
     */
    private static final class PainelAtendimentoAtivo extends PainelSuperficie {

        private final JLabel labelSenha = new JLabel("—");
        private final JLabel labelNome = new JLabel("Nenhum atendimento em andamento");
        private final JLabel labelDetalhes = new JLabel("O operador está livre. Clique em 'Chamar Próxima' para atender.");
        private final JLabel labelCronometro = new JLabel("Tempo: 00:00");
        private final JPanel areaChip = Estilo.painel();

        private LocalDateTime inicioAtendimento;

        private PainelAtendimentoAtivo() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

            labelSenha.setFont(Estilo.fonteDisplay(52f));
            labelSenha.setForeground(Estilo.paleta().primaria);

            labelNome.setFont(Estilo.fonteForte(24f));
            labelNome.setForeground(Estilo.paleta().texto);

            labelDetalhes.setFont(Estilo.fonteRegular(13.5f));
            labelDetalhes.setForeground(Estilo.paleta().textoSecundario);

            labelCronometro.setFont(Estilo.fonteMono(15f));
            labelCronometro.setForeground(Estilo.paleta().textoSecundario);
            labelCronometro.setIcon(Icone.RELOGIO.comoIcone(18, Estilo.paleta().primaria));
            labelCronometro.setIconTextGap(8);

            areaChip.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

            JLabel tituloSecao = Estilo.secao("Status do Atendimento no seu Guichê");

            Pilha pilha = new Pilha();
            pilha.adicionar(tituloSecao, 0, 0, 16);
            pilha.adicionar(labelSenha, 0, 0, 6);
            pilha.adicionar(labelNome, 0, 0, 10);
            pilha.adicionar(areaChip, 0, 0, 14);
            pilha.adicionar(labelDetalhes, 0, 0, 14);
            pilha.adicionar(labelCronometro);

            add(pilha, BorderLayout.CENTER);
        }

        private void atualizar(Atendimento atual, Consumidor proximoNaFila) {
            areaChip.removeAll();

            if (atual == null) {
                inicioAtendimento = null;
                labelSenha.setText("—");
                labelNome.setText("Guichê Livre · Aguardando Chamada");
                labelDetalhes.setText(proximoNaFila != null
                        ? "Próximo da fila pronto para atendimento: " + Apresentacao.id(proximoNaFila.getId()) + " · " + proximoNaFila.getNome()
                        : "Não há consumidores aguardando na fila de espera.");
                labelCronometro.setText("Tempo em atendimento: 00:00");
            } else {
                inicioAtendimento = atual.getHorario();
                Consumidor c = atual.getConsumidor();
                labelSenha.setText(Apresentacao.id(c.getId()));
                labelNome.setText(c.getNome());
                labelDetalhes.setText("Em atendimento no " + atual.getGuiche() + " · Chamado às " + Apresentacao.hora(atual.getHorario()));
                areaChip.add(new ChipPrioridade(c.getPrioridade(), true));
                atualizarCronometro();
            }

            areaChip.revalidate();
            areaChip.repaint();
            revalidate();
            repaint();
        }

        private void atualizarCronometro() {
            if (inicioAtendimento == null) {
                labelCronometro.setText("Tempo em atendimento: 00:00");
                return;
            }
            Duration duracao = Duration.between(inicioAtendimento, LocalDateTime.now());
            long minutos = duracao.toMinutes();
            long segundos = duracao.toSecondsPart();
            labelCronometro.setText(String.format("Tempo em atendimento: %02d:%02d", minutos, segundos));
        }
    }
}
