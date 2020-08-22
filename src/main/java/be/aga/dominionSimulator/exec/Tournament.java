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
        final double[][] results;
        public final List<DomPlayer> players;

        public TournamentResult(List<DomPlayer> players) {
            this.players = players;
            int n = players.size();
            results = new double[n][];
            for(int i = 0; i < n; i++) {
                results[i] = new double[n];
            }
        }

        public void result(int playerA, int playerB, int aWins, int bWins, double ties) {
            results[playerA][playerB] = aWins + ties;
            results[playerB][playerA] = bWins + ties;
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
                results.result(i, j, player1.getWins(), player2.getWins(), player1.getTies());
                player1.resetPlayer();
                player2.resetPlayer();
            }
        }
        print(results);
        rank(results);
    }

    public static void print(TournamentResult tr) {
        StringBuilder sb = new StringBuilder("\t");
        for(int i = 0; i < tr.players.size(); i++) {
          sb.append(tr.players.get(i).getName()).append("\t");
        }
        sb.append("\n");
        for(int i = 0; i < tr.players.size(); i++) {
            sb.append(tr.players.get(i).getName()).append("\t");
            for(int j = 0; j < tr.players.size(); j++) {
                sb.append(tr.results[i][j]).append("\t");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public static void rank(TournamentResult tr) {
        Map<String, Double> results = new HashMap<>();
        for(int i = 0; i < tr.players.size(); i++) {
            double score = 0.0;
            for (int j = 0; j < tr.results[i].length; j++) {
                score += tr.results[i][j];
            }
            results.put(tr.players.get(i).getName(), score);
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(results.entrySet());
        Comparator<Map.Entry<String, Double>> comparator = (e1,e2) -> Double.compare(e1.getValue(), e2.getValue());
        Collections.sort(entries, comparator);
        for(int i = entries.size() -1; i >= 0; i--) {
            int place = entries.size() - i;
            System.err.println("place: " + place + " " + entries.get(i).getKey() + " with " + entries.get(i).getValue() + " points");
        }

    }
}
