package be.aga.dominionSimulator.exec;

import be.aga.dominionSimulator.DomEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import be.aga.dominionSimulator.DomPlayer;
import be.aga.dominionSimulator.SimulationResult;
import org.apache.log4j.Level;
import org.xml.sax.InputSource;


/**
 * Simulates a tournament between all of the bots in a directory
 */
public class Tournament {

    public static class TournamentResult {
        private final Map<DomPlayer, Map<DomPlayer, Double>> results = new HashMap<>();
        public final List<DomPlayer> players;

        public TournamentResult(List<DomPlayer> players) {
            this.players = players;
            int n = players.size();
            for (DomPlayer player : players) {
                results.put(player, new HashMap<>());
            }
        }

        public void result(DomPlayer playerA, DomPlayer playerB, int aWins, int bWins, double ties) {
            if(!results.get(playerA).containsKey(playerB)) {
              results.get(playerA).put(playerB, 0.0);
            }
            results.get(playerA).put(playerB,results.get(playerA).get(playerB) + aWins + ties);
            if(!results.get(playerB).containsKey(playerA)) {
                results.get(playerB).put(playerA, 0.0);
            }
            results.get(playerB).put(playerA,results.get(playerB).get(playerA) + bWins + ties);
        }

        public double getResult(DomPlayer playerA, DomPlayer playerB) {
            if(playerA == playerB) {
                // 0 for the diagonal
                return 0;
            }
            if(!results.containsKey(playerA)) {
                System.err.println("no results for player " + playerA.getName());
                return 0;
            }
            if(!results.get(playerA).containsKey(playerB)) {
                System.err.println("no results for player " + playerB.getName() + " against " + playerA.getName());
                return 0;
            }
            return results.get(playerA).get(playerB);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File dir = new File(args[0]);
        Integer numGames = Integer.valueOf(args[1]);
        if(!dir.exists() || !dir.isDirectory()) {
            System.err.println("invalid director " + args[0]);
            return;
        }
        DomEngine domEngine = new DomEngine(true);
        // lower the log level for mass experimentsg
        domEngine.setLevel(Level.WARN);
        List<DomPlayer> players = new ArrayList<>();
        for (File file : dir.listFiles()) {
            DomPlayer domPlayer = domEngine.loadUserBotsFromXML(new InputSource(new FileInputStream(file)));
            players.add(domPlayer);
        }
        TournamentResult results = new TournamentResult(players);
        for(int i = 0; i < players.size(); i++) {
            for(int j = i+1; j < players.size(); j++) {
                DomPlayer player1 = players.get(i);
                DomPlayer player2 = players.get(j);
                System.out.println("playing " + numGames + " against " + player1.getName() + " against " + player2.getName());
                List<DomPlayer> thePlayers = new ArrayList<>();
                thePlayers.add(player1);
                thePlayers.add(player2);
                SimulationResult simulationResult = domEngine.startSimulation(thePlayers, false, numGames, false);
                results.result(player1, player2, player1.getWins(), player2.getWins(), player1.getTies());
                player1.resetPlayer();
                player2.resetPlayer();
            }
        }
        List<Map.Entry<DomPlayer, Double>> sortedResults = rank(results);
        print(sortedResults, results);
    }

    public static void print(List<Map.Entry<DomPlayer, Double>> sortedResults, TournamentResult tr) {
        int max = sortedResults.stream().mapToInt(e -> e.getKey().getName().length()).max().getAsInt();
        StringBuilder sb = new StringBuilder(pad("", max) + "\t");
        for (Map.Entry<DomPlayer, Double> entry : sortedResults) {
            String name = entry.getKey().getName();
            if(name.length() > 7) {
                name = name.substring(0, 7);
            }
            sb.append(name).append("\t");
        }
        sb.append("Total\n");
        for(int i = 0; i < sortedResults.size(); i++) {
            DomPlayer playerA = sortedResults.get(i).getKey();
            sb.append(i+1).append(": ");
            sb.append(pad(playerA.getName(), max)).append("\t");
            for(int j = 0; j < sortedResults.size(); j++) {
                DomPlayer playerB = sortedResults.get(j).getKey();
                sb.append(tr.getResult(playerA, playerB)).append("\t");
            }
            sb.append(sortedResults.get(i).getValue());
            sb.append("\n");
        }
        sb.append(pad("", max));
        for(int i = 0; i < sortedResults.size(); i++) {
          sb.append("\t").append(sortedResults.get(i).getValue());
        }
        System.out.println(sb);
    }

    private static String pad(String name, int max) {
        while(name.length() < max) {
            name += " ";
        }
        return name;
    }

    public static List<Map.Entry<DomPlayer, Double>> rank(TournamentResult tr) {
        Map<DomPlayer, Double> results = new HashMap<>();
        for(int i = 0; i < tr.players.size(); i++) {
            double score = 0.0;
            for (int j = 0; j < tr.players.size(); j++) {
                score += tr.getResult(tr.players.get(i), tr.players.get(j));
            }
            results.put(tr.players.get(i), score);
        }
        List<Map.Entry<DomPlayer, Double>> entries = new ArrayList<>(results.entrySet());
        Comparator<Map.Entry<DomPlayer, Double>> comparator = (e1,e2) -> Double.compare(e2.getValue(), e1.getValue());
        Collections.sort(entries, comparator);
        for(int i = 0; i < entries.size(); i++) {
            System.err.println("place: " + (i+1) + " " + entries.get(i).getKey().getName() + " with " + entries.get(i).getValue() + " points");
        }
        return entries;
    }
}
