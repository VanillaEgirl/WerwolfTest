package root.Persona.Rollen.Nebenrollen;

import root.Persona.Fraktionen.Bürger;
import root.Persona.Nebenrolle;
import root.Persona.Rollen.Constants.NebenrollenType.NebenrollenType;
import root.Persona.Rollen.Constants.NebenrollenType.Passiv;
import root.ResourceManagement.ImagePath;
import root.Spieler;

public class Seelenlicht extends Nebenrolle {
    public static final String name = "Seelenlicht";
    public static final String imagePath = ImagePath.SEELENLICHT_KARTE;
    public static boolean spammable = false;
    public NebenrollenType type = new Passiv();

    public void tauschen(Nebenrolle nebenrolle) {
        try {
            Spieler spieler = game.findSpielerPerRolle(name);
            spieler.nebenrolle = nebenrolle;
        } catch (NullPointerException e) {
            System.out.println(name + " nicht gefunden");
        }
    }

    public Nebenrolle getTauschErgebnis() {
        Spieler spieler = game.findSpielerPerRolle(name);

        if (spieler != null) {
            Nebenrolle nebenrolle;

            if (spieler.hauptrolle.getFraktion().getName().equals(Bürger.name)) {
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
    public boolean isSpammable() {
        return spammable;
    }

    @Override
    public NebenrollenType getType() {
        return type;
    }
}
