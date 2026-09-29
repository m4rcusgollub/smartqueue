package ui;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import model.Prioridade;

/**
 * Paleta moderna de alta fidelidade: estilo Linear / Notion / Fintech.
 * Centraliza tokens de cor para temas Claro e Escuro, com contraste WCAG AA/AAA.
 */
public final class Paleta {

    public static final Paleta CLARO = new Construtor()
            .fundo(0xF8FAFC)
            .superficie(0xFFFFFF)
            .superficieAlt(0xF1F5F9)
            .lateral(0xFFFFFF)
            .borda(0xE2E8F0)
            .bordaSuave(0xEDF2F7)
            .selecao(0xEFF6FF)
            .texto(0x0F172A)
            .textoSecundario(0x475569)
            .textoSuave(0x94A3B8)
            .sobrePrimaria(0xFFFFFF)
            .primaria(0x2563EB)
            .primariaHover(0x1D4ED8)
            .primariaAtiva(0x1E40AF)
            .primariaSuave(0xEFF6FF)
            .primariaTexto(0x1D4ED8)
            .sucesso(0x10B981)
            .sucessoSuave(0xECFDF5)
            .alerta(0xD97706)
            .alertaSuave(0xFFFBEB)
            .perigo(0xEF4444)
            .perigoSuave(0xFEF2F2)
            .baixa(0x64748B, 0xF1F5F9)
            .normal(0x2563EB, 0xEFF6FF)
            .prioridade(0xD97706, 0xFFFBEB)
            .urgente(0xDC2626, 0xFEF2F2)
            .sombra(0x0F172A)
            .construir();

    public static final Paleta ESCURO = new Construtor()
            .fundo(0x0B0F17)
            .superficie(0x131924)
            .superficieAlt(0x1A2232)
            .lateral(0x0F141F)
            .borda(0x222C3D)
            .bordaSuave(0x1B2433)
            .selecao(0x1A2642)
            .texto(0xF8FAFC)
            .textoSecundario(0x94A3B8)
            .textoSuave(0x64748B)
            .sobrePrimaria(0xFFFFFF)
            .primaria(0x3B82F6)
            .primariaHover(0x60A5FA)
            .primariaAtiva(0x2563EB)
            .primariaSuave(0x172544)
            .primariaTexto(0x93C5FD)
            .sucesso(0x34D399)
            .sucessoSuave(0x0F291E)
            .alerta(0xFBBF24)
            .alertaSuave(0x2E230E)
            .perigo(0xF87171)
            .perigoSuave(0x301717)
            .baixa(0x94A3B8, 0x1E293B)
            .normal(0x93C5FD, 0x172544)
            .prioridade(0xFBBF24, 0x2E230E)
            .urgente(0xF87171, 0x301717)
            .sombra(0x000000)
            .construir();

    public final Color fundo;
    public final Color superficie;
    public final Color superficieAlt;
    public final Color lateral;
    public final Color borda;
    public final Color bordaSuave;
    public final Color selecao;
    public final Color texto;
    public final Color textoSecundario;
    public final Color textoSuave;
    public final Color sobrePrimaria;
    public final Color primaria;
    public final Color primariaHover;
    public final Color primariaAtiva;
    public final Color primariaSuave;
    public final Color primariaTexto;
    public final Color sucesso;
    public final Color sucessoSuave;
    public final Color alerta;
    public final Color alertaSuave;
    public final Color perigo;
    public final Color perigoSuave;
    public final Color sombra;

    private final Map<Prioridade, CoresPrioridade> prioridades =
            new EnumMap<>(Prioridade.class);

    private Paleta(Construtor origem) {
        fundo = Cores.cinza(origem.fundo);
        superficie = Cores.cinza(origem.superficie);
        superficieAlt = Cores.cinza(origem.superficieAlt);
        lateral = Cores.cinza(origem.lateral);
        borda = Cores.cinza(origem.borda);
        bordaSuave = Cores.cinza(origem.bordaSuave);
        selecao = Cores.cinza(origem.selecao);
        texto = Cores.cinza(origem.texto);
        textoSecundario = Cores.cinza(origem.textoSecundario);
        textoSuave = Cores.cinza(origem.textoSuave);
        sobrePrimaria = Cores.cinza(origem.sobrePrimaria);
        primaria = Cores.cinza(origem.primaria);
        primariaHover = Cores.cinza(origem.primariaHover);
        primariaAtiva = Cores.cinza(origem.primariaAtiva);
        primariaSuave = Cores.cinza(origem.primariaSuave);
        primariaTexto = Cores.cinza(origem.primariaTexto);
        sucesso = Cores.cinza(origem.sucesso);
        sucessoSuave = Cores.cinza(origem.sucessoSuave);
        alerta = Cores.cinza(origem.alerta);
        alertaSuave = Cores.cinza(origem.alertaSuave);
        perigo = Cores.cinza(origem.perigo);
        perigoSuave = Cores.cinza(origem.perigoSuave);
        sombra = Cores.cinza(origem.sombra);

        prioridades.put(Prioridade.BAIXA, origem.baixa);
        prioridades.put(Prioridade.NORMAL, origem.normal);
        prioridades.put(Prioridade.PRIORIDADE, origem.prioridade);
        prioridades.put(Prioridade.URGENTE, origem.urgente);
    }

    public CoresPrioridade prioridade(Prioridade prioridade) {
        return prioridades.get(prioridade);
    }

    private static final class Construtor {

        private int fundo;
        private int superficie;
        private int superficieAlt;
        private int lateral;
        private int borda;
        private int bordaSuave;
        private int selecao;
        private int texto;
        private int textoSecundario;
        private int textoSuave;
        private int sobrePrimaria;
        private int primaria;
        private int primariaHover;
        private int primariaAtiva;
        private int primariaSuave;
        private int primariaTexto;
        private int sucesso;
        private int sucessoSuave;
        private int alerta;
        private int alertaSuave;
        private int perigo;
        private int perigoSuave;
        private int sombra;

        private CoresPrioridade baixa;
        private CoresPrioridade normal;
        private CoresPrioridade prioridade;
        private CoresPrioridade urgente;

        private Construtor fundo(int valor) {
            fundo = valor;
            return this;
        }

        private Construtor superficie(int valor) {
            superficie = valor;
            return this;
        }

        private Construtor superficieAlt(int valor) {
            superficieAlt = valor;
            return this;
        }

        private Construtor lateral(int valor) {
            lateral = valor;
            return this;
        }

        private Construtor borda(int valor) {
            borda = valor;
            return this;
        }

        private Construtor bordaSuave(int valor) {
            bordaSuave = valor;
            return this;
        }

        private Construtor selecao(int valor) {
            selecao = valor;
            return this;
        }

        private Construtor texto(int valor) {
            texto = valor;
            return this;
        }

        private Construtor textoSecundario(int valor) {
            textoSecundario = valor;
            return this;
        }

        private Construtor textoSuave(int valor) {
            textoSuave = valor;
            return this;
        }

        private Construtor sobrePrimaria(int valor) {
            sobrePrimaria = valor;
            return this;
        }

        private Construtor primaria(int valor) {
            primaria = valor;
            return this;
        }

        private Construtor primariaHover(int valor) {
            primariaHover = valor;
            return this;
        }

        private Construtor primariaAtiva(int valor) {
            primariaAtiva = valor;
            return this;
        }

        private Construtor primariaSuave(int valor) {
            primariaSuave = valor;
            return this;
        }

        private Construtor primariaTexto(int valor) {
            primariaTexto = valor;
            return this;
        }

        private Construtor sucesso(int valor) {
            sucesso = valor;
            return this;
        }

        private Construtor sucessoSuave(int valor) {
            sucessoSuave = valor;
            return this;
        }

        private Construtor alerta(int valor) {
            alerta = valor;
            return this;
        }

        private Construtor alertaSuave(int valor) {
            alertaSuave = valor;
            return this;
        }

        private Construtor perigo(int valor) {
            perigo = valor;
            return this;
        }

        private Construtor perigoSuave(int valor) {
            perigoSuave = valor;
            return this;
        }

        private Construtor sombra(int valor) {
            sombra = valor;
            return this;
        }

        private Construtor baixa(int texto, int fundo) {
            baixa = new CoresPrioridade(Cores.cinza(texto), Cores.cinza(fundo));
            return this;
        }

        private Construtor normal(int texto, int fundo) {
            normal = new CoresPrioridade(Cores.cinza(texto), Cores.cinza(fundo));
            return this;
        }

        private Construtor prioridade(int texto, int fundo) {
            prioridade = new CoresPrioridade(Cores.cinza(texto), Cores.cinza(fundo));
            return this;
        }

        private Construtor urgente(int texto, int fundo) {
            urgente = new CoresPrioridade(Cores.cinza(texto), Cores.cinza(fundo));
            return this;
        }

        private Paleta construir() {
            return new Paleta(this);
        }
    }
}
