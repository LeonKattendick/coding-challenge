package de.kattendick.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TravelRequest {

    private int currentFloor;

    private int destinationFloor;

}
