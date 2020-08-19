package be.aga.dominionSimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationResult {

    private final List<Double> scores;

    public SimulationResult(List<Double> scores) {
        this.scores = scores;
    }

    public List<Double> getScores() {
        return Collections.unmodifiableList(scores);
    }

}
