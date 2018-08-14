package root.mechanics;

import root.ResourceManagement.ResourcePath;
import root.Spieler;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Steve on 23.11.2017.
 */
public class Liebespaar {
    Game game;

    public static final String imagePath = ResourcePath.LIEBESPAAR;
    public static final String ZUFÄLLIG = "Zufällig";

    public Spieler spieler1;
    public Spieler spieler2;

    public Liebespaar(String spieler1Name, String spieler2Name, Game game)
    {
        this.game = game;
        ArrayList<Spieler> spieler = (ArrayList<Spieler>)game.spieler.clone();

        if(spieler1Name.equals(ZUFÄLLIG)) {
            if(!spieler2Name.equals(ZUFÄLLIG)) {
                spieler2 = game.findSpieler(spieler2Name);
                spieler.remove(spieler2);
            }
            spieler1 = generateRandomSpieler(spieler);
        } else {
            spieler1 = game.findSpieler(spieler1Name);
        }

        if(spieler2Name.equals(ZUFÄLLIG)) {
            spieler.remove(spieler1);
            spieler2 = generateRandomSpieler(spieler);
        } else {
            spieler2 = game.findSpieler(spieler2Name);
        }
    }

    public static Spieler generateRandomSpieler(ArrayList<Spieler> spieler){
        int randomNum = ThreadLocalRandom.current().nextInt(0, spieler.size());

        return spieler.get(randomNum);
    }

    public Spieler getPlayerToDie() {
        if(spieler1!=null && spieler2!=null) {
            if (spieler1.lebend && !spieler2.lebend) {
                return spieler1;
            }
            if (!spieler1.lebend && spieler2.lebend) {
                return spieler2;
            }
        }

        return null;
    }

    public static String getImagePath() {
        return imagePath;
    }
}
