package de.kattendick.challenge.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DirectionState {

    STILL(0), UP(1), DOWN(-1);

    private final int offset;

}
