package de.kattendick.challenge.util;

import lombok.Data;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Elevator implements Runnable {

    @NonNull
    private int id;

    @NonNull
    private int currentFloor;

    private Set<Integer> destinationFloors = new TreeSet<>();

    private DirectionState directionState = DirectionState.STILL;

    public void moveToFloor(int destinationFloor) {
        if (this.destinationFloors.contains(destinationFloor)) return;

        this.destinationFloors.add(destinationFloor);
        this.directionState = destinationFloor > this.currentFloor ? DirectionState.UP : DirectionState.DOWN;

        System.out.printf("[Aufzug %02d] %02d -> %02d\n", this.id, this.currentFloor, destinationFloor);
    }

    @Override
    public void run() {
        if (this.directionState == DirectionState.STILL || this.destinationFloors.size() == 0) return;

        while (this.destinationFloors.size() != 0) {
            try {
                Thread.sleep(100); // Takes 1/10s to move to different floor
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.currentFloor += this.directionState.getOffset();

            Optional<Integer> reachedFloor = this.destinationFloors.stream().filter(v -> v == this.currentFloor).findAny();
            if (!reachedFloor.isPresent()) continue;

            this.destinationFloors.remove(reachedFloor.get());
            System.out.printf("[Aufzug %02d] - %02d -\n", this.id, this.currentFloor);
        }
        this.directionState = DirectionState.STILL;
    }
}
