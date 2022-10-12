# coding-challenge
This code implements the basic functionality of an elevator system.

# Used dependencies
- Lombok
- JUnit

# Features
- A complex sequence of moves can be entered, which an elevator will follow.
- Each elevator can run on a single thread.
- The elevator will print out the current state of all inputs.
- An infinite amount of elevators can be setup in parallel.
- For new inputs waiting elevators are preferred. If none is available the closest elevator is tasked.

# Restrictions
- An elevator will move strictly on the given route and doesn't stop in between.
- The queuing of new moves is not quite "fair" when all elevators are in use, as the system only checks the last floor and not the time it takes to get there.
