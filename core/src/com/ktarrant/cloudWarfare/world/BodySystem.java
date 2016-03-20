package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.player.PlayerFactory;

/**
 * Created by Kevin on 2/28/2016.
 */
public class BodySystem extends IteratingSystem implements ContactListener {
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<WorldComponent> worldMapper = ComponentMapper.getFor(WorldComponent.class);
    private ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);

    public BodySystem() {
        super(Family.all(BodyComponent.class).get(), SystemPriority.BODY.getPriorityValue());
    }

    public final EntityListener worldListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            registerWorldEntity(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {
            unregisterWorldEntity(entity);
        }
    };

    public final EntityListener bodyListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            registerBodyEntity(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {
            unregisterBodyEntity(entity);
        }
    };

    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        // Register all worlds we can find
        Family worldFamily = Family.all(WorldComponent.class).get();
        ImmutableArray<Entity> worldEntities = engine.getEntitiesFor(worldFamily);
        for (Entity worldEntity : worldEntities) {
            registerWorldEntity(worldEntity);
        }
        // Start listening for new worlds
        engine.addEntityListener(worldFamily, worldListener);

        // Register all the bodies we can find
        Family bodyFamily = Family.all(BodyComponent.class).get();
        ImmutableArray<Entity> bodyEntities = engine.getEntitiesFor(bodyFamily);
        for (Entity bodyEntity : bodyEntities) {
            registerBodyEntity(bodyEntity);
        }
        // Start listening for new bodies
        engine.addEntityListener(bodyFamily, bodyListener);
    }

    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        // Stop listening for new entities
        engine.removeEntityListener(worldListener);
        engine.removeEntityListener(bodyListener);

        // Unregister ourselves with all worlds we can find
        ImmutableArray<Entity> worldEntities = engine.getEntitiesFor(Family.all(
                WorldComponent.class).get());
        for (Entity worldEntity : worldEntities) {
            unregisterWorldEntity(worldEntity);
        }

        // Clean up the fixtures of all the rootBody entities
        ImmutableArray<Entity> bodyEntities = engine.getEntitiesFor(Family.all(
                BodyComponent.class).get());
        for (Entity bodyEntity : bodyEntities) {
            unregisterWorldEntity(bodyEntity);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComp = bodyMapper.get(entity);
        BoundsComponent boundsComp = boundsMapper.get(bodyComp.worldEntity);

        // Check if this entity has fallen off the bounds
        Body playerBody = bodyComp.rootBody;
        if (!boundsComp.bounds.contains(playerBody.getPosition())) {
            // Reset the playerComponent and put them back in the start position
            // TODO: We need to tell the player that they just died.
            playerBody.setAngularVelocity(0.0f);
            playerBody.setLinearVelocity(0.0f, 0.0f);
            playerBody.setTransform(PlayerFactory.DEFAULT_START_POS, 0.0f);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();
        if (entityA != null && entityB != null) {
            // Both of these entities are managed by us
            bodyMapper.get(entityA).contactBodies.add(entityB);
            bodyMapper.get(entityB).contactBodies.add(entityA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();
        if (entityA != null && entityB != null) {
            // Both of these entities are managed by us
            bodyMapper.get(entityA).contactBodies.removeValue(entityB, false);
            bodyMapper.get(entityB).contactBodies.removeValue(entityA, false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    protected void registerWorldEntity(Entity worldEntity) {
        WorldComponent worldComp = worldMapper.get(worldEntity);
        worldComp.world.setContactListener(this);
    }

    protected void unregisterWorldEntity(Entity worldEntity) {
        WorldComponent worldComp = worldMapper.get(worldEntity);
        worldComp.world.setContactListener(null);
    }

    protected void registerBodyEntity(Entity bodyEntity) {
        BodyComponent bodyComp = bodyMapper.get(bodyEntity);
        // Use the rootFixture to get a reference to the entity
        bodyComp.rootFixture.setUserData(bodyEntity);
    }

    protected void unregisterBodyEntity(Entity bodyEntity) {
        BodyComponent bodyComp = bodyMapper.get(bodyEntity);
        // Remove this entity from all contact bodies lists
        for (Entity contactEntity : bodyComp.contactBodies) {
            BodyComponent comp = bodyMapper.get(contactEntity);
            comp.contactBodies.removeValue(bodyEntity, false);
        }
    }
}
