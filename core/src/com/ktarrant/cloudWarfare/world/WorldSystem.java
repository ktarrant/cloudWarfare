package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.ktarrant.cloudWarfare.SystemPriority;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2/28/2016.
 */
public class WorldSystem extends IteratingSystem {
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float TIME_STEP = 1 / 60.0f;

    private ComponentMapper<WorldComponent> worldMapper
            = ComponentMapper.getFor(WorldComponent.class);
    private float accumulator;

    public WorldSystem() {
        super(Family.all(WorldComponent.class).get(),
                SystemPriority.WORLD.getPriorityValue());
    }

    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        // Reset the accumulator
        accumulator = 0.0f;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        World world = worldMapper.get(entity).world;
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            // Update the physics of the world
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
}
