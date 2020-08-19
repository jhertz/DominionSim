package be.aga.dominionSimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Initial wrapper for returning the results of the simulation from the {@link be.aga.dominionSimulator.DomEngine}.
 *
 * Will become more fleshed out as the simulation logic gets more complicated.
 */
public class SimulationResult {

    // [0] is the first player score
    // [1] is the second player score
    // [0] + [1] <= 1.0 (and the difference is the ties)
    private final List<Double> scores;

    public SimulationResult(List<Double> scores) {
        this.scores = scores;
    }

    public List<Double> getScores() {
        return Collections.unmodifiableList(scores);
    }

}
