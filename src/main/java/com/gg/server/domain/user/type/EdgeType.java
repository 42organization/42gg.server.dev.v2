package com.gg.server.domain.user.type;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public enum EdgeType {
    BASIC(0),
    COLOR1(1),
    COLOR2(2),
    COLOR3(3),
    COLOR4(4),
    COLOR5(5),
    COLOR6(6),
    COLOR7(7),
    COLOR8(8),
    COLOR9(9),
    COLOR10(10),
    COLOR11(11),
    COLOR12(12),
    COLOR13(13),
    COLOR14(14),
    COLOR15(15);

    private final Integer code;

    public static EdgeType getRandomEdgeType() {
        Random random = new Random();
        int tierInt = random.nextInt(100);
        int colorInt;

        if (tierInt < 5)
            colorInt = random.nextInt(99) % 3;
        else if (tierInt < 15)
            colorInt = (random.nextInt(99) % 3) + 3;
        else if (tierInt < 30)
            colorInt = (random.nextInt(99) % 3) + 6;
        else if (tierInt < 50)
            colorInt = (random.nextInt(99) % 3) + 9;
        else
            colorInt = (random.nextInt(99) % 3) + 12;
        return EdgeType.values()[colorInt];
    }

    public static EdgeType of(String code) {
        return EdgeType.valueOf(code);
    }
}