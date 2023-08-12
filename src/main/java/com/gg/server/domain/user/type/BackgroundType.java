package com.gg.server.domain.user.type;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public enum BackgroundType {
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

    public static BackgroundType getRandomBackgroundType() {
        Random random = new Random();
        int tierInt = random.nextInt(100);
        int colorInt;

        if (tierInt <= 50)
            colorInt = random.nextInt(100) % 4;
        else if (tierInt <= 70)
            colorInt = (random.nextInt(99) % 3) + 4;
        else if (tierInt <= 85)
            colorInt = (random.nextInt(99) % 3) + 7;
        else if (tierInt <= 95)
            colorInt = (random.nextInt(99) % 3) + 10;
        else
            colorInt = (random.nextInt(99) % 3) + 13;

        return BackgroundType.values()[colorInt];
    }

    public static BackgroundType of(String code) {
        return BackgroundType.valueOf(code);
    }
}