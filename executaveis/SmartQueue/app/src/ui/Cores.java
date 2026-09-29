package ui;

import java.awt.Color;

public final class Cores {

    private Cores() {
    }

    public static Color cinza(int tom) {
        return new Color(tom);
    }

    public static Color comAlfa(Color cor, int alfa) {
        return new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), alfa);
    }

    public static Color misturar(Color base, Color outra, float proporcao) {
        float inverso = 1f - proporcao;

        return new Color(
                Math.round(base.getRed() * inverso + outra.getRed() * proporcao),
                Math.round(base.getGreen() * inverso + outra.getGreen() * proporcao),
                Math.round(base.getBlue() * inverso + outra.getBlue() * proporcao)
        );
    }
}
