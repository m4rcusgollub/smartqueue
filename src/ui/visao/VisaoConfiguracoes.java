package ui.visao;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import service.FilaService;
import ui.AcoesJanela;
import ui.CaixaSelecao;
import ui.Estilo;
import ui.Pilha;
import ui.Preferencias;
import ui.SeletorSegmentado;
import ui.Tema;

/**
 * Configurações reais do sistema: preferências de interface que alteram o
 * comportamento das telas e informações somente leitura da fila.
 */
public class VisaoConfiguracoes extends Visao {

    private CaixaSelecao confirmarAoChamar;
    private CaixaSelecao alertarUrgente;
    private CaixaSelecao limparNome;

    public VisaoConfiguracoes(FilaService servico, Preferencias preferencias, AcoesJanela acoes,
                              boolean compacta) {
        super(servico, preferencias, acoes, compacta);

        montar(construirConteudo());
        atualizar();
    }

    @Override
    public void atualizar() {
        confirmarAoChamar.setSelected(preferencias.isConfirmarAoChamar());
        alertarUrgente.setSelected(preferencias.isAlertarUrgente());
        limparNome.setSelected(preferencias.isLimparNomeAoAdicionar());
    }

    private JComponent construirConteudo() {
        confirmarAoChamar = new CaixaSelecao("Confirmar antes de chamar o próximo",
                preferencias.isConfirmarAoChamar());
        confirmarAoChamar.addActionListener(evento -> {
            preferencias.setConfirmarAoChamar(confirmarAoChamar.isSelected());
            acoes.registrarMensagem("Preferência de confirmação atualizada.");
        });

        alertarUrgente = new CaixaSelecao("Alertar quando houver consumidor urgente aguardando",
                preferencias.isAlertarUrgente());
        alertarUrgente.addActionListener(evento -> {
            preferencias.setAlertarUrgente(alertarUrgente.isSelected());
            acoes.atualizarInterface();
        });

        limparNome = new CaixaSelecao("Limpar o campo de nome após adicionar",
                preferencias.isLimparNomeAoAdicionar());
        limparNome.addActionListener(evento -> {
            preferencias.setLimparNomeAoAdicionar(limparNome.isSelected());
            acoes.registrarMensagem("Preferência de cadastro atualizada.");
        });

        Pilha coluna = new Pilha();
        coluna.adicionar(Estilo.grupo("Aparência"), 0, 0, 12);
        coluna.adicionar(linhaDeControle("Tema",
                "Aplicado imediatamente em toda a interface.", construirSeletorDeTema()));

        coluna.adicionar(Estilo.grupo("Atendimento"), 0, 30, 12);
        coluna.adicionar(linhaDeOpcao(confirmarAoChamar,
                "Exibe o ID e o nome antes de registrar a chamada."));
        coluna.adicionar(linhaDeOpcao(alertarUrgente,
                "Mostra um aviso no dashboard enquanto houver urgente na fila."));
        coluna.adicionar(linhaDeOpcao(limparNome,
                "Deixa o formulário pronto para o próximo cadastro."));

        coluna.adicionar(Estilo.grupo("Fila"), 0, 30, 12);
        coluna.adicionar(linhaInformativa("Critério de chamada",
                "Prioridade, de Urgente para Baixa"));
        coluna.adicionar(linhaInformativa("Desempate entre prioridades iguais",
                "Ordem interna da PriorityQueue"));
        coluna.adicionar(linhaInformativa("Capacidade", "Sem limite definido"));
        coluna.adicionar(linhaInformativa("Regra de negócio",
                "model.FilaPrioridade · PriorityQueue + Comparator"));

        coluna.adicionar(Estilo.grupo("Sistema"), 0, 30, 12);
        coluna.adicionar(linhaInformativa("Aplicação", "Smart Queue 1.0.0"));
        coluna.adicionar(linhaInformativa("Interface", "Java Swing · tema claro e escuro"));
        coluna.adicionar(linhaInformativa("Versão do Java",
                System.getProperty("java.version", "não identificada")));
        coluna.adicionar(linhaInformativa("Persistência",
                "Em memória — dados descartados ao fechar o sistema"));

        JPanel conteudo = Estilo.painel();
        conteudo.setLayout(new BorderLayout());
        conteudo.add(rolagem(coluna), BorderLayout.CENTER);

        return conteudo;
    }

    private JComponent construirSeletorDeTema() {
        Tema[] temas = Tema.values();
        String[] rotulos = new String[temas.length];

        for (int indice = 0; indice < temas.length; indice++) {
            rotulos[indice] = temas[indice].getRotulo();
        }

        SeletorSegmentado seletor = new SeletorSegmentado(rotulos,
                preferencias.getTema() == Tema.ESCURO ? 1 : 0);
        seletor.setPreferredSize(new Dimension(190, Estilo.ALTURA_CAMPO));
        seletor.aoAlterar(indice -> acoes.definirTema(temas[indice]));

        return seletor;
    }

    private JComponent linhaDeControle(String titulo, String descricao, JComponent controle) {
        JLabel rotulo = Estilo.dado(titulo);
        rotulo.setFont(Estilo.fonteForte(13f));

        Pilha textos = new Pilha();
        textos.adicionar(rotulo, 0, 0, 4);
        textos.adicionar(Estilo.suave(descricao));

        JPanel linha = Estilo.painel();
        linha.setLayout(new BorderLayout(24, 0));
        linha.setBorder(linhaComDivisor());
        linha.add(textos, BorderLayout.CENTER);
        linha.add(controle, BorderLayout.EAST);

        return linha;
    }

    private JComponent linhaDeOpcao(CaixaSelecao caixa, String descricao) {
        JLabel dica = Estilo.suave(descricao);
        dica.setBorder(BorderFactory.createEmptyBorder(6, 26, 0, 0));

        Pilha textos = new Pilha();
        textos.adicionar(caixa);
        textos.adicionar(dica);

        JPanel linha = Estilo.painel();
        linha.setLayout(new BorderLayout());
        linha.setBorder(linhaComDivisor());
        linha.add(textos, BorderLayout.NORTH);

        return linha;
    }

    private JComponent linhaInformativa(String titulo, String valor) {
        JLabel rotulo = Estilo.dado(titulo);

        JLabel descricao = new JLabel(valor);
        descricao.setFont(Estilo.fonteRegular(13f));
        descricao.setForeground(Estilo.paleta().textoSecundario);

        JPanel linha = Estilo.painel();
        linha.setLayout(new BorderLayout(24, 0));
        linha.setBorder(linhaComDivisor());
        linha.add(rotulo, BorderLayout.WEST);
        linha.add(descricao, BorderLayout.EAST);

        return linha;
    }

    private Border linhaComDivisor() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.paleta().bordaSuave),
                BorderFactory.createEmptyBorder(14, 0, 14, 0));
    }

    private JScrollPane rolagem(JComponent conteudo) {
        JScrollPane rolagem = new JScrollPane(conteudo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.setViewportBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return rolagem;
    }
}
