package ui;

/**
 * Páginas da área principal. Cada uma declara o próprio título, a descrição
 * exibida no cabeçalho e o ícone usado na navegação.
 */
public enum Pagina {

    DASHBOARD("Dashboard", "Visão geral, tempo estimado de espera e métricas da fila",
            Icone.PAINEL),

    TOTEM("Totem de Senhas", "Autoatendimento intuitivo para emissão rápida de senhas",
            Icone.TOTEM),

    ATENDIMENTO("Painel do Atendente", "Guichê de atendimento: chamar, rechamar, finalizar e pular",
            Icone.ATENDIMENTO),

    PAINEL_TV("Painel de TV", "Display de chamada para sala de espera com relógio e som",
            Icone.TV),

    FILA("Fila de Espera", "Cadastro de consumidores e acompanhamento da ordem da fila",
            Icone.FILA),

    HISTORICO("Histórico", "Atendimentos registrados e tempo de resposta da sessão",
            Icone.HISTORICO),

    CONFIGURACOES("Configurações", "Preferências de tema, alertas e diagnóstico do sistema",
            Icone.CONFIGURACOES);

    private final String titulo;
    private final String descricao;
    private final Icone icone;

    Pagina(String titulo, String descricao, Icone icone) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.icone = icone;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Icone getIcone() {
        return icone;
    }
}
