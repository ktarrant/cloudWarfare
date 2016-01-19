package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ktarrant.cloudWarfare.action.Action;
import com.ktarrant.cloudWarfare.action.ActionDef;
import com.ktarrant.cloudWarfare.action.ActionDefLoader;
import com.ktarrant.cloudWarfare.action.ActionModifier;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/** PlayerManager - A class for managing the interaction of Player objects with the World and
 *  each other.
 *
 * updateModifier - Updates the current ActionModifier input from the user
 *
 * updateCursorChange - Updates the current touch input from the user
 *
 * setActivePlayer - Sets the currently active (controlled) player
 *
 * update(delta) - Updates the state of all controlled players.
 *
 * drawDebug(renderer) - Draws the Players using a PlayerRenderer
 *
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerManager implements ContactListener, Disposable {
    public static final Vector2 DEFAULT_START_POS = new Vector2(0.0f, 10.0f);
    public static final int MAX_CONCURRENT_ACTIONS = 16;

    // --- Static-ish parameters of the World ---
    /** The World containing all the Player s */
    protected final World world;
    /** The Camera being used to render the Players s */
    protected final Camera camera;
    /** Start position for player spawn. TODO: Intelligently place new players. */
    protected Vector2 startPos;
    /** Bounds of the Map to confine players in. */
    protected Rectangle bounds;

    // --- State Variables for Player ---
    /** Collection of the Player s being managed. */
    protected Array<Player> players;
    /** The Player currently under control. */
    protected Player activePlayer;

    // --- State Variables for Control ---
    /** Place to get ActionDef lists from. TODO: Load ActionDef list from config file. */
    protected ActionDefLoader actionDefLoader;
    /** List of ActionDef s available for this PlayerState/ActionModifier combination. */
    protected Array<ActionDef> curActionDefList;
    /** Queue of Actions to be performed in this frame. */
    protected Queue<Action> actionQueue;
    /** The ActionDef currently being repeated. */
    protected ActionDef activeActionDef;
    /** Time passed since the last Action performed. */
    protected float timeSinceLastAction;
    /** Current position of the user touch, or null if not being touched. */
    protected Vector2 curScreenPos;
    /** ActionModifier of activePlayer .*/
    protected ActionModifier activeActionModifier;

    /**
     * Create a new PlayerManager.
     * @param world World the controlled Player s will be in.
     * @param camera Camera used to render the Player s.
     */
    public PlayerManager(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
        this.startPos = DEFAULT_START_POS;
        this.bounds = null;

        this.players = new Array<Player>();
        this.activePlayer = null;

        this.actionDefLoader = new ActionDefLoader();
        this.updateActionDefList();
        this.actionQueue = new ArrayBlockingQueue<Action>(MAX_CONCURRENT_ACTIONS);
        this.timeSinceLastAction = 0.0f;
        this.curScreenPos = null;

        // Set us as the contact listener so we can catch player contacts
        world.setContactListener(this);

        // Load the ActionDef lists from wherever they come from
        actionDefLoader.load();

        // Set up the ActionModifier with a default value
        updateModifier(ActionModifier.NORMAL);
    }

    /**
     * Updates the current ActionModifier input from the user.
     * @param modifier New ActionModifier value.
     */
    public void updateModifier(ActionModifier modifier) {
        activeActionModifier = modifier;
        updateActionDefList();
    }

    /**
     * Updates the current touch input from the user.
     * @param cursorPos New screen location of the touch input
     * @param touchedDown true if the user touched the screen, false otherwise.
     */
    public void updateCursorChange(Vector2 cursorPos, boolean touchedDown) {
        curScreenPos = cursorPos;
        if (curScreenPos == null) {
            activeActionDef = null;
            timeSinceLastAction = 0.0f;
            return;
        }

        if (activePlayer == null) {
            return;
        }

        for (ActionDef actionDef : curActionDefList) {
            if (actionDef.isInCaptureZone(curScreenPos)) {
                // Set this up to be repeated later
                activeActionDef = actionDef;

                if (touchedDown) {
                    addActionToQueue(activeActionModifier, activeActionDef);
                }
                break;
            }
        }
    }

    /**
     * Adds a new Player to the World. If this is the first Player being added, that Player
     * will automatically be set as the active (controlled) Player.
     */
    public Player addNewPlayer() {
        Player player = PlayerFactory.createNewPlayer(world);
        player.body.setTransform(startPos, 0.0f);
        players.add(player);
        if (players.size == 1) {
            setActivePlayer(player);
        }
        return player;
    }

    /**
     * Sets the currently active (controlled) player
     * @param player New Active Player. Must be one of the managed Player s.
     */
    public void setActivePlayer(Player player) {
        activePlayer = player;
        updateActionDefList();
    }

    /**
     * Process any Player input from this frame and update the state of all the Player s.
     * @param delta Time passed since last update
     */
    public void update(float delta) {
        Action action;

        // First, repeat last Action if necessary
        if (activeActionDef != null) {
            timeSinceLastAction += delta;
            while (timeSinceLastAction > activeActionDef.repeatInterval) {
                // It has been long enough since the last Action to perform it again
                addActionToQueue(activeActionModifier, activeActionDef);
                timeSinceLastAction -= activeActionDef.repeatInterval;
            }
        }

        // Second, perform all the Actions in the queue
        while (!actionQueue.isEmpty()) {
            action = actionQueue.poll();
            action.execute(delta);
        }

        // Finally, update the state of all the players
        for (Player player : players) {
            player.update(delta);
        }
    }

    /**
     * Draws the Players for debug purposes using a PlayerRenderer
     * @param renderer PlayerRenderer to use to draw.
     */
    public void drawDebug(PlayerRenderer renderer) {
        if (activePlayer != null) {
            renderer.updateEnvironmentData("Stamina", activePlayer.getStamina());
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin();

            renderer.drawPlayerControlHelp(activePlayer, curActionDefList);
            renderer.end();
            renderer.drawEnvironmentData(activePlayer);
            renderer.drawPlayerState(activePlayer);
        }
    }

    /**
     * Returns the World position of the active Player.
     * @return Position of the Player, or null if no active Player.
     */
    public Vector2 getActivePosition() {
        if (activePlayer == null) {
            return null;
        } else {
            return activePlayer.body.getPosition();
        }
    }

    /**
     * Set the bounds of the map, beyond which Player s must respawn.
     * @param mapMinX Smallest X-value within bounds on the map.
     * @param mapMinY Largest X-value within bounds on the map.
     * @param mapMaxX Smallest Y-value within bounds on the map.
     * @param mapMaxY Largest Y-value within bounds on the map.
     */
    public void setBounds(float mapMinX, float mapMinY, float mapMaxX, float mapMaxY) {
        bounds = new Rectangle(mapMinX, mapMinY, mapMaxX - mapMinX, mapMaxY - mapMinY);
    }

    /**
     * Updates the ActionDef list for the active Player and ActionModifer combination.
     */
    protected void updateActionDefList() {
        if (activePlayer == null) {
            // Create empty array to use as placeholder
            curActionDefList = new Array<ActionDef>();
            return;
        } else {
            // Load the ActionDef list for the current PlayerState/ActionModifier combination
            curActionDefList = actionDefLoader.getActionDefList(
                    activeActionModifier,
                    activePlayer.state);
        }
    }

    /**
     * Add an Action to the queue with the current screen position and active Player.
     * @param modifier ActionModifier of the action to add.
     * @param actionDef ActionDef of the action to add.
     */
    protected void addActionToQueue(ActionModifier modifier, ActionDef actionDef) {
        Action action = new Action(
                activePlayer,
                modifier,
                actionDef,
                curScreenPos.nor()
        );
        actionQueue.add(action);
    }

    /**
     * Check if any Player s have gone out of bounds, and if they have then restore
     * them to the start position.
     */
    protected void checkBounds() {
        if (bounds != null) {
            for (Player player : players) {
                Vector2 pos = player.body.getPosition();
                if (!bounds.contains(pos)) {
                    // Reset the player and put them back in the start position
                    player.body.setAngularVelocity(0.0f);
                    player.body.setLinearVelocity(0.0f, 0.0f);
                    player.body.setTransform(startPos, 0.0f);
                }
            }
        }
    }


    @Override
    public void dispose() {
        for (Player player : this.players) {
            if (player.circleShape != null) {
                player.circleShape.dispose();
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixB) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(PlayerState.FOOT_ACTIVE);
                    if (player == activePlayer) {
                        updateActionDefList();
                    }
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(PlayerState.FOOT_ACTIVE);
                    if (player == activePlayer) {
                        updateActionDefList();
                    }
                    return;
                }
            }
        } // else do nothing
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixB) {
                    player.contactBodies.remove(fixA.getBody());
                    if (player.contactBodies.isEmpty()) {
                        player.setState(PlayerState.AIR_ACTIVE);
                        if (player == activePlayer) {
                            updateActionDefList();
                        }
                    }
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.remove(fixB.getBody());
                    if (player.contactBodies.isEmpty()) {
                        player.setState(PlayerState.AIR_ACTIVE);
                        if (player == activePlayer) {
                            updateActionDefList();
                        }
                    }
                    return;
                }
            }
        }
    }
}
