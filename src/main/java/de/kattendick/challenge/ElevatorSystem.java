package de.kattendick.challenge;

import de.kattendick.challenge.model.DirectionState;
import de.kattendick.challenge.model.Elevator;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
public class ElevatorSystem {

    private final ExecutorService service;

    private final List<Elevator> elevators;

    private final List<Future<?>> elevatorFutures = new LinkedList<>();

    public ElevatorSystem(List<Elevator> elevators) {
        this.elevators = elevators;
        this.service = Executors.newFixedThreadPool(elevators.size());

        for (Elevator elevator : elevators) {
            elevatorFutures.add(service.submit(elevator));
        }
    }

    /**
     * If a person wants to use the elevator, this method is invoked.
     *
     * @param currentFloor     the floor the person is currently at
     * @param destinationFloor the floor the person wants to travel to
     */
    public synchronized void buttonPressedAtFloor(int currentFloor, int destinationFloor) {

        Optional<Elevator> optionalElevator = findNearestElevator(currentFloor, destinationFloor);
        if (!optionalElevator.isPresent()) throw new RuntimeException("No elevator found. Should not happen!");

        Elevator elevator = optionalElevator.get();

        if (elevator.getCurrentFloor() != currentFloor) elevator.moveToFloor(currentFloor);
        elevator.moveToFloor(destinationFloor);

    }

    /**
     * Returns the nearest elevator based on distance. If there is at least one waiting elevator. Only waiting are used.
     * The calculation of the nearest elevator is based on the last floor of the current queue.
     *
     * @param currentFloor     the floor the person starts from
     * @param destinationFloor the floor the person wants to go to
     * @return the nearest elevator
     */
    public synchronized Optional<Elevator> findNearestElevator(int currentFloor, int destinationFloor) {
        DirectionState neededDirection = DirectionState.getNeededDirection(currentFloor, destinationFloor);

        boolean anyInNeededDirection = this.elevators
                .stream()
                .anyMatch(v -> v.isGoingInRightDirection(neededDirection));

        int distance = 999;
        Elevator nearest = null;
        for (Elevator elevator : this.elevators) {
            if (anyInNeededDirection && !elevator.isGoingInRightDirection(neededDirection)) continue;

            int d = Math.abs(currentFloor - elevator.getLastFloor());
            if (d >= distance) continue;

            distance = d;
            nearest = elevator;
        }
        return Optional.ofNullable(nearest);
    }

    /**
     * This shutdown disables all elevators as soon as they have reached the end of their queue.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void shutdown() throws ExecutionException, InterruptedException {
        for (Elevator elevator : this.elevators) {
            elevator.shutdown();
        }
        for (Future<?> elevatorFuture : this.elevatorFutures) {
            elevatorFuture.get();
        }
    }
}
