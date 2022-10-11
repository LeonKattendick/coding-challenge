package de.kattendick.challenge;

import de.kattendick.challenge.util.Elevator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorTests {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private final ExecutorService service = Executors.newFixedThreadPool(10);

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(this.outputStreamCaptor));
    }

    @Test
    public void whenElevatorAt0AndPress35_ThenMoveTo35() throws InterruptedException, ExecutionException {

        Elevator elevator = new Elevator(1, 0);
        Future<?> future = service.submit(() -> {
            elevator.moveToFloor(35);
            elevator.run();
        });
        future.get();

        assertEquals(
                "[Aufzug 01] 00 -> 35\n" +
                        "[Aufzug 01] - 35 -\n",
                outputStreamCaptor.toString());
    }

    @Test
    public void whenElevatorAt0AndPress35AndPress20_ThenMoveTo20And35() throws InterruptedException, ExecutionException {

        Elevator elevator = new Elevator(1, 0);
        Future<?> future = service.submit(() -> {
            elevator.moveToFloor(35);
            elevator.moveToFloor(20);
            elevator.run();
        });
        future.get();

        assertEquals(
                "[Aufzug 01] 00 -> 35\n" +
                        "[Aufzug 01] 00 -> 20\n" +
                        "[Aufzug 01] - 20 -\n" +
                        "[Aufzug 01] - 35 -\n",
                outputStreamCaptor.toString());
    }

    @Test
    public void whenElevatorAt35AndPress0AndPress20_ThenMoveTo20And0() throws InterruptedException, ExecutionException {

        Elevator elevator = new Elevator(1, 35);
        Future<?> future = service.submit(() -> {
            elevator.moveToFloor(0);
            elevator.moveToFloor(20);
            elevator.run();
        });
        future.get();

        assertEquals(
                "[Aufzug 01] 35 -> 00\n" +
                        "[Aufzug 01] 35 -> 20\n" +
                        "[Aufzug 01] - 20 -\n" +
                        "[Aufzug 01] - 00 -\n",
                outputStreamCaptor.toString());
    }
}
