package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.player.PlayerState;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionDefLoader {
    public static final float DEFAULT_CAPTURE_ANGLE = MathUtils.PI / 6.0f;
    public static final Vector2 DEFAULT_PUFF_POWER = new Vector2(0.7f, 2.0f);
    public static final Vector2 DEFAULT_JUMP_POWER = new Vector2(1.2f, 2.5f);
    public static final Vector2 DEFAULT_RUN_RIGHT_POWER = new Vector2(1.3f, 0.0f);
    public static final Vector2 DEFAULT_RUN_LEFT_POWER = new Vector2(-1.3f, 0.0f);
    public static final float DEFAULT_DEPLETE_CONSTANT = 0.1f;
    public static final float DEFAULT_DEPLETE_MULTIPLIER = 0.85f;
    public static final float DEFAULT_REPEAT_INTERVAL = 0.25f;

    ActionDef puffDef;
    ActionDef jumpDef;
    ActionDef runLeftDef;
    ActionDef runRightDef;

    Array<ActionDef> nullList;
    Array<ActionDef> airList;
    Array<ActionDef> footList;

    public void load() {
        puffDef = new ActionDef(
                -MathUtils.PI,
                MathUtils.PI2,
                DEFAULT_PUFF_POWER,
                Vector2.Zero,
                DEFAULT_DEPLETE_CONSTANT,
                DEFAULT_DEPLETE_MULTIPLIER,
                DEFAULT_REPEAT_INTERVAL
        );

        jumpDef = new ActionDef(
                DEFAULT_CAPTURE_ANGLE / 2.0f,
                MathUtils.PI - DEFAULT_CAPTURE_ANGLE,
                DEFAULT_JUMP_POWER,
                Vector2.Zero,
                DEFAULT_DEPLETE_CONSTANT,
                DEFAULT_DEPLETE_MULTIPLIER,
                DEFAULT_REPEAT_INTERVAL
        );

        runRightDef = new ActionDef(
                -DEFAULT_CAPTURE_ANGLE / 2.0f,
                DEFAULT_CAPTURE_ANGLE,
                Vector2.Zero,
                DEFAULT_RUN_RIGHT_POWER,
                DEFAULT_DEPLETE_CONSTANT,
                DEFAULT_DEPLETE_MULTIPLIER,
                DEFAULT_REPEAT_INTERVAL
        );

        runLeftDef = new ActionDef(
                (MathUtils.PI2 - DEFAULT_CAPTURE_ANGLE) / 2.0f,
                DEFAULT_CAPTURE_ANGLE,
                Vector2.Zero,
                DEFAULT_RUN_LEFT_POWER,
                DEFAULT_DEPLETE_CONSTANT,
                DEFAULT_DEPLETE_MULTIPLIER,
                DEFAULT_REPEAT_INTERVAL
        );

        nullList = new Array<ActionDef>();

        airList = new Array<ActionDef>();
        airList.add(puffDef);

        footList = new Array<ActionDef>();
        footList.add(jumpDef);
        footList.add(runLeftDef);
        footList.add(runRightDef);
    }

    public Array<ActionDef> getActionDefList(ActionModifier modifier, PlayerState state) {
        switch (modifier) {
            case ATTACK:
                return nullList;

            case NORMAL:
            default:
                // Treat all other modifiers as NORMAL
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
