package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.Component;

/**
 * Created by ktarrant1 on 1/4/16.
 */
public class PlayerStateComponent implements Component {
    public char stateIcon = 'x';
    public boolean isOnFoot = false;
    public float staminaRegenRate = 0.0f;
    public float linearDamping = 0.0f;
    public boolean isFixedRotation = false;
    public float angularDamping = 0.0f;
}
