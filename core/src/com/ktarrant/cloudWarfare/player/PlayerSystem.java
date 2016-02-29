package com.ktarrant.cloudWarfare.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.action.ActionSystem;
import com.ktarrant.cloudWarfare.action.ActionComponent;
import com.ktarrant.cloudWarfare.action.ActionDefLoader;
import com.ktarrant.cloudWarfare.action.ActionModifierComponent;
import com.ktarrant.cloudWarfare.world.BodyComponent;
import com.ktarrant.cloudWarfare.world.CameraComponent;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/** PlayerSystem - A class for managing the interaction of PlayerComponent objects with the World and
 *  each other.
 *
 * updateModifier - Updates the current ActionModifierComponent input from the user
 *
 * updateCursorChange - Updates the current touch input from the user
 *
 * setActivePlayerComponent - Sets the currently active (controlled) player
 *
 * update(delta) - Updates the state of all controlled players.
 *
 * drawDebug(renderer) - Draws the Players using a PlayerRenderer
 *
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerSystem extends IteratingSystem {
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<PlayerStateComponent> stateMapper =
            ComponentMapper.getFor(PlayerStateComponent.class);
    private ComponentMapper<BodyComponent> bodyMapper = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<CameraComponent> cameraMapper =
            ComponentMapper.getFor(CameraComponent.class);

    private Entity activePlayer;

    public PlayerSystem() {
        super(Family.all(
                PlayerComponent.class,
                BodyComponent.class,
                PlayerStateComponent.class).get(),
                SystemPriority.PLAYER.getPriorityValue());
    }

    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        // Reset the active player
        activePlayer = null;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // If we don't have an active player, use the first one we get
        if (activePlayer == null) {
            activePlayer = entity;
        }

        // Handle updates specific to the active player
        if (activePlayer == entity) {
            // Update the camera to be centered on the active player
            BodyComponent bodyComp = bodyMapper.get(entity);
            PlayerComponent playerComp = playerMapper.get(entity);
            CameraComponent cameraComp = cameraMapper.get(playerComp.worldEntity);
            Camera camera = cameraComp.camera;
            camera.position.set(bodyComp.body.getPosition(), 0);
            camera.update();
        }
    }
//    public static final Vector2 DEFAULT_START_POS = new Vector2(0.0f, 10.0f);
//    public static final int MAX_CONCURRENT_ACTIONS = 16;
//
//    // --- Static-ish parameters of the World ---
//    /** Start position for player spawn. TODO: Intelligently place new players. */
//    protected Vector2 startPos;
//
//    // --- State Variables for PlayerComponent ---
//    /** Collection of the PlayerComponent s being managed. */
//    protected Array<PlayerComponent> players;
//    /** The PlayerComponent currently under control. */
//    protected PlayerComponent activePlayerComponent;
//
//    // --- State Variables for Control ---
//    /** Place to get ActionComponent lists from. TODO: Load ActionComponent list from config file. */
//    protected ActionDefLoader actionDefLoader;
//    /** List of ActionComponent s available for this PlayerStateComponent/ActionModifierComponent combination. */
//    protected Array<ActionComponent> curActionDefList;
//    /** Queue of Actions to be performed in this frame. */
//    protected Queue<ActionSystem> actionSystemQueue;
//    /** The ActionComponent currently being repeated. */
//    protected ActionComponent activeActionComponent;
//    /** Time passed since the last ActionSystem performed. */
//    protected float timeSinceLastAction;
//    /** Current position of the user touch, or null if not being touched. */
//    protected Vector2 curScreenPos;
//    /** ActionModifierComponent of activePlayerComponent .*/
//    protected ActionModifierComponent activeActionModifierComponent;
//
//    /**
//     * Create a new PlayerSystem.
//     * @param world World the controlled PlayerComponent s will be in.
//     * @param camera Camera used to render the PlayerComponent s.
//     */
//    public PlayerSystem(World world, Camera camera) {
//        this.world = world;
//        this.camera = camera;
//        this.startPos = DEFAULT_START_POS;
//        this.bounds = null;
//
//        this.players = new Array<PlayerComponent>();
//        this.activePlayerComponent = null;
//
//        this.actionDefLoader = new ActionDefLoader();
//        this.updateActionDefList();
//        this.actionSystemQueue = new ArrayBlockingQueue<ActionSystem>(MAX_CONCURRENT_ACTIONS);
//        this.timeSinceLastAction = 0.0f;
//        this.curScreenPos = null;
//
//        // Set us as the contact listener so we can catch player contacts
//        world.setContactListener(this);
//
//        // Load the ActionComponent lists from wherever they come from
//        actionDefLoader.load();
//
//        // Set up the ActionModifierComponent with a default value
//        updateModifier(ActionModifierComponent.NORMAL);
//    }
//
//    /**
//     * Check if the given direction vector lies inside this ActionComponent 's capture angle zone.
//     * TODO: Optimize this process. We are leaning on angle computations here and they are
//     * probably very expensive.
//     * @param actionLengthAngle
//     *@param actionStartAngle @param direction Direction vector
//     * @return True if the direction vector lies in our capture angle zone.
//     */
//    public static boolean isInCaptureZone(float actionLengthAngle, float actionStartAngle, Vector2 direction) {
//        float angle = direction.angleRad();
//        float diff = angle - actionStartAngle;
//        if (diff < 0.0f && (diff + MathUtils.PI2 < actionLengthAngle)) {
//            return true;
//        } else if (diff > 0.0f && (diff < actionLengthAngle)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * Updates the current ActionModifierComponent input from the user.
//     * @param modifier New ActionModifierComponent value.
//     */
//    public void updateModifier(ActionModifierComponent modifier) {
//        activeActionModifierComponent = modifier;
//        updateActionDefList();
//    }
//
//    /**
//     * Updates the current touch input from the user.
//     * @param cursorPos New screen location of the touch input
//     * @param touchedDown true if the user touched the screen, false otherwise.
//     */
//    public void updateCursorChange(Vector2 cursorPos, boolean touchedDown) {
//        curScreenPos = cursorPos;
//        if (curScreenPos == null) {
//            activeActionComponent = null;
//            timeSinceLastAction = 0.0f;
//            return;
//        }
//
//        if (activePlayerComponent == null) {
//            return;
//        }
//
//        for (ActionComponent actionComponent : curActionDefList) {
//            if (isInCaptureZone(actionComponent.actionLengthAngle, actionComponent.actionStartAngle, curScreenPos)) {
//                // Set this up to be repeated later
//                activeActionComponent = actionComponent;
//
//                if (touchedDown) {
//                    addActionToQueue(activeActionModifierComponent, activeActionComponent);
//                }
//                break;
//            }
//        }
//    }
//
//    /**
//     * Adds a new PlayerComponent to the World. If this is the first PlayerComponent being added, that PlayerComponent
//     * will automatically be set as the active (controlled) PlayerComponent.
//     */
//    public PlayerComponent addNewPlayer() {
//        PlayerComponent playerComponent = PlayerFactory.createNewPlayer(world);
//        playerComponent.body.setTransform(startPos, 0.0f);
//        players.add(playerComponent);
//        if (players.size == 1) {
//            setActivePlayerComponent(playerComponent);
//        }
//        return playerComponent;
//    }
//
//    /**
//     * Sets the currently active (controlled) playerComponent
//     * @param playerComponent New Active PlayerComponent. Must be one of the managed PlayerComponent s.
//     */
//    public void setActivePlayerComponent(PlayerComponent playerComponent) {
//        activePlayerComponent = playerComponent;
//        updateActionDefList();
//    }
//
//    @Override
//    protected void processEntity(Entity entity, float deltaTime) {
////        ActionSystem actionSystem;
////
////        // First, repeat last ActionSystem if necessary
////        if (activeActionComponent != null) {
////            timeSinceLastAction += delta;
////            while (timeSinceLastAction > activeActionComponent.repeatInterval) {
////                // It has been long enough since the last ActionSystem to perform it again
////                addActionToQueue(activeActionModifierComponent, activeActionComponent);
////                timeSinceLastAction -= activeActionComponent.repeatInterval;
////            }
////        }
////
////        // Second, perform all the Actions in the queue
////        while (!actionSystemQueue.isEmpty()) {
////            actionSystem = actionSystemQueue.poll();
////            actionSystem.execute(delta);
////        }
////
////        // Finally, update the state of all the players
////        for (PlayerComponent playerComponent : players) {
////            playerComponent.update(delta);
////        }
//    }
//
//    /**
//     * Draws the Players for debug purposes using a PlayerRenderer
//     * @param renderer PlayerRenderer to use to draw.
//     */
//    public void drawDebug(PlayerRenderer renderer) {
//        if (activePlayerComponent != null) {
//            renderer.updateEnvironmentData("Stamina", activePlayerComponent.getStamina());
//            renderer.setProjectionMatrix(camera.combined);
//            renderer.begin();
//
//            renderer.drawPlayerControlHelp(activePlayerComponent, curActionDefList);
//            renderer.end();
//            renderer.drawEnvironmentData(activePlayerComponent);
//            renderer.drawPlayerState(activePlayerComponent);
//        }
//    }
//
//    /**
//     * Returns the World position of the active PlayerComponent.
//     * @return Position of the PlayerComponent, or null if no active PlayerComponent.
//     */
//    public Vector2 getActivePosition() {
//        if (activePlayerComponent == null) {
//            return null;
//        } else {
//            return activePlayerComponent.body.getPosition();
//        }
//    }
//
//    /**
//     * Set the bounds of the map, beyond which PlayerComponent s must respawn.
//     * @param mapMinX Smallest X-value within bounds on the map.
//     * @param mapMinY Largest X-value within bounds on the map.
//     * @param mapMaxX Smallest Y-value within bounds on the map.
//     * @param mapMaxY Largest Y-value within bounds on the map.
//     */
//    public void setBounds(float mapMinX, float mapMinY, float mapMaxX, float mapMaxY) {
//        bounds = new Rectangle(mapMinX, mapMinY, mapMaxX - mapMinX, mapMaxY - mapMinY);
//    }
//
//    /**
//     * Updates the ActionComponent list for the active PlayerComponent and ActionModifer combination.
//     */
//    protected void updateActionDefList() {
//        if (activePlayerComponent == null) {
//            // Create empty array to use as placeholder
//            curActionDefList = new Array<ActionComponent>();
//            return;
//        } else {
//            // Load the ActionComponent list for the current PlayerStateComponent/ActionModifierComponent combination
//            curActionDefList = actionDefLoader.getActionDefList(
//                    activeActionModifierComponent,
//                    activePlayerComponent.state);
//        }
//    }
//
//    /**
//     * Add an ActionSystem to the queue with the current screen position and active PlayerComponent.
//     * @param modifier ActionModifierComponent of the action to add.
//     * @param actionComponent ActionComponent of the action to add.
//     */
//    protected void addActionToQueue(ActionModifierComponent modifier, ActionComponent actionComponent) {
//        ActionSystem actionSystem = new ActionSystem(
//                activePlayerComponent,
//                modifier,
//                actionComponent,
//                curScreenPos.nor()
//        );
//        actionSystemQueue.add(actionSystem);
//    }
//
//    /**
//     * Check if any PlayerComponent s have gone out of bounds, and if they have then restore
//     * them to the start position.
//     */
//    protected void checkBounds() {
//        if (bounds != null) {
//            for (PlayerComponent playerComponent : players) {
//                Vector2 pos = playerComponent.body.getPosition();
//                if (!bounds.contains(pos)) {
//                    // Reset the playerComponent and put them back in the start position
//                    playerComponent.body.setAngularVelocity(0.0f);
//                    playerComponent.body.setLinearVelocity(0.0f, 0.0f);
//                    playerComponent.body.setTransform(startPos, 0.0f);
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public void dispose() {
//        for (PlayerComponent playerComponent : this.players) {
//            if (playerComponent.circleShape != null) {
//                playerComponent.circleShape.dispose();
//            }
//        }
//    }
//
//    @Override
//    public void preSolve(Contact contact, Manifold oldManifold) {
//
//    }
//
//    @Override
//    public void postSolve(Contact contact, ContactImpulse impulse) {
//
//    }
//
//    @Override
//    public void beginContact(Contact contact) {
//        Fixture fixA = contact.getFixtureA();
//        Fixture fixB = contact.getFixtureB();
//
//        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
//            for (PlayerComponent playerComponent : players) {
//                if (playerComponent.fixture == fixB) {
//                    playerComponent.contactBodies.add(fixA.getBody());
//                    playerComponent.setState(PlayerStateComponent.FOOT_ACTIVE);
//                    if (playerComponent == activePlayerComponent) {
//                        updateActionDefList();
//                    }
//                    return;
//                }
//            }
//        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
//            for (PlayerComponent playerComponent : players) {
//                if (playerComponent.fixture == fixA) {
//                    playerComponent.contactBodies.add(fixA.getBody());
//                    playerComponent.setState(PlayerStateComponent.FOOT_ACTIVE);
//                    if (playerComponent == activePlayerComponent) {
//                        updateActionDefList();
//                    }
//                    return;
//                }
//            }
//        } // else do nothing
//    }
//
//    @Override
//    public void endContact(Contact contact) {
//        Fixture fixA = contact.getFixtureA();
//        Fixture fixB = contact.getFixtureB();
//
//        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
//            for (PlayerComponent playerComponent : players) {
//                if (playerComponent.fixture == fixB) {
//                    playerComponent.contactBodies.remove(fixA.getBody());
//                    if (playerComponent.contactBodies.isEmpty()) {
//                        playerComponent.setState(PlayerStateComponent.AIR_ACTIVE);
//                        if (playerComponent == activePlayerComponent) {
//                            updateActionDefList();
//                        }
//                    }
//                    return;
//                }
//            }
//        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
//            for (PlayerComponent playerComponent : players) {
//                if (playerComponent.fixture == fixA) {
//                    playerComponent.contactBodies.remove(fixB.getBody());
//                    if (playerComponent.contactBodies.isEmpty()) {
//                        playerComponent.setState(PlayerStateComponent.AIR_ACTIVE);
//                        if (playerComponent == activePlayerComponent) {
//                            updateActionDefList();
//                        }
//                    }
//                    return;
//                }
//            }
//        }
//    }
}
