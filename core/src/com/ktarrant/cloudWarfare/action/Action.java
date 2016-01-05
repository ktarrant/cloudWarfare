package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.Vector2;
import com.ktarrant.cloudWarfare.player.Player;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class Action {
    public final Player player;
    public final ActionModifier modifier;
    public final ActionDef actionDef;
    public final Vector2 direction;

    public Action(Player player,
                  ActionModifier modifier,
                  ActionDef actionDef,
                  Vector2 direction) {
        this.player = player;
        this.modifier = modifier;
        this.actionDef = actionDef;
        this.direction = direction;
    }

    public void execute() {
        float staminaAfter =
                player.stamina * this.actionDef.depleteMultiplier - this.actionDef.depleteConstant;
        if (staminaAfter < 0.0f) {
            // Not enough stamina
            return;
        }

        direction.scl(actionDef.linearImpulseMultiplier);
        direction.add(actionDef.linearImpulseConstant);
        direction.scl(player.stamina);
        player.body.applyLinearImpulse(direction, Vector2.Zero, true);

        player.stamina = staminaAfter;
    }
}
