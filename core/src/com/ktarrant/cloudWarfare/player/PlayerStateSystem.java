package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.world.BodyComponent;

/**
 * Created by Kevin on 2/28/2016.
 */
public class PlayerStateSystem extends IteratingSystem {
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);

    public PlayerStateSystem() {
        super(Family.all(
                PlayerComponent.class,
                BodyComponent.class).get(),
                SystemPriority.PLAYER_STATE.getPriorityValue());
    }

    /** TODO: Change this so it actually figures out which entity is the floor entity. */
    private static Entity getFloorEntity(Array<Entity> contactBodies) {
        if (contactBodies == null || contactBodies.size == 0) {
            return null;
        } else {
            return contactBodies.get(0);
        }
    }

    private void updateState(PlayerComponent playerComp, BodyComponent bodyComp) {
        // Compute which state we are now in
        Entity floorEntity = getFloorEntity(bodyComp.contactBodies);
        if (floorEntity == null) {
            // We are currently in the air
            if (playerComp.state.isOnFoot) {
                // The current state is on foot. Need to change it to in air
                playerComp.state = PlayerState.AIR_ACTIVE;
            } else {
                // Nothing to do here.
                // TODO: Account for active/inactive?
                return;
            }
        } else {
            // We are on a platform
            if (!playerComp.state.isOnFoot) {
                // The current state is on foot. Need to change it to in air
                playerComp.state = PlayerState.FOOT_ACTIVE;
            } else {
                // Nothing to do here.
                // TODO: Account for active/inactive?
                return;
            }
        }

        // Update any properties we can regardless of state
        bodyComp.rootBody.setFixedRotation(playerComp.state.isFixedRotation);
        bodyComp.rootBody.setAngularDamping(playerComp.state.angularDamping);

        if (playerComp.state.isOnFoot) {
            BodyComponent floorBody = bodyMapper.get(floorEntity);
            bodyComp.rootBody.setLinearDamping(
                    playerComp.state.linearDamping * floorBody.rootBody.getLinearDamping());
        } else {
            bodyComp.rootBody.setLinearDamping(playerComp.state.linearDamping);
        }

        // If the player is on Foot and fixed rotation, update which way they are facing.
        if (playerComp.state.isOnFoot && playerComp.state.isFixedRotation &&
                bodyComp.rootBody.getLinearVelocity().x != 0.0f) {
            bodyComp.rootBody.setTransform(bodyComp.rootBody.getPosition(),
                    bodyComp.rootBody.getLinearVelocity().x > 0.0f ? 0.0f : MathUtils.PI);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComp = playerMapper.get(entity);
        BodyComponent bodyComp = bodyMapper.get(entity);

        // Perform a state change if needed
        updateState(playerComp, bodyComp);

        // If the player is on Foot, replenish stamina faster
        playerComp.stamina += playerComp.state.staminaRegenRate * deltaTime;
        playerComp.stamina = (playerComp.stamina > playerComp.maxStamina) ?
                playerComp.maxStamina : playerComp.stamina;
    }
}
