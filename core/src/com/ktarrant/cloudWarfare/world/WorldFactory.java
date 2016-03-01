package com.ktarrant.cloudWarfare.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class WorldFactory {
    public static final float MAP_WIDTH = 50.0f;
    public static final float MAP_HEIGHT = 50.0f;
    public static final float MAP_KILL_MARGIN = 50.0f;
    public static final float MIN_VIEW_WIDTH = 25.0f;
    public static final float PLATFORM_WIDTH = 30.0f;
    public static final float PLATFORM_POS_Y = 10.0f;
    private static final BodyDef demoPlatformBodyDef;
    static {
        demoPlatformBodyDef = new BodyDef();
        // Set its world position
        demoPlatformBodyDef.position.set(
                new Vector2(-PLATFORM_WIDTH/2.0f, PLATFORM_POS_Y));
        // Give it some linear dampening - will be transferred to players that land on it
        demoPlatformBodyDef.linearDamping = 1.0f;
    }
    private static final PolygonShape polygonShape;
    static {
        polygonShape = new PolygonShape();
        polygonShape.setAsBox(PLATFORM_WIDTH, 1.0f);
    }
    private static final FixtureDef demoPlatformFixtureDef;
    static {
        // Create a fixture definition to apply our shape to
        demoPlatformFixtureDef = new FixtureDef();
        demoPlatformFixtureDef.shape = polygonShape;
        demoPlatformFixtureDef.density = 0.2f;
//        demoPlatformFixtureDef.friction = 1.0f;
    }

    public BodyComponent createPlatformComponent(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        BodyComponent bodyComp = new BodyComponent();

        // Use the static body and fixture defs
        bodyComp.bodyDef = demoPlatformBodyDef;
        bodyComp.fixtureDef = demoPlatformFixtureDef;

        // Create the body and the fixture using the world
        bodyComp.body = worldComp.world.createBody(bodyComp.bodyDef);
        bodyComp.fixture = bodyComp.body.createFixture(demoPlatformFixtureDef);

        // Keep a reference to the parent world
        bodyComp.worldEntity = worldEntity;

        return bodyComp;
    }

    public static BoundsComponent createDemoBoundsComponent() {
        BoundsComponent boundsComp = new BoundsComponent();
        boundsComp.bounds.set((-MAP_WIDTH / 2.0f) - MAP_KILL_MARGIN,
                -MAP_KILL_MARGIN,
                (MAP_WIDTH / 2.0f) + MAP_KILL_MARGIN,
                MAP_HEIGHT + MAP_KILL_MARGIN);
        return boundsComp;
    }

    public static CameraComponent createDemoCameraComponent() {
        // Set up the camera view
        CameraComponent cameraComp = new CameraComponent();
        OrthographicCamera orthoCam = new OrthographicCamera();
        orthoCam.setToOrtho(false, MIN_VIEW_WIDTH,
                MIN_VIEW_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        cameraComp.camera = orthoCam;
        return cameraComp;
    }


    public Entity createDemoWorld() {
        // Create the world
        WorldComponent worldComp = new WorldComponent();
        worldComp.world = new World(new Vector2(0, -10.0f), true);

        // Create the playable bounds
        BoundsComponent boundsComp = createDemoBoundsComponent();

        // Create a demo camera
        CameraComponent cameraComp = createDemoCameraComponent();

        // Create the world entity
        Entity worldEntity = new Entity();
        worldEntity.add(worldComp);
        worldEntity.add(boundsComp);
        worldEntity.add(cameraComp);

        return worldEntity;
    }

    public void dispose() {
        polygonShape.dispose();
    }
}
