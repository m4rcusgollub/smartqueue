package ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Padronização das tabelas: altura de linha, cabeçalho, divisores e leitura
 * dos valores. Nenhuma tabela da aplicação é estilizada fora daqui.
 */
public final class Tabela {

    public static final int ALTURA_CABECALHO = 34;

    private Tabela() {
    }

    public static JTable criar() {
        Paleta paleta = Estilo.paleta();

        JTable tabela = new JTable();
        tabela.setFont(Estilo.fonteRegular(13f));
        tabela.setForeground(paleta.texto);
        tabela.setBackground(paleta.superficie);
        tabela.setSelectionBackground(paleta.selecao);
        tabela.setSelectionForeground(paleta.texto);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(Estilo.ALTURA_LINHA);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setAutoCreateRowSorter(false);
        tabela.setRowSelectionAllowed(true);
        tabela.setFocusable(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(true);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, ALTURA_CABECALHO));
        tabela.getTableHeader().setBackground(paleta.superficieAlt);
        tabela.getTableHeader().setDefaultRenderer(new Renderizadores.Cabecalho());

        return tabela;
    }

    public static JScrollPane emRolagem(JTable tabela) {
        Paleta paleta = Estilo.paleta();

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.setViewportBorder(null);
        rolagem.setBackground(paleta.superficie);
        rolagem.getViewport().setBackground(paleta.superficie);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return rolagem;
    }

    public static void renderizador(JTable tabela, int coluna, TableCellRenderer renderizador) {
        tabela.getColumnModel().getColumn(coluna).setCellRenderer(renderizador);
    }

    public static void larguras(JTable tabela, int... larguras) {
        for (int indice = 0; indice < larguras.length; indice++) {
            TableColumn coluna = tabela.getColumnModel().getColumn(indice);
            coluna.setPreferredWidth(larguras[indice]);
            // Colunas estreitas (Pos., ID) não encolhem; as demais podem ceder
            // espaço para o nome quando a janela fica apertada.
            coluna.setMinWidth(Math.min(larguras[indice], 150));
        }
    }

    public static void ocultar(JTable tabela, int... colunas) {
        for (int indice : colunas) {
            TableColumn coluna = tabela.getColumnModel().getColumn(indice);
            coluna.setMinWidth(0);
            coluna.setMaxWidth(0);
            coluna.setPreferredWidth(0);
        }
    }
}
