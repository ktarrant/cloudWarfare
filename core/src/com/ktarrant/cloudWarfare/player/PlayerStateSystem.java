package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.world.BodyComponent;

/**
 * Created by Kevin on 2/28/2016.
 */
public class PlayerStateSystem extends IteratingSystem {
    private ComponentMapper<PlayerStaminaComponent> staminaMapper =
            ComponentMapper.getFor(PlayerStaminaComponent.class);
    private ComponentMapper<PlayerStateComponent> stateMapper =
            ComponentMapper.getFor(PlayerStateComponent.class);
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

    public PlayerStateSystem() {
        super(Family.all(
                PlayerStaminaComponent.class,
                PlayerStateComponent.class,
                BodyComponent.class).get());
    }

    /** TODO: Change this so it actually figures out which entity is the floor entity. */
    private static Entity getFloorBody(Array<Entity> contactBodies) {
        if (contactBodies == null || contactBodies.size == 0) {
            return null;
        } else {
            return contactBodies.get(0);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerStaminaComponent staminaComp = staminaMapper.get(entity);
        PlayerStateComponent stateComp = stateMapper.get(entity);
        BodyComponent bodyComp = bodyMapper.get(entity);

        // Update any properties we can regardless of state
        bodyComp.body.setFixedRotation(stateComp.isFixedRotation);
        bodyComp.body.setAngularDamping(stateComp.angularDamping);

        if (stateComp.isOnFoot) {
            Entity floorEntity = getFloorBody(bodyComp.contactBodies);
            BodyComponent floorBody = bodyMapper.get(floorEntity);
            bodyComp.body.setLinearDamping(
                    stateComp.linearDamping * floorBody.body.getLinearDamping());
        } else {
            bodyComp.body.setLinearDamping(stateComp.linearDamping);
        }

        // If the player is on Foot and fixed rotation, update which way they are facing.
        if (stateComp.isOnFoot && stateComp.isFixedRotation &&
                bodyComp.body.getLinearVelocity().x != 0.0f) {
            bodyComp.body.setTransform(bodyComp.body.getPosition(),
                    bodyComp.body.getLinearVelocity().x > 0.0f ? 0.0f : MathUtils.PI);
        }

        // If the player is on Foot, replenish stamina faster
        staminaComp.stamina += stateComp.staminaRegenRate * deltaTime;
        staminaComp.stamina = (staminaComp.stamina > staminaComp.maxStamina) ?
                staminaComp.maxStamina : staminaComp.stamina;
    }
}
