package de.kattendick.challenge.util;

import lombok.Data;
import lombok.NonNull;

import java.util.*;

@Data
public class Elevator implements Runnable {

    @NonNull
    private int id;

    @NonNull
    private int currentFloor;

    private Queue<Integer> destinationFloors = new PriorityQueue<>();

    private DirectionState directionState = DirectionState.STILL;

    public void moveToFloor(int destinationFloor) {
        if (this.destinationFloors.contains(destinationFloor)) return;

        this.destinationFloors.add(destinationFloor);
        if (this.directionState == DirectionState.STILL) this.directionState = destinationFloor > this.currentFloor ? DirectionState.UP : DirectionState.DOWN;

        System.out.printf("[Aufzug %02d] %02d -> %02d\n", this.id, this.currentFloor, destinationFloor);
    }

    @Override
    public void run() {
        if (this.directionState == DirectionState.STILL || this.destinationFloors.size() == 0) return;

        while (this.destinationFloors.size() != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.currentFloor += this.directionState.getOffset();

            Optional<Integer> reachedFloor = this.destinationFloors.stream().filter(v -> v == this.currentFloor).findAny();
            if (!reachedFloor.isPresent()) continue;

            this.destinationFloors.remove(reachedFloor.get());
            System.out.printf("[Aufzug %02d] - %02d -\n", this.id, this.currentFloor);

            Optional<Integer> nextFloor = this.destinationFloors.stream().findFirst();
            if (nextFloor.isPresent()) this.directionState = nextFloor.get() > this.currentFloor ? DirectionState.UP : DirectionState.DOWN;

        }
        this.directionState = DirectionState.STILL;
    }
}
