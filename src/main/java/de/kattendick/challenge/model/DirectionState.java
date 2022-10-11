package de.kattendick.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DirectionState {

    STILL(0), UP(1), DOWN(-1);

    private final int offset;

    public static DirectionState getNeededDirection(int currentFloor, int destinationFloor) {
        return destinationFloor > currentFloor ? DirectionState.UP : DirectionState.DOWN;
    }
}
