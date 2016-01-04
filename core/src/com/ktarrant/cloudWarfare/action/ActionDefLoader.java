package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.player.Player;
import com.ktarrant.cloudWarfare.player.PlayerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionDefLoader {
    public final static float DEFAULT_CAPTURE_ANGLE = MathUtils.PI / 6.0f;
    public static final float DEFAULT_JUMP_POWER = 0.75f;
    public static final float DEFAULT_RUN_POWER = 0.5f;

    ActionDef jumpDef;
    ActionDef runLeftDef;
    ActionDef runRightDef;

    Array<ActionDef> nullList;
    Array<ActionDef> airList;
    Array<ActionDef> footList;

    public void load() {
        jumpDef  = new ActionDef(
                -MathUtils.PI,
                MathUtils.PI2,
                new Vector2(DEFAULT_JUMP_POWER, DEFAULT_JUMP_POWER),
                Vector2.Zero
        );

        runRightDef = new ActionDef(
                -DEFAULT_CAPTURE_ANGLE / 2.0f,
                DEFAULT_CAPTURE_ANGLE,
                Vector2.Zero,
                new Vector2(DEFAULT_RUN_POWER, 0.0f)
        );

        runLeftDef = new ActionDef(
                (MathUtils.PI2 - DEFAULT_CAPTURE_ANGLE) / 2.0f,
                DEFAULT_CAPTURE_ANGLE,
                Vector2.Zero,
                new Vector2(-DEFAULT_RUN_POWER, 0.0f)
        );

        nullList = new Array<ActionDef>();

        airList = new Array<ActionDef>();
        airList.add(jumpDef);

        footList = new Array<ActionDef>();
        footList.add(runLeftDef);
        footList.add(runRightDef);
    }

    public Array<ActionDef> getActionDefList(ActionModifier modifier, Player.PlayerState state) {
        switch (modifier) {
            default:
                // For now, handle all modifiers the same
                switch (state) {
                    case AIR_ACTIVE:
                        return airList;

                    case FOOT_ACTIVE:
                        return footList;

                    default:
                        return nullList;
                }
        }
    }
}
