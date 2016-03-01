package com.ktarrant.cloudWarfare;

/**
 * Created by Kevin on 2/29/2016.
 */
public enum SystemPriority {
    // Lower priority means it gets executed first
    WORLD           (0),
    BODY            (1),
    PLAYER_STATE    (2),
    TOUCH           (3),
    ACTION          (4),
    WORLD_RENDER    (5),
    PLAYER_RENDER   (6);

    int priorityValue;

    private SystemPriority(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public int getPriorityValue() {
        return priorityValue;
    }
}
