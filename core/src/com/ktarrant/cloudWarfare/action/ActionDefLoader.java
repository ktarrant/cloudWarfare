package com.ktarrant.cloudWarfare.action;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.player.PlayerStateComponent;

import java.util.HashMap;

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
    private static final ActionDef ACTION_DEF_PUFF;
    static {
        ACTION_DEF_PUFF = new ActionDef();
        ACTION_DEF_PUFF.actionStartAngle = MathUtils.PI;
        ACTION_DEF_PUFF.actionLengthAngle = MathUtils.PI2;
        ACTION_DEF_PUFF.linearImpulseMultiplier.set(DEFAULT_PUFF_POWER);
        ACTION_DEF_PUFF.linearImpulseConstant.set(Vector2.Zero);
        ACTION_DEF_PUFF.depleteConstant = DEFAULT_DEPLETE_CONSTANT;
        ACTION_DEF_PUFF.depleteMultiplier = DEFAULT_DEPLETE_MULTIPLIER;
        ACTION_DEF_PUFF.repeatInterval = DEFAULT_REPEAT_INTERVAL;
    }
    private static final ActionDef ACTION_DEF_JUMP;
    static {
        ACTION_DEF_JUMP = new ActionDef();
        ACTION_DEF_JUMP.actionStartAngle = DEFAULT_CAPTURE_ANGLE / 2.0f;
        ACTION_DEF_JUMP.actionLengthAngle = MathUtils.PI - DEFAULT_CAPTURE_ANGLE;
        ACTION_DEF_JUMP.linearImpulseMultiplier.set(DEFAULT_JUMP_POWER);
        ACTION_DEF_JUMP.linearImpulseConstant.set(Vector2.Zero);
        ACTION_DEF_JUMP.depleteConstant = DEFAULT_DEPLETE_CONSTANT;
        ACTION_DEF_JUMP.depleteMultiplier = DEFAULT_DEPLETE_MULTIPLIER;
        ACTION_DEF_JUMP.repeatInterval = DEFAULT_REPEAT_INTERVAL;
    }
    private static final ActionDef ACTION_DEF_RUN_RIGHT;
    static {
        ACTION_DEF_RUN_RIGHT = new ActionDef();
        ACTION_DEF_RUN_RIGHT.actionStartAngle = -DEFAULT_CAPTURE_ANGLE / 2.0f;
        ACTION_DEF_RUN_RIGHT.actionLengthAngle = DEFAULT_CAPTURE_ANGLE;
        ACTION_DEF_RUN_RIGHT.linearImpulseMultiplier.set(Vector2.Zero);
        ACTION_DEF_RUN_RIGHT.linearImpulseConstant.set(DEFAULT_RUN_RIGHT_POWER);
        ACTION_DEF_RUN_RIGHT.depleteConstant = DEFAULT_DEPLETE_CONSTANT;
        ACTION_DEF_RUN_RIGHT.depleteMultiplier = DEFAULT_DEPLETE_MULTIPLIER;
        ACTION_DEF_RUN_RIGHT.repeatInterval = DEFAULT_REPEAT_INTERVAL;
    }
    private static final ActionDef ACTION_DEF_RUN_LEFT;
    static {
        ACTION_DEF_RUN_LEFT = new ActionDef();
        ACTION_DEF_RUN_LEFT.actionStartAngle = (MathUtils.PI2 - DEFAULT_CAPTURE_ANGLE) / 2.0f;
        ACTION_DEF_RUN_LEFT.actionLengthAngle = DEFAULT_CAPTURE_ANGLE;
        ACTION_DEF_RUN_LEFT.linearImpulseMultiplier.set(Vector2.Zero);
        ACTION_DEF_RUN_LEFT.linearImpulseConstant.set(DEFAULT_RUN_LEFT_POWER);
        ACTION_DEF_RUN_LEFT.depleteConstant = DEFAULT_DEPLETE_CONSTANT;
        ACTION_DEF_RUN_LEFT.depleteMultiplier = DEFAULT_DEPLETE_MULTIPLIER;
        ACTION_DEF_RUN_LEFT.repeatInterval = DEFAULT_REPEAT_INTERVAL;
    }
    private static final Array<ActionDef> ACTION_DEF_LIST_NULL;
    static {
        ACTION_DEF_LIST_NULL = new Array<ActionDef>();
    }
    private static final Array<ActionDef> ACTION_DEF_LIST_NORMAL_AIR;
    static {
        ACTION_DEF_LIST_NORMAL_AIR = new Array<ActionDef>();
        ACTION_DEF_LIST_NORMAL_AIR.add(ACTION_DEF_PUFF);
    }
    private static final Array<ActionDef> ACTION_DEF_LIST_NORMAL_FOOT;
    static {
        ACTION_DEF_LIST_NORMAL_FOOT = new Array<ActionDef>();
        ACTION_DEF_LIST_NORMAL_FOOT.add(ACTION_DEF_JUMP);
        ACTION_DEF_LIST_NORMAL_FOOT.add(ACTION_DEF_RUN_LEFT);
        ACTION_DEF_LIST_NORMAL_FOOT.add(ACTION_DEF_RUN_RIGHT);
    }
    private static final HashMap<ActionModifierComponent, HashMap<PlayerStateComponent, Array<ActionDef>>> actionDefMap;
    static {
        actionDefMap = new HashMap<ActionModifierComponent, HashMap<PlayerStateComponent, Array<ActionDef>>>();
        HashMap<PlayerStateComponent, Array<ActionDef>> normalMap =
                new HashMap<PlayerStateComponent, Array<ActionDef>>();
        HashMap<PlayerStateComponent, Array<ActionDef>> attackMap =
                new HashMap<PlayerStateComponent, Array<ActionDef>>();
        actionDefMap.put(ActionModifierComponent.NORMAL, normalMap);
        actionDefMap.put(ActionModifierComponent.ATTACK, attackMap);

        normalMap.put(PlayerStateComponent.AIR_ACTIVE, ACTION_DEF_LIST_NORMAL_AIR);
        normalMap.put(PlayerStateComponent.FOOT_ACTIVE, ACTION_DEF_LIST_NORMAL_FOOT);
        // TODO: Add ActionDef lists for ATTACK state
    }

    public static ActionComponent getActionComponent(
            ActionModifierComponent modifier,
            PlayerStateComponent state) {

        HashMap<PlayerStateComponent, Array<ActionDef>> modifierResult = actionDefMap.get(modifier);
        Array<ActionDef> match = ACTION_DEF_LIST_NULL;
        if (modifierResult != null) {
            Array<ActionDef> stateResult = modifierResult.get(state);
            if (stateResult != null) {
                match = stateResult;
            }
        }
        ActionComponent actionComponent = new ActionComponent();
        actionComponent.actionDefList = match;
        return actionComponent;
    }
}
