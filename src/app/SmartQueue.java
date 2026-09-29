package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import service.FilaService;
import ui.Estilo;
import ui.JanelaPrincipal;
import ui.Preferencias;

/**
 * Ponto de entrada da interface gráfica.
 *
 * A lógica do sistema continua em model/ (Consumidor, Prioridade,
 * FilaPrioridade); esta aplicação só apresenta os dados dessa lógica.
 */
public final class SmartQueue {

    private SmartQueue() {
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            instalarLafDoSistema();
            new JanelaPrincipal(new FilaService(), new Preferencias()).setVisible(true);
        });
    }

    private static void instalarLafDoSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception | LinkageError ignorado) {
            // sem LAF do sistema, segue o padrão do Swing
        }

        UIManager.put("OptionPane.messageFont", Estilo.fonteRegular(13f));
        UIManager.put("OptionPane.buttonFont", Estilo.fonteForte(13f));
    }
}
