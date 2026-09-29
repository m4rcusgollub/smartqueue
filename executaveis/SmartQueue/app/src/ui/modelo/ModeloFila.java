package ui.modelo;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Consumidor;
import model.Prioridade;
import service.FilaService;
import ui.Apresentacao;

/**
 * Lista da fila na ordem em que os consumidores serão chamados, com a posição
 * calculada pelo próprio modelo de apresentação (a ordem vem do serviço).
 */
public class ModeloFila extends AbstractTableModel {

    private static final String[] COLUNAS =
            {"Pos.", "ID", "Nome", "Prioridade", "Espera", "Situação"};

    private final FilaService servico;
    private List<Consumidor> consumidores = List.of();

    public ModeloFila(FilaService servico) {
        this.servico = servico;
        atualizar();
    }

    public void atualizar() {
        consumidores = servico.aguardando();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return consumidores.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    @Override
    public Class<?> getColumnClass(int coluna) {
        return switch (coluna) {
            case 0 -> Integer.class;
            case 3 -> Prioridade.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Consumidor consumidor = consumidores.get(linha);

        return switch (coluna) {
            case 0 -> linha + 1;
            case 1 -> Apresentacao.id(consumidor.getId());
            case 2 -> consumidor.getNome();
            case 3 -> consumidor.getPrioridade();
            case 4 -> Apresentacao.esperaCurta(servico.minutosDeEspera(consumidor));
            default -> linha == 0 ? "Próximo" : "Aguardando";
        };
    }
}
