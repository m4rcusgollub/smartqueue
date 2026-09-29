package ui;

/**
 * Operações da janela que as telas podem solicitar: navegação, troca de tema,
 * registro da última ação e atualização geral da interface.
 */
public interface AcoesJanela {

    void irPara(Pagina pagina);

    void definirTema(Tema tema);

    void registrarMensagem(String texto);

    boolean confirmar(String mensagem);

    void atualizarInterface();
}
