package de.kattendick.challenge.model;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.Optional;

@Data
public class Elevator implements Runnable {

    @NonNull
    private int id;

    @NonNull
    private volatile int currentFloor;

    private LinkedList<Integer> destinationFloors = new LinkedList<>();

    private DirectionState directionState = DirectionState.STILL;

    private boolean shutdown = false;

    public void moveToFloor(int destinationFloor) {
        if (this.destinationFloors.contains(destinationFloor)) return;

        this.destinationFloors.add(destinationFloor);
        if (this.directionState == DirectionState.STILL) changeDirection(destinationFloor);

        System.out.printf("[Aufzug %02d] %02d -> %02d\n", this.id, this.currentFloor, destinationFloor);
    }

    public boolean isGoingInRightDirection(DirectionState neededDirection) {
        return this.directionState == DirectionState.STILL || this.directionState == neededDirection;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    private void changeDirection(int nextFloor) {
        this.directionState = DirectionState.getNeededDirection(this.currentFloor, nextFloor);
    }

    @Override
    public void run() {
        while (true) {
            if (this.directionState == DirectionState.STILL || this.destinationFloors.size() == 0) {
                if (this.shutdown) break;
                continue;
            }

            while (!this.destinationFloors.isEmpty()) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (this) {
                    this.currentFloor += this.directionState.getOffset();

                    Optional<Integer> nextFloor = Optional.ofNullable(this.destinationFloors.peek());
                    if (!nextFloor.isPresent() || nextFloor.get() != this.currentFloor) continue;

                    this.destinationFloors.remove(nextFloor.get());
                    if (!this.destinationFloors.isEmpty()) changeDirection(this.destinationFloors.peek());
                }
                System.out.printf("[Aufzug %02d] - %02d -\n", this.id, this.currentFloor);
            }
            this.directionState = DirectionState.STILL;
        }
    }
}
