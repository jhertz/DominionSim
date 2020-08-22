package be.aga.dominionSimulator.exec;

import be.aga.dominionSimulator.DomEngine;
import be.aga.dominionSimulator.DomPlayer;
import be.aga.dominionSimulator.SimulationResult;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic way to run simulations at the cmdline
 * arg 0: number of simulations
 * arg 1: first player
 * arg 2: second plays
 *
 * FIXME: if you give it two of the same player, I think it might break
 *
 * mvn exec:java -Dmy.mainClass="seanahan.BasicCMDSimulation" -Dexec.args="10000 Magpie \"Big Money Ultimate\""
 */
public class BasicCMDSimulation {
    private static final Logger LOGGER = Logger.getLogger( DomEngine.class );

    public static void main(String[] args) {
        DomEngine domEngine = new DomEngine(true);
        List<DomPlayer> thePlayers = new ArrayList();
        Integer numGames = Integer.valueOf(args[0]);
        thePlayers.add(get(args[1], domEngine));
        thePlayers.add(get(args[2], domEngine));
        for (DomPlayer dp : thePlayers) {
            printDeck(dp);
        }
        SimulationResult simulationResult = domEngine.startSimulation(thePlayers, false, numGames, false);
        printResults(simulationResult);
    }

    public static void printResults(SimulationResult simulationResult) {
        int i = 0;
        double ties = 1.0;
        for (Double score : simulationResult.getScores()) {
            ties -= score;
            System.err.println("player: " + ++i + ": " + score*100 + "%");
        }
        System.err.println("ties: " + ties*100 + "%");
    }

    public static DomPlayer get(String arg, DomEngine myEngine) {
        for (DomPlayer player : myEngine.getBotArray()) {
            if (player.getName().equals(arg)) {
                return player;
            }
        }
        LOGGER.log(Priority.INFO, "bot " + arg + " not found");
        return null;
    }

    private static void printDeck(DomPlayer dp) {
        System.out.println(dp.name);
        int i = 0;
        for (DomBuyRule buyRule : dp.getBuyRules()) {
            System.out.println( "\t" + (i++) + " " + buyRule.getCardToBuy().name());
            for (DomBuyCondition theCondition : buyRule.getBuyConditions()) {
                DomBuyCondition.FunctionTypeWithValue lfunctionTypeWithValue = DomBuyCondition.getFunctionTypeWithValue(theCondition.getLeftFunction(),
                        theCondition.getLeftValue(), theCondition.getLeftCardType(), theCondition.getLeftCardName());
                String ln = theCondition.getLeftFunction().name();
                String la = lfunctionTypeWithValue.value.toString();
                String o = theCondition.getComparator().name();
                String rn = theCondition.getRightFunction().name();
                DomBuyCondition.FunctionTypeWithValue rfunctionTypeWithValue = DomBuyCondition.getFunctionTypeWithValue(theCondition.getRightFunction(),
                        theCondition.getRightValue(), theCondition.getRightCardType(), theCondition.getRightCardName());
                String ra = rfunctionTypeWithValue.value.toString();
                System.out.println("\t\t" + ln + " " + la + " " + o + " " + rn + " " + ra);
            }

        }
    }

}
