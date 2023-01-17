package app.battle;

import static app.enums.card_element.normal;
import static app.enums.card_element.water;
import static app.enums.card_element.fire;

public class EffectivenessTable { //table for calculating the effectiveness of attacks between two elements
    public static final float[][] effectiveness = new float[3][3];
    static {
        effectiveness[normal.ordinal()][normal.ordinal()] = 1f;
        effectiveness[normal.ordinal()][water.ordinal()] = 2f;
        effectiveness[normal.ordinal()][fire.ordinal()] = 0.5f;

        effectiveness[water.ordinal()][normal.ordinal()] = 0.5f;
        effectiveness[water.ordinal()][water.ordinal()] = 1f;
        effectiveness[water.ordinal()][fire.ordinal()] = 2f;

        effectiveness[fire.ordinal()][normal.ordinal()] = 2f;
        effectiveness[fire.ordinal()][water.ordinal()] = 0.5f;
        effectiveness[fire.ordinal()][fire.ordinal()] = 1;
    }
}