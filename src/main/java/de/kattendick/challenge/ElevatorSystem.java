package de.kattendick.challenge;

import de.kattendick.challenge.model.DirectionState;
import de.kattendick.challenge.model.Elevator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
public class ElevatorSystem {

    private final ExecutorService service = Executors.newFixedThreadPool(10);

    private List<Elevator> elevators = new ArrayList<>();

    public ElevatorSystem(List<Elevator> elevators) {
        this.elevators = elevators;
        for (Elevator elevator : elevators) {
            service.execute(elevator);
        }
    }

    public ElevatorSystem(int amount) {
        for (int i = 0; i < amount; i++) {
            this.elevators.add(new Elevator(i + 1, 0));
        }
        for (Elevator elevator : elevators) {
            service.submit(elevator);
        }
    }

    public void buttonPressedForIBM() {
        buttonPressedAtFloor(0, 35);
    }

    public void buttonPressedAtFloor(int currentFloor, int destinationFloor) {

        Optional<Elevator> optionalElevator = findNearestElevator(currentFloor, destinationFloor);
        if (!optionalElevator.isPresent()) throw new RuntimeException("No elevator found. Should not happen!");

        Elevator elevator = optionalElevator.get();

        if (elevator.getCurrentFloor() != currentFloor) elevator.moveToFloor(currentFloor);
        elevator.moveToFloor(destinationFloor);

    }

    public Optional<Elevator> findNearestElevator(int currentFloor, int destinationFloor) {
        DirectionState neededDirection = DirectionState.getNeededDirection(currentFloor, destinationFloor);

        boolean anyInNeededDirection = elevators
                .stream()
                .anyMatch(v -> v.isGoingInRightDirection(neededDirection));

        int distance = 999;
        Elevator nearest = null;
        for (Elevator elevator : elevators) {
            // we first check if any elevator is going in the needed direction -> if so we can ignore all unwanted
            if (anyInNeededDirection && !elevator.isGoingInRightDirection(neededDirection)) continue;

            int d = Math.abs(currentFloor - elevator.getCurrentFloor());
            if (d >= distance) continue;

            distance = d;
            nearest = elevator;
        }
        return Optional.ofNullable(nearest);
    }

    public void shutdown() {
        for (Elevator elevator : elevators) {
            elevator.shutdown();
        }
    }
}
