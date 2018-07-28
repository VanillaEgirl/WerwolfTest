package root.Rollen.Nebenrollen;

import root.ResourceManagement.ResourcePath;
import root.Rollen.Fraktionen.Bürger;
import root.Rollen.Nebenrolle;
import root.Spieler;

/**
 * Created by Steve on 12.11.2017.
 */
public class ReinesLicht extends Nebenrolle
{
    public static final String name = "Reines Licht";
    public static final String imagePath = ResourcePath.REINES_LICHT_KARTE;
    public static boolean unique = true;
    public static boolean spammable = false;
    public String type = Nebenrolle.PASSIV;

    public void tauschen(Nebenrolle nebenrolle) {
        try {
            Spieler spieler = Spieler.findSpielerPerRolle(name);
            spieler.nebenrolle = nebenrolle;
        }catch (NullPointerException e) {
            System.out.println(name + " nicht gefunden");
        }
    }

    public Nebenrolle getTauschErgebnis() {
        Spieler spieler = Spieler.findSpielerPerRolle(name);

        if(spieler!=null) {
            Nebenrolle nebenrolle;

            if(spieler.hauptrolle.getFraktion().getName().equals(Bürger.name)) {
                nebenrolle = new ReineSeele();
            } else {
                nebenrolle = new SchwarzeSeele();
            }

            return nebenrolle;
        } else {
            return new ReineSeele();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public boolean isSpammable() {
        return spammable;
    }

    @Override
    public String getType() { return type; }
}
