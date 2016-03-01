package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Component;

/**
 * Created by ktarrant1 on 1/4/16.
 */
public enum PlayerStateComponent implements Component {
    AIR_ACTIVE      ('A', false, 0.15f, 0.0f, true,  1.0f),
    AIR_INACTIVE    ('I', false, 0.15f, 0.0f, false, 1.0f),
    FOOT_ACTIVE     ('F', true,  0.65f, 1.0f, true,  10.0f),
    FOOT_INACTIVE   ('L', true,  0.65f, 1.0f, false, 5.0f);

    public final char stateIcon;
    public final boolean isOnFoot;
    public final float staminaRegenRate;
    public final float linearDamping;
    public final boolean isFixedRotation;
    public final float angularDamping;

    private PlayerStateComponent(char stateIcon, boolean isOnFoot, float staminaRegenRate,
                                 float linearDamping,
                                 boolean isFixedRotation, float angularDamping) {
        this.stateIcon = stateIcon;
        this.isOnFoot = isOnFoot;
        this.staminaRegenRate = staminaRegenRate;
        this.linearDamping = linearDamping;
        this.isFixedRotation = isFixedRotation;
        this.angularDamping = angularDamping;
    }
}
