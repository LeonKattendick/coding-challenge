package de.kattendick.challenge;

import de.kattendick.challenge.model.Elevator;

import java.util.ArrayList;
import java.util.List;

public class ElevatorSystem {

    private List<Elevator> elevators = new ArrayList<>();

    public ElevatorSystem() {
        for (int i = 0; i < 7; i++) {
            elevators.add(new Elevator(i + 1, 0));
        }
    }

    public void buttonPressedAtEntry() {
        buttonPressedAtFloor(0);
    }

    public void buttonPressedAtFloor(int pressedFloor) {

    }

    public static void main(String[] args) {
        new ElevatorSystem();
    }
}
