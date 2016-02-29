package com.ktarrant.cloudWarfare.action;

import com.badlogic.ashley.core.Component;

/**
 * Created by ktarrant1 on 1/3/16.
 */
public class ActionModifierComponent implements Component {
    public enum ActionModifier {
        NORMAL,
        ATTACK,
        SUCK,
        ROCK,
        SPIKE;
    };
    ActionModifier actionModifer;
}
