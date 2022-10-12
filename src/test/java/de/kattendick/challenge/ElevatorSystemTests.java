package de.kattendick.challenge;

import de.kattendick.challenge.model.Elevator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorSystemTests {

    @Test
    public void whenGivenTwoElevators_ThenFindNearest() {

        Elevator elevator1 = new Elevator(1, 55);
        Elevator elevator2 = new Elevator(2, 0);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1, elevator2));

        assertEquals(1, elevatorSystem.findNearestElevator(40).get().getId());
    }

    @SneakyThrows
    @Test
    public void whenAtEntryLevel_ThenMoveToIBM() {

        Elevator elevator1 = new Elevator(1, 0);
        Elevator elevator2 = new Elevator(2, 20);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1, elevator2));
        elevatorSystem.buttonPressedAtFloor(0, 35);
        elevatorSystem.shutdown();

        assertEquals(35, elevatorSystem.getElevators().get(0).getCurrentFloor());
    }

    @SneakyThrows
    @Test
    public void whenAllGoingUp_ThenMoveFromIBMToEntry() {

        Elevator elevator1 = new Elevator(1, 0);
        Elevator elevator2 = new Elevator(2, 20);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1, elevator2));

        elevatorSystem.buttonPressedAtFloor(0, 55);
        elevatorSystem.buttonPressedAtFloor(0, 55);
        elevatorSystem.buttonPressedAtFloor(35, 0);
        elevatorSystem.shutdown();

        assertEquals(0, elevatorSystem.getElevators().get(0).getCurrentFloor());
        assertEquals(55, elevatorSystem.getElevators().get(1).getCurrentFloor());
    }
}
