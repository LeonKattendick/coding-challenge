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
        if (getLastFloor() == destinationFloor) return;

        System.out.printf("[Aufzug %02d] %02d -> %02d\n", this.id, getLastFloor(), destinationFloor);

        this.destinationFloors.add(destinationFloor);
        if (this.directionState == DirectionState.STILL) changeDirection(destinationFloor);
    }

    /**
     * Returns the last floor the elevator will halt after the current queue. If the elevator is waiting the current floor is returned.
     *
     * @return the last floor
     */
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
                Thread.sleep(10); // every 10ms the elevator checks for new tasks
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (this.directionState == DirectionState.STILL || this.destinationFloors.size() == 0) {
                if (this.shutdown) break;
                continue;
            }

            while (!this.destinationFloors.isEmpty()) {
                try {
                    Thread.sleep(100); // it takes 100ms for the elevator to pass one floor
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
