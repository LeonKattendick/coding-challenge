package de.kattendick.challenge;

import de.kattendick.challenge.model.Elevator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorTests {

    private final ExecutorService service = Executors.newFixedThreadPool(10);

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(this.outputStreamCaptor));
    }

    @SneakyThrows
    @Test
    public void whenElevatorAt0AndPress35_ThenMoveTo35() {

        Elevator elevator = new Elevator(1, 0);

        elevator.moveToFloor(35);

        Future<?> future = service.submit(elevator);
        elevator.shutdown();
        future.get();

        assertEquals(
                "[Aufzug 01] 00 -> 35\n" +
                        "[Aufzug 01] - 35 -\n",
                outputStreamCaptor.toString()
        );
    }

    @SneakyThrows
    @Test
    public void whenElevatorAt0AndPress20AndPress35_ThenMoveTo20And35() {

        Elevator elevator = new Elevator(1, 0);

        elevator.moveToFloor(20);
        elevator.moveToFloor(35);

        Future<?> future = service.submit(elevator);
        elevator.shutdown();
        future.get();

        assertEquals(
                "[Aufzug 01] 00 -> 20\n" +
                        "[Aufzug 01] 20 -> 35\n" +
                        "[Aufzug 01] - 20 -\n" +
                        "[Aufzug 01] - 35 -\n",
                outputStreamCaptor.toString()
        );
    }

    @SneakyThrows
    @Test
    public void whenElevatorAt0AndPress35AndPress20_ThenMoveTo35And20() {

        Elevator elevator = new Elevator(1, 0);

        elevator.moveToFloor(35);
        elevator.moveToFloor(20);

        Future<?> future = service.submit(elevator);
        elevator.shutdown();
        future.get();

        assertEquals(
                "[Aufzug 01] 00 -> 35\n" +
                        "[Aufzug 01] 35 -> 20\n" +
                        "[Aufzug 01] - 35 -\n" +
                        "[Aufzug 01] - 20 -\n",
                outputStreamCaptor.toString()
        );
    }

    @SneakyThrows
    @Test
    public void whenElevatorAt35AndPress0AndPress55_ThenMoveTo0And55() {

        Elevator elevator = new Elevator(1, 35);

        elevator.moveToFloor(0);
        elevator.moveToFloor(55);

        Future<?> future = service.submit(elevator);
        elevator.shutdown();
        future.get();

        assertEquals(
                "[Aufzug 01] 35 -> 00\n" +
                        "[Aufzug 01] 00 -> 55\n" +
                        "[Aufzug 01] - 00 -\n" +
                        "[Aufzug 01] - 55 -\n",
                outputStreamCaptor.toString()
        );
    }

    @SneakyThrows
    @Test
    public void whenElevatorAt35AndPressDouble0_ThenMoveOnlyOnce() {

        Elevator elevator = new Elevator(1, 35);

        elevator.moveToFloor(0);
        elevator.moveToFloor(0);

        Future<?> future = service.submit(elevator);
        elevator.shutdown();
        future.get();

        assertEquals(
                "[Aufzug 01] 35 -> 00\n" +
                        "[Aufzug 01] - 00 -\n",
                outputStreamCaptor.toString()
        );
    }
}
