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

    /**
     * The passed floor is added to the queue of floors the elevator is traveling to.
     *
     * @param destinationFloor floor that the elevator needs to pass
     */
    public void moveToFloor(int destinationFloor) {
        if (this.destinationFloors.contains(destinationFloor)) return;

        System.out.printf("[Aufzug %02d] %02d -> %02d\n", this.id, getLastFloor(), destinationFloor);

        this.destinationFloors.add(destinationFloor);
        if (this.directionState == DirectionState.STILL) changeDirection(destinationFloor);
    }

    /**
     * Checks if the elevator is going in the desired direction or if it is waiting.
     *
     * @param neededDirection direction that wants to be checked
     * @return if the elevator is moving correctly or is waiting
     */
    public boolean isGoingInRightDirection(DirectionState neededDirection) {
        return this.directionState == DirectionState.STILL || this.directionState == neededDirection;
    }

    public int getLastFloor() {
        Integer lastFloor = this.destinationFloors.peekLast();
        if (lastFloor == null) lastFloor = this.currentFloor;

        return lastFloor;
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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (this.directionState == DirectionState.STILL || this.destinationFloors.size() == 0) {
                if (this.shutdown) break;
                continue;
            }

            while (!this.destinationFloors.isEmpty()) {
                try {
                    Thread.sleep(100);
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
