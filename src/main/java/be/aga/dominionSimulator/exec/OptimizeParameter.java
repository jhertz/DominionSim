package be.aga.dominionSimulator.experiments;

import be.aga.dominionSimulator.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static be.aga.dominionSimulator.BasicCMDSimulation.get;
import static be.aga.dominionSimulator.BasicCMDSimulation.printResults;

/**
 * Optimize a value for the first deck for a parameter args[1], iterating over all values from args[2] to args[3], writing to directory args[4]
 */
public class OptimizeParameter {
    public static void main(String[] args) throws IOException {
        String parameterName = args[1];
        double parameterMinValue = Double.valueOf(args[2]);
        double parameterMaxValue = Double.valueOf(args[3]);
        File dir = new File(args[4]);
        if(!dir.exists()) {
            System.err.println("making directory " + dir);
            dir.mkdirs();
        }
        if(dir.exists() && !dir.isDirectory()) {
            System.err.println("file exists but is not directory: " + args[4]);
        }
        for(double i = parameterMinValue; i < parameterMaxValue + 1; i++) {
            DomEngine domEngine = new DomEngine(true);
            DomPlayer domPlayer = get(args[0], domEngine);
            editDeck(domPlayer, parameterName, i);
            String xml = XMLOutputter.getXML(domPlayer);
            File outputLoc = new File(dir, domPlayer.getName() + ".xml");
            PrintWriter printWriter = new PrintWriter(new FileWriter(outputLoc));
            printWriter.println(xml);
            printWriter.close();
        }
    }

    private static void editDeck(DomPlayer domPlayer, String parameterName, double i) {
        String newName = domPlayer.getName() + "_" + parameterName + "_"+ i;
        domPlayer.setName(newName);
        boolean found = false;
        for (DomBuyRule buyRule : domPlayer.getBuyRules()) {
            if(buyRule.getCardToBuy().name().equals(parameterName)) {
                if(found) {
                    throw new IllegalArgumentException("can't iterate over rule which is in there twice");
                }
                found = true;
                if(buyRule.getBuyConditions().size() > 1) {
                    throw new IllegalArgumentException("doesn't work if there are multiple conditions");
                }
                DomBuyCondition buyCondition = buyRule.getBuyConditions().get(0);
                double oldValue = buyCondition.getRightValue();
                buyCondition.setRightValue(i);
                System.err.println("changing parameter to " + i + " from " + oldValue);
            }
        }
    }


}
