package com.ktarrant.cloudWarfare.world;

import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Created by ktarrant1 on 3/19/16.
 */
public enum FilterCategory {
    PLATFORM    (1 << 1),
    NEUTRAL     (1 << 2),
    PLAYER_1    (1 << 3),
    PLAYER_2    (1 << 4),
    PLAYER_3    (1 << 5),
    PLAYER_4    (1 << 6);

    public final short categoryBits;
    public final short maskBits;

    private FilterCategory(int categoryBits) {
        this.categoryBits = (short) categoryBits;
        this.maskBits = (short) ~categoryBits;
    }
}
