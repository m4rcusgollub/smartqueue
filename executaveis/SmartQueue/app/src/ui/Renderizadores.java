package ui;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import model.Prioridade;

/**
 * Renderizadores das tabelas: fundo por linha, divisor inferior discreto e
 * células específicas para prioridade e situação.
 */
public final class Renderizadores {

    public enum Tom {
        NORMAL,
        SECUNDARIO,
        SUAVE
    }

    private Renderizadores() {
    }

    abstract static class Base extends DefaultTableCellRenderer {

        private final int paddingEsquerda;

        Base(int paddingEsquerda) {
            this.paddingEsquerda = paddingEsquerda;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, comFoco, linha, coluna);

            Paleta paleta = Estilo.paleta();
            setBackground(selecionada ? paleta.selecao : paleta.superficie);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, paleta.bordaSuave),
                    BorderFactory.createEmptyBorder(0, paddingEsquerda, 0, 10)));

            return this;
        }
    }

    /** Texto comum: nome, horário, tempo de espera e posição. */
    public static final class Texto extends Base {

        private final boolean forte;
        private final Tom tom;
        private final int alinhamento;

        public Texto(int paddingEsquerda, boolean forte, Tom tom, int alinhamento) {
            super(paddingEsquerda);
            this.forte = forte;
            this.tom = tom;
            this.alinhamento = alinhamento;
        }

        public Texto(int paddingEsquerda) {
            this(paddingEsquerda, false, Tom.NORMAL, SwingConstants.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, comFoco, linha, coluna);

            Paleta paleta = Estilo.paleta();
            setFont(forte ? Estilo.fonteForte(13f) : Estilo.fonteRegular(13f));
            setForeground(switch (tom) {
                case NORMAL -> paleta.texto;
                case SECUNDARIO -> paleta.textoSecundario;
                case SUAVE -> paleta.textoSuave;
            });
            setHorizontalAlignment(alinhamento);

            return this;
        }
    }

    /** Código do registro (ID), em fonte monoespaçada para leitura rápida. */
    public static final class Codigo extends Base {

        private final int alinhamento;

        public Codigo(int paddingEsquerda, int alinhamento) {
            super(paddingEsquerda);
            this.alinhamento = alinhamento;
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, comFoco, linha, coluna);

            setFont(Estilo.fonteMono(12.5f));
            setForeground(Estilo.paleta().textoSecundario);
            setHorizontalAlignment(alinhamento);

            return this;
        }
    }

    /** Prioridade: reutiliza a mesma etiqueta usada nos painéis. */
    public static final class PrioridadeChip implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            Paleta paleta = Estilo.paleta();
            Prioridade prioridade = valor instanceof Prioridade escolhida
                    ? escolhida
                    : Prioridade.NORMAL;

            ChipPrioridade celula = new ChipPrioridade(prioridade);
            celula.definirFundoLinha(selecionada ? paleta.selecao : paleta.superficie);
            celula.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, paleta.bordaSuave));

            return celula;
        }
    }

    /** Situação na fila ou histórico: ponto colorido semântico + texto. */
    public static final class Situacao extends Base {

        public Situacao() {
            super(14);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, comFoco, linha, coluna);

            Paleta paleta = Estilo.paleta();
            String texto = (valor != null) ? valor.toString() : "";

            Color corPonto;
            Color corTexto;
            boolean destaque = false;

            if ("Concluído".equalsIgnoreCase(texto)) {
                corPonto = paleta.sucesso;
                corTexto = paleta.sucesso;
                destaque = true;
            } else if ("Em Atendimento".equalsIgnoreCase(texto) || "Próximo".equalsIgnoreCase(texto)) {
                corPonto = paleta.primaria;
                corTexto = paleta.primariaTexto;
                destaque = true;
            } else if ("Não Compareceu".equalsIgnoreCase(texto)) {
                corPonto = paleta.perigo;
                corTexto = paleta.perigo;
            } else {
                corPonto = paleta.textoSuave;
                corTexto = paleta.textoSecundario;
            }

            setIcon(new PontoColorido(corPonto, 7));
            setIconTextGap(8);
            setFont(destaque ? Estilo.fonteForte(13f) : Estilo.fonteRegular(13f));
            setForeground(corTexto);

            return this;
        }
    }

    /** Cabeçalho: microtexto em caixa alta, fundo neutro e divisor inferior. */
    public static final class Cabecalho extends DefaultTableCellRenderer {

        private final int paddingEsquerda;

        public Cabecalho() {
            this(14);
        }

        public Cabecalho(int paddingEsquerda) {
            this.paddingEsquerda = paddingEsquerda;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                                                       boolean selecionada, boolean comFoco,
                                                       int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, false, false, linha, coluna);

            Paleta paleta = Estilo.paleta();
            setText(String.valueOf(tabela.getColumnName(coluna)).toUpperCase(Locale.ROOT));
            setFont(Estilo.fonteCabecalhoTabela());
            setForeground(paleta.textoSecundario);
            setBackground(paleta.superficieAlt);
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, paleta.borda),
                    BorderFactory.createEmptyBorder(0, paddingEsquerda, 0, 10)));

            return this;
        }
    }
}
