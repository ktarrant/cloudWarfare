package com.ktarrant.cloudWarfare.action;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.input.TouchComponent;
import com.ktarrant.cloudWarfare.player.PlayerComponent;
import com.ktarrant.cloudWarfare.world.BodyComponent;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<ActionComponent> actionMapper =
            ComponentMapper.getFor(ActionComponent.class);
    private ComponentMapper<ActionModifierComponent> actionModMapper =
            ComponentMapper.getFor(ActionModifierComponent.class);
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<TouchComponent> touchMapper =
            ComponentMapper.getFor(TouchComponent.class);

    public ActionSystem() {
        super(Family.all(
                PlayerComponent.class,
                ActionComponent.class,
                ActionModifierComponent.class,
                BodyComponent.class,
                TouchComponent.class).get(),
                SystemPriority.ACTION.getPriorityValue());
    }

    /**
     * Check if the given direction vector lies inside this ActionComponent 's capture angle zone.
     * TODO: Optimize this process. We are leaning on angle computations here and they are
     * probably very expensive.
     * @param actionLengthAngle
     * @param actionStartAngle
     * @param direction Direction vector
     * @return True if the direction vector lies in our capture angle zone.
     */
    public static boolean isInCaptureZone(float actionLengthAngle, float actionStartAngle, Vector2 direction) {
        float angle = direction.angleRad();
        float diff = angle - actionStartAngle;
        if (diff < 0.0f && (diff + MathUtils.PI2 < actionLengthAngle)) {
            return true;
        } else if (diff > 0.0f && (diff < actionLengthAngle)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TouchComponent touchComp = touchMapper.get(entity);
        ActionComponent actionComp = actionMapper.get(entity);

        // See if this touch input has triggered any actions
        ActionDef match = null;
        for (ActionDef actionDef : actionComp.actionDefList) {
            if (isInCaptureZone(actionDef.actionLengthAngle, actionDef.actionStartAngle,
                    touchComp.playerTouchVector)) {
                match = actionDef;
                break;
            }
        }

        if (match != null) {
            // We found an ActionDef match, that means we can create execute the action
            PlayerComponent playerComp = playerMapper.get(entity);
            BodyComponent bodyComp = bodyMapper.get(entity);

            float staminaAfter = playerComp.stamina * match.depleteMultiplier
                    - match.depleteConstant;
            if (staminaAfter >= 0.0f) {
                Vector2 direction = touchComp.playerTouchVector.nor();
                direction.scl(match.linearImpulseMultiplier);
                direction.add(match.linearImpulseConstant);
                direction.scl(playerComp.stamina);
                bodyComp.rootBody.applyLinearImpulse(direction, Vector2.Zero, true);
                playerComp.stamina = staminaAfter;
            }
        }

        // Finished processing Action, remove it from Engine
        getEngine().removeEntity(entity);
    }
}
