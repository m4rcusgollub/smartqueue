package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import model.Prioridade;

/**
 * Sistema visual da aplicação: tema ativo, tipografia, medidas, cores de
 * prioridade, efeitos de feedback e fábricas de componentes.
 */
public final class Estilo {

    public static final int RAIO = 8;
    public static final int RAIO_PAINEL = 12;
    public static final int RAIO_CHIP = 6;
    public static final int RAIO_CARD_TOTEM = 16;
    public static final int ALTURA_CAMPO = 38;
    public static final int ALTURA_BOTAO = 38;
    public static final int ALTURA_BOTAO_PRINCIPAL = 42;
    public static final int ALTURA_LINHA = 48;
    public static final int ALTURA_ITEM_NAVEGACAO = 40;
    public static final int PADDING_CONTEUDO = 24;
    public static final int ESPACO = 20;
    public static final int LARGURA_LATERAL = 240;
    public static final int LARGURA_LATERAL_COMPACTA = 64;
    public static final int LARGURA_COLUNA_LATERAL = 360;
    public static final int LARGURA_LEITURA = 760;

    private static final String FAMILIA = primeiraDisponivel(
            "Inter", "Geist", "Segoe UI Variable Text", "Segoe UI", "SF Pro Text",
            "Helvetica Neue", "Roboto", "Noto Sans", "Arial", Font.SANS_SERIF);

    private static final String FAMILIA_FORTE = primeiraDisponivel(
            "Inter SemiBold", "Segoe UI Semibold", "SF Pro Text Semibold", FAMILIA);

    private static final String FAMILIA_DISPLAY = primeiraDisponivel(
            "Inter Black", "Segoe UI Bold", "SF Pro Display", FAMILIA_FORTE);

    private static final String FAMILIA_MONO = primeiraDisponivel(
            "Cascadia Mono", "Consolas", "JetBrains Mono", "DejaVu Sans Mono", Font.MONOSPACED);

    private static final int ESTILO_FORTE =
            FAMILIA_FORTE.equals(FAMILIA) ? Font.BOLD : Font.PLAIN;

    private static final Map<String, Font> FONTES = new HashMap<>();

    private static Tema tema = Tema.CLARO;

    private Estilo() {
    }

    public static void definirTema(Tema novoTema) {
        tema = novoTema;
    }

    public static Tema tema() {
        return tema;
    }

    public static Paleta paleta() {
        return tema == Tema.ESCURO ? Paleta.ESCURO : Paleta.CLARO;
    }

    public static CoresPrioridade prioridade(Prioridade prioridade) {
        return paleta().prioridade(prioridade);
    }

    public static Font fonteRegular(float tamanho) {
        return fonte(FAMILIA, Font.PLAIN, tamanho);
    }

    public static Font fonteForte(float tamanho) {
        return fonte(FAMILIA_FORTE, ESTILO_FORTE, tamanho);
    }

    public static Font fonteDisplay(float tamanho) {
        return fonte(FAMILIA_DISPLAY, Font.BOLD, tamanho);
    }

    public static Font fonteMono(float tamanho) {
        return fonte(FAMILIA_MONO, Font.PLAIN, tamanho);
    }

    /** Fonte de microtexto com espaçamento entre letras (etiquetas e badges). */
    public static Font fonteChip(float tamanho) {
        return fonteForte(tamanho).deriveFont(Map.of(TextAttribute.TRACKING, 0.04f));
    }

    public static Font fonteCabecalhoTabela() {
        return fonteForte(10.5f).deriveFont(Map.of(TextAttribute.TRACKING, 0.09f));
    }

    private static Font fonte(String familia, int estilo, float tamanho) {
        String chave = familia + '|' + estilo + '|' + tamanho;

        return FONTES.computeIfAbsent(chave,
                ignorado -> new Font(familia, estilo, 12).deriveFont(tamanho));
    }

    private static String primeiraDisponivel(String... candidatas) {
        try {
            List<String> instaladas = List.of(
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames(Locale.ROOT));

            for (String candidata : candidatas) {
                if (instaladas.contains(candidata)) {
                    return candidata;
                }
            }
        } catch (RuntimeException | LinkageError ignorado) {
            // ambiente sem fontes gráficas: segue com a família lógica padrão
        }

        return Font.SANS_SERIF;
    }

    public static JLabel titulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteForte(20f));
        rotulo.setForeground(paleta().texto);
        return rotulo;
    }

    public static JLabel subtitulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteRegular(12.5f));
        rotulo.setForeground(paleta().textoSecundario);
        return rotulo;
    }

    public static JLabel secao(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteForte(14f));
        rotulo.setForeground(paleta().texto);
        return rotulo;
    }

    /** Título de grupo de configurações, em microtexto de caixa alta. */
    public static JLabel grupo(String texto) {
        JLabel rotulo = new JLabel(texto.toUpperCase(Locale.ROOT));
        rotulo.setFont(fonteCabecalhoTabela());
        rotulo.setForeground(paleta().textoSecundario);
        return rotulo;
    }

    public static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteForte(12f));
        rotulo.setForeground(paleta().textoSecundario);
        return rotulo;
    }

    public static JLabel dado(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteRegular(13f));
        rotulo.setForeground(paleta().texto);
        return rotulo;
    }

    public static JLabel suave(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteRegular(12f));
        rotulo.setForeground(paleta().textoSuave);
        return rotulo;
    }

    public static JLabel valor(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonteForte(28f));
        rotulo.setForeground(paleta().texto);
        return rotulo;
    }

    public static JPanel painel() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        return painel;
    }

    /** Contêiner com a margem interna padrão do conteúdo de um painel/superfície. */
    public static JPanel interior() {
        JPanel interior = painel();
        interior.setLayout(new BorderLayout());
        interior.setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
        return interior;
    }

    public static Component espaco(int altura) {
        return Box.createRigidArea(new Dimension(0, altura));
    }

    public static Component espacoHorizontal(int largura) {
        return Box.createRigidArea(new Dimension(largura, 0));
    }

    public static Border bordaInferior() {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, paleta().borda);
    }

    public static Border bordaInferiorSuave() {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, paleta().bordaSuave);
    }

    /**
     * Toca um sinal sonoro agradável e elegante de chamada (dual chime sintético:
     * D5 -> A5, padrão internacional de aeroportos/hospitais), sem depender de arquivos externos.
     */
    public static void tocarAlertaChamada() {
        new Thread(() -> {
            try {
                byte[] audio = new byte[7200];
                for (int i = 0; i < audio.length; i++) {
                    double freq = i < 3600 ? 587.33 : 880.0;
                    double amp = Math.sin(Math.PI * (i % 3600) / 3600.0);
                    double sample = Math.sin(2.0 * Math.PI * i * freq / 8000.0) * amp;
                    audio[i] = (byte) (sample * 65.0);
                }
                AudioFormat formato = new AudioFormat(8000f, 8, 1, true, false);
                SourceDataLine linha = AudioSystem.getSourceDataLine(formato);
                linha.open(formato);
                linha.start();
                linha.write(audio, 0, audio.length);
                linha.drain();
                linha.close();
            } catch (Exception e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }
}
