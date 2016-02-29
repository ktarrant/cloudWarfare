package com.ktarrant.cloudWarfare.action;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionSystem  { // extends IteratingSystem {
//    private ComponentMapper<PlayerComponent> playerMap;
//    private ComponentMapper<ActionComponent> actionMap;
//    private ComponentMapper<ActionModifierComponent> modifierMap;
//
//    public ActionSystem() {
//        super(Family.getFor(ActionComponent.class, ActionModifierComponent.class));
//    }
//
//    @Override
//    protected void processEntity(Entity entity, float deltaTime) {
//
//    }
//    public final PlayerComponent player;
//    public final ActionModifierComponent modifier;
//    public final ActionComponent actionComponent;
//    public final Vector2 direction;

//    public ActionSystem(PlayerComponent player,
//                        ActionModifierComponent modifier,
//                        ActionComponent actionComponent,
//                        Vector2 direction) {
//        this.player = player;
//        this.modifier = modifier;
//        this.actionComponent = actionComponent;
//        this.direction = direction;
//    }
//
//    public void execute(float delta) {
//        float staminaAfter =
//                player.stamina * this.actionComponent.depleteMultiplier - this.actionComponent.depleteConstant;
//        if (staminaAfter < 0.0f) {
//            // Not enough stamina
//            return;
//        }
//        direction.scl(actionComponent.linearImpulseMultiplier);
//        direction.add(actionComponent.linearImpulseConstant);
//        direction.scl(player.stamina);
//        player.body.applyLinearImpulse(direction, Vector2.Zero, true);
//
//        player.stamina = staminaAfter;
//    }
}
