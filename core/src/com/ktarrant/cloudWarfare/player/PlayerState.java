package com.ktarrant.cloudWarfare.player;

/**
 * Created by ktarrant1 on 1/4/16.
 */
public enum PlayerState {
    AIR_ACTIVE('A'),
    AIR_INACTIVE('I'),
    FOOT_ACTIVE('F'),
    FOOT_INACTIVE('L');

    public final char stateIcon;

    private PlayerState(char stateIcon) {
        this.stateIcon = stateIcon;
    }
}
