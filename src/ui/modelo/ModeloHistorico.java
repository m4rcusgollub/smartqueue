package ui.modelo;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Prioridade;
import service.Atendimento;
import service.FilaService;
import ui.Apresentacao;

/**
 * Atendimentos registrados, do mais recente para o mais antigo, com guichê e status.
 */
public class ModeloHistorico extends AbstractTableModel {

    private static final String[] COLUNAS = {"ID", "Nome", "Prioridade", "Guichê", "Data", "Horário", "Situação"};

    private final FilaService servico;
    private List<Atendimento> atendimentos = List.of();

    public ModeloHistorico(FilaService servico) {
        this.servico = servico;
        atualizar();
    }

    public void atualizar() {
        atendimentos = servico.historico();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return atendimentos.size();
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
        return coluna == 2 ? Prioridade.class : String.class;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        if (linha >= atendimentos.size()) {
            return "";
        }
        Atendimento atendimento = atendimentos.get(linha);

        return switch (coluna) {
            case 0 -> Apresentacao.id(atendimento.getConsumidor().getId());
            case 1 -> atendimento.getConsumidor().getNome();
            case 2 -> atendimento.getConsumidor().getPrioridade();
            case 3 -> atendimento.getGuiche();
            case 4 -> Apresentacao.data(atendimento.getHorario());
            case 5 -> Apresentacao.hora(atendimento.getHorario());
            default -> atendimento.getStatus();
        };
    }
}
