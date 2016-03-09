package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ktarrant.cloudWarfare.SystemPriority;

/**
 * Created by Kevin on 2/28/2016.
 */
public class DebugRendererSystem extends IteratingSystem {
    private ComponentMapper<WorldComponent> worldMapper
            = ComponentMapper.getFor(WorldComponent.class);
    private ComponentMapper<CameraComponent> cameraMapper
            = ComponentMapper.getFor(CameraComponent.class);

    private Box2DDebugRenderer debugRenderer;

    public DebugRendererSystem() {
        super(Family.all(WorldComponent.class, CameraComponent.class).get(),
                SystemPriority.WORLD_RENDER.getPriorityValue());

        // Create a renderer that annotates the objects
        this.debugRenderer = new Box2DDebugRenderer(
                true, 	// boolean drawBodies
                true, 	// boolean drawJoints
                false, 	// boolean drawAABBs
                true, 	// boolean drawInactiveBodies
                true, 	// boolean drawVelocities
                true  	// boolean drawContacts
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        World world = worldMapper.get(entity).world;
        Camera camera = cameraMapper.get(entity).camera;

        // Set the camera to center on the current player
//        camera.position.set(playerSystem.getActivePosition(), 0);
//        camera.update();
        this.debugRenderer.render(world, camera.combined);
    }
}
