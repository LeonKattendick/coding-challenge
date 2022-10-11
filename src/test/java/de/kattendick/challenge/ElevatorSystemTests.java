package de.kattendick.challenge;

import de.kattendick.challenge.model.Elevator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorSystemTests {

    @Test
    public void whenGivenTwoElevators_ThenFindNearest() {
        Elevator elevator1 = new Elevator(1, 55);
        Elevator elevator2 = new Elevator(2, 0);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1, elevator2));

        assertEquals(1, elevatorSystem.findNearestElevator(40 ,0).get().getId());
    }

    @Test
    public void whenAtEntryLevel_ThenMoveToIBM() throws InterruptedException {
        Elevator elevator1 = new Elevator(1, 0);
        Elevator elevator2 = new Elevator(2, 20);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1, elevator2));
        elevatorSystem.buttonPressedAtFloor(0, 35);

        Thread.sleep(2000);
        assertEquals(35, elevatorSystem.getElevators().get(0).getCurrentFloor());
    }

    @Test
    public void whenAllGoingUp_ThenMoveFromIBMToEntry() throws InterruptedException {
        Elevator elevator1 = new Elevator(1, 0);

        ElevatorSystem elevatorSystem = new ElevatorSystem(Arrays.asList(elevator1));

        elevatorSystem.buttonPressedAtFloor(0, 55);
        elevatorSystem.buttonPressedAtFloor(0, 55);
        elevatorSystem.buttonPressedAtFloor(35, 0);
        elevatorSystem.shutdown();

        assertEquals(35, elevatorSystem.getElevators().get(0).getCurrentFloor());
    }
}
