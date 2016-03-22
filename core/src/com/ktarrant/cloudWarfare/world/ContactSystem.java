package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Kevin on 2/28/2016.
 */
public class ContactSystem extends EntitySystem implements ContactListener {
    private ComponentMapper<ContactComponent> contactMapper = ComponentMapper.getFor(ContactComponent.class);
    private ComponentMapper<WorldComponent> worldMapper = ComponentMapper.getFor(WorldComponent.class);
    private ComponentMapper<BoundsComponent> boundsMapper = ComponentMapper.getFor(BoundsComponent.class);

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

    public final EntityListener contactListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            registerContactEntity(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {
            unregisterContactEntity(entity);
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
        Family contactFamily = Family.all(ContactComponent.class).get();
        ImmutableArray<Entity> contactEntities = engine.getEntitiesFor(contactFamily);
        for (Entity contactEntity : contactEntities) {
            registerContactEntity(contactEntity);
        }
        // Start listening for new bodies
        engine.addEntityListener(contactFamily, contactListener);
    }

    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        // Stop listening for new entities
        engine.removeEntityListener(worldListener);
        engine.removeEntityListener(contactListener);

        // Unregister ourselves with all worlds we can find
        ImmutableArray<Entity> worldEntities = engine.getEntitiesFor(Family.all(
                WorldComponent.class).get());
        for (Entity worldEntity : worldEntities) {
            unregisterWorldEntity(worldEntity);
        }

        // Clean up the fixtures of all the rootBody entities
        ImmutableArray<Entity> contactEntities = engine.getEntitiesFor(Family.all(
                ContactComponent.class).get());
        for (Entity contactEntity : contactEntities) {
            unregisterWorldEntity(contactEntity);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();
        if (entityA != null && entityB != null) {
            // Both of these entities are managed by us
            contactMapper.get(entityA).contactBodies.add(entityB);
            contactMapper.get(entityB).contactBodies.add(entityA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();
        if (entityA != null && entityB != null) {
            // Both of these entities are managed by us
            contactMapper.get(entityA).contactBodies.removeValue(entityB, false);
            contactMapper.get(entityB).contactBodies.removeValue(entityA, false);
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

    protected void registerContactEntity(Entity contactEntity) {
        ContactComponent contactComp = contactMapper.get(contactEntity);
        // Use the rootFixture to get a reference to the entity
        contactComp.rootFixture.setUserData(contactEntity);
    }

    protected void unregisterContactEntity(Entity contactEntity) {
        ContactComponent contactComp = contactMapper.get(contactEntity);
        // Remove this entity from all contact bodies lists
        for (Entity entity : contactComp.contactBodies) {
            ContactComponent comp = contactMapper.get(entity);
            comp.contactBodies.removeValue(contactEntity, false);
        }
    }
}
