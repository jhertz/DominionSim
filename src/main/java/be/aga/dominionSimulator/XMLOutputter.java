package be.aga.dominionSimulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility for writing bots to XML, factored out of DomEngine
 */
public class XMLOutputter {

    /**
     * Factored out of DomEngine, prints the user bots
     * @param bots
     * @return
     */
    public static String getXML(Collection<DomPlayer> bots, Predicate<DomPlayer> isPrintable) {
        String newline = System.getProperty("line.separator");
        StringBuilder theXML = new StringBuilder();
        theXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(newline);
        theXML.append("<playerCollection>").append(newline);
        for (DomPlayer thePlayer : bots) {
            if(isPrintable.test(thePlayer)) {
                theXML.append(thePlayer.getXML()).append(newline);
            }
        }
        theXML.append("</playerCollection>");
        return theXML.toString();
    }

    /**
     * Get XML for a single bot
     * @param bot - the bot to print
     * @return - the XML string of a collection of bots with a single entry
     */
    public static String getXML(DomPlayer bot) {
       List<DomPlayer> list = new ArrayList<>();
       list.add(bot);
       return getXML(list, (b) -> true);
    }

}
