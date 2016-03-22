package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.player.body.PlayerComponent;
import com.ktarrant.cloudWarfare.world.ContactComponent;

/**
 * Created by Kevin on 2/28/2016.
 */
public class PlayerStateSystem extends IteratingSystem implements EntityListener {
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<ContactComponent> contactMapper =
            ComponentMapper.getFor(ContactComponent.class);

    public PlayerStateSystem() {
        super(Family.all(PlayerComponent.class, ContactComponent.class).get(),
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

    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        // Update all players that we find
        Family playerFamily = Family.all(ContactComponent.class, PlayerComponent.class).get();
        ImmutableArray<Entity> playerEntities = engine.getEntitiesFor(playerFamily);
        for (Entity playerEntity : playerEntities) {
            updateState(playerMapper.get(playerEntity), contactMapper.get(playerEntity), true);
        }
        // Start listening for new players
        engine.addEntityListener(playerFamily, this);
    }


    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        // Stop listening for new entities
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        updateState(playerMapper.get(entity), contactMapper.get(entity), true);
    }

    @Override
    public void entityRemoved(Entity entity) {
        // Not needed
    }

    private void updateState(PlayerComponent playerComp, ContactComponent contactComp,
                             boolean force) {
        // Compute which state we are now in
        Entity floorEntity = getFloorEntity(contactComp.contactBodies);
        if (floorEntity == null) {
            // We are currently in the air
            if (playerComp.state.isOnFoot) {
                // The current state is on foot. Need to change it to in air
                playerComp.state = PlayerState.AIR_ACTIVE;
            } else {
                // Nothing to do here.
                // TODO: Account for active/inactive?
                if (!force) {
                    return;
                }
            }
        } else {
            // We are on a platform
            if (!playerComp.state.isOnFoot) {
                // The current state is on foot. Need to change it to in air
                playerComp.state = PlayerState.FOOT_ACTIVE;
            } else {
                // Nothing to do here.
                // TODO: Account for active/inactive?
                if (!force) {
                    return;
                }
            }
        }

        // Update any properties we can regardless of state
        playerComp.rootBody.setFixedRotation(playerComp.state.isFixedRotation);
        playerComp.rootBody.setAngularDamping(playerComp.state.angularDamping);
        playerComp.rootBody.setLinearDamping(playerComp.state.linearDamping);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComp = playerMapper.get(entity);

        // Perform a state change if needed
        updateState(playerComp, contactMapper.get(entity), false);

        // If the player is on Foot, replenish stamina faster
        playerComp.stamina += playerComp.state.staminaRegenRate * deltaTime;
        playerComp.stamina = (playerComp.stamina > playerComp.maxStamina) ?
                playerComp.maxStamina : playerComp.stamina;
    }
}
