package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2/28/2016.
 */
public class ContactSystem extends EntitySystem implements ContactListener, EntityListener {
    private HashMap<Fixture, Entity> entityMap;
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<WorldComponent> worldMapper = ComponentMapper.getFor(WorldComponent.class);

    public ContactSystem() {
        this.entityMap = new HashMap<Fixture, Entity>();
    }

    public void addedToEngine(Engine engine) {
        // Register ourselves with all worlds we can find
        ImmutableArray<Entity> worldEntities = engine.getEntitiesFor(Family.all(
                WorldComponent.class).get());
        for (Entity worldEntity : worldEntities) {
            WorldComponent worldComp = worldMapper.get(worldEntity);
            worldComp.world.setContactListener(this);
        }

        // Create the Contact HashMap
        Family family = Family.all(BodyComponent.class).get();
        ImmutableArray<Entity> bodyEntities = engine.getEntitiesFor(family);
        for (Entity entity : bodyEntities) {
            BodyComponent bodyComp = bodyMapper.get(entity);
            entityMap.put(bodyComp.fixture, entity);
        }

        // Start listening for new entities
        engine.addEntityListener(family, this);
    }

    public void removedFromEngine(Engine engine) {
        // Stop listening for new entities
        engine.removeEntityListener(this);

        // Unregister ourselves with all worlds we can find
        ImmutableArray<Entity> worldEntities = engine.getEntitiesFor(Family.all(
                WorldComponent.class).get());
        for (Entity worldEntity : worldEntities) {
            WorldComponent worldComp = worldMapper.get(worldEntity);
            worldComp.world.setContactListener(null);
        }

        // Clear our hash map
        entityMap.clear();
    }

    public void entityAdded (Entity entity) {
        BodyComponent bodyComp = bodyMapper.get(entity);
        entityMap.put(bodyComp.fixture, entity);
    }

    public void entityRemoved (Entity entity) {
        BodyComponent bodyComp = bodyMapper.get(entity);
        entityMap.remove(bodyComp.fixture);

        // Remove this entity from all contact bodies lists
        for (Map.Entry<Fixture, Entity> entry : entityMap.entrySet()) {
            BodyComponent comp = bodyMapper.get(entry.getValue());
            comp.contactBodies.removeValue(entity, false);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Entity entityA = entityMap.get(contact.getFixtureA());
        Entity entityB = entityMap.get(contact.getFixtureB());
        if (entityA != null && entityB != null) {
            // Both of these entities are managed by us
            bodyMapper.get(entityA).contactBodies.add(entityB);
            bodyMapper.get(entityB).contactBodies.add(entityA);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = entityMap.get(contact.getFixtureA());
        Entity entityB = entityMap.get(contact.getFixtureB());
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
}
