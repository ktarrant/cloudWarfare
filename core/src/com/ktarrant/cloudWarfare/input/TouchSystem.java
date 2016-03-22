package com.ktarrant.cloudWarfare.input;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ktarrant.cloudWarfare.SystemPriority;
import com.ktarrant.cloudWarfare.action.ActionComponent;
import com.ktarrant.cloudWarfare.action.ActionDefLoader;
import com.ktarrant.cloudWarfare.action.ActionModifierComponent;
import com.ktarrant.cloudWarfare.player.body.PlayerComponent;
import com.ktarrant.cloudWarfare.world.CameraComponent;

import java.util.HashMap;

/**
 * Created by Kevin on 2/29/2016.
 */
public class TouchSystem extends IteratingSystem implements InputProcessor {
    private ComponentMapper<PlayerComponent> playerMapper =
            ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<CameraComponent> cameraMapper =
            ComponentMapper.getFor(CameraComponent.class);
    private ComponentMapper<ActionModifierComponent> actionModMapper =
            ComponentMapper.getFor(ActionModifierComponent.class);

    private Entity activePlayer;
    private HashMap<Integer, Boolean> keyMap;
    private ActionModifierComponent currentModifier;
    private Vector2 currentCursorVector;

    public TouchSystem() {
        super(Family.all(
                PlayerComponent.class).get(),
        SystemPriority.TOUCH.getPriorityValue());

        currentModifier = ActionModifierComponent.NORMAL;
        currentCursorVector = null;
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
            PlayerComponent playerComp = playerMapper.get(entity);
            CameraComponent cameraComp = cameraMapper.get(playerComp.worldEntity);
            Camera camera = cameraComp.camera;
            camera.position.set(playerComp.rootBody.getPosition(), 0);
            camera.update();
        }
    }

    private void updateKeys() {
        ActionModifierComponent rv = ActionModifierComponent.NORMAL;
        if (keyMap.containsKey(Input.Keys.A)) {
            if (rv == ActionModifierComponent.NORMAL && keyMap.get(Input.Keys.A)) {
                rv = ActionModifierComponent.ATTACK;
            }
        }
        currentModifier = rv;
    }

    public Entity getActivePlayer() {
        return this.activePlayer;
    }

    public void setActivePlayer(Entity activePlayer) {
        this.activePlayer = activePlayer;
    }

    private void updateCursor(Vector3 cursorPos) {
        if (activePlayer == null) {
            return;
        }

        if (cursorPos == null) {
            currentCursorVector = null;
            return;
        }

        PlayerComponent playerComp = playerMapper.get(activePlayer);
        Camera camera = cameraMapper.get(playerComp.worldEntity).camera;

        // Compute the angles and vectors of the tap relative to the character
        Vector3 worldCoor = camera.unproject(cursorPos);
        Vector2 playerPos = playerComp.rootBody.getPosition();
        currentCursorVector = new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y);
    }

    private void makeCurrentAction(boolean touchdown) {
        if (activePlayer == null) {
            return;
        }

        // Create the TouchComponent
        TouchComponent touchComp = new TouchComponent();
        touchComp.playerTouchVector.set(currentCursorVector);
        touchComp.touchedDown = touchdown;

        // Create the ActionComponent
        PlayerComponent playerComp = playerMapper.get(activePlayer);
        ActionComponent actionComp = ActionDefLoader.getActionComponent(
                currentModifier,
                playerComp.state);

        // Create the Action entity
        Entity currentAction= new Entity();
        currentAction.add(playerComp);
        currentAction.add(actionComp);
        currentAction.add(currentModifier);
        currentAction.add(touchComp);
        this.getEngine().addEntity(currentAction);
    }

    @Override
    public boolean keyDown(int keycode) {
        keyMap.put(keycode, true);
        updateKeys();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyMap.put(keycode, false);
        updateKeys();
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCursor(new Vector3(screenX, screenY, 0.0f));
        makeCurrentAction(true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCursor(null);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        updateCursor(new Vector3(screenX, screenY, 0.0f));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
