package com.ktarrant.cloudWarfare.player.body;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.ktarrant.cloudWarfare.player.PlayerState;
import com.ktarrant.cloudWarfare.world.ContactComponent;
import com.ktarrant.cloudWarfare.world.FilterCategory;
import com.ktarrant.cloudWarfare.world.WorldComponent;
import com.ktarrant.cloudWarfare.world.WorldFactory;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerFactory {

    // ---------------------------------------------------------------------------------------------
    // Constants for controlling the factory
    // ---------------------------------------------------------------------------------------------
    public static final float PLAYER_TORSO_RADIUS = 0.6f;
    public static final Vector2 DEFAULT_START_POS =
            new Vector2(0.0f, WorldFactory.PLATFORM_POS_Y + 20.0f);
    public static final float DEFAULT_MAX_STAMINA = 1.0f;

    // ---------------------------------------------------------------------------------------------
    // Shapes used in the player
    // ---------------------------------------------------------------------------------------------
    protected static final CircleShape PLAYER_TORSO_SHAPE;
    static {
        // Create a circle torsoShape and set its radius to 6
        PLAYER_TORSO_SHAPE = new CircleShape();
        PLAYER_TORSO_SHAPE.setRadius(PLAYER_TORSO_RADIUS);
    }

    // ---------------------------------------------------------------------------------------------
    // Common Definitions for the Player
    // ---------------------------------------------------------------------------------------------
    protected static final FixtureDef PLAYER_TORSO_FIXTUREDEF;
    static {
        PLAYER_TORSO_FIXTUREDEF = createBaseFixtureDef();
        PLAYER_TORSO_FIXTUREDEF.shape = PLAYER_TORSO_SHAPE;
    }

    public static BodyDef createBaseBodyDef() {
        BodyDef baseBodyDef = new BodyDef();
        baseBodyDef.type = BodyDef.BodyType.DynamicBody;
        baseBodyDef.fixedRotation = true;
        return baseBodyDef;
    }

    public static FixtureDef createBaseFixtureDef() {
        // Create a rootFixture definition to apply our torsoShape to
        FixtureDef baseFixtureDef = new FixtureDef();
        baseFixtureDef.density = 0.2f;
        baseFixtureDef.friction = 1.0f;
        baseFixtureDef.filter.categoryBits = FilterCategory.PLAYER_1.categoryBits;
        baseFixtureDef.filter.maskBits = FilterCategory.PLAYER_1.maskBits;
//        baseFixtureDef.restitution = 0.6f; // Make it bounce a little bit
        return baseFixtureDef;
    }


    public static Entity createPlayerEntity(Entity worldEntity) {
        WorldComponent worldComp = worldEntity.getComponent(WorldComponent.class);
        PlayerComponent playerComp = new PlayerComponent(worldEntity);
        Entity entity = new Entity();

        playerComp.maxStamina = DEFAULT_MAX_STAMINA;
        playerComp.stamina = playerComp.maxStamina;
        playerComp.state = PlayerState.AIR_ACTIVE;

        // Create our rootBody in the world using our torsoBody definition
        BodyDef torsoBodyDef = createBaseBodyDef();
        torsoBodyDef.position.set(DEFAULT_START_POS);
        playerComp.rootBody = worldComp.world.createBody(torsoBodyDef);
        // Create our rootFixture and attach it to the rootBody
        playerComp.rootFixture =
                playerComp.rootBody.createFixture(PLAYER_TORSO_FIXTUREDEF);

        LimbFactory.setupLimbs(worldComp, playerComp);
        entity.add(playerComp);

        // Link the contact component to the player component using the box2d fixture
        ContactComponent contactComp = new ContactComponent();
        contactComp.rootFixture = playerComp.rootFixture;
        contactComp.rootFixture.setUserData(entity);

        entity.add(contactComp);
        return entity;
    }
}
