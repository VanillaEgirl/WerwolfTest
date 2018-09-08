package root.Persona;

import root.Persona.Fraktionen.Bürger;
import root.Persona.Rollen.Constants.NebenrollenType.Aktiv;
import root.Persona.Rollen.Constants.NebenrollenType.NebenrollenType;
import root.Persona.Rollen.Hauptrollen.Bürger.Schamanin;
import root.Persona.Rollen.Nebenrollen.Schatten;
import root.Persona.Rollen.Nebenrollen.Tarnumhang;
import root.Spieler;

import java.awt.*;

public class Nebenrolle extends Rolle {
    public static Nebenrolle defaultNebenrolle = new Schatten();
    public static Color defaultFarbe = new Color(240, 240, 240);

    public void tauschen(Nebenrolle nebenrolle) {
        try {
            Spieler spieler = game.findSpielerPerRolle(this.getName());
            if (spieler != null) {
                spieler.nebenrolle = nebenrolle;
            }
        } catch (NullPointerException e) {
            System.out.println(this.getName() + " nicht gefunden");
        }
    }

    public Nebenrolle getTauschErgebnis() {
        try {
            Spieler spieler = game.findSpielerPerRolle(this.getName());
            if (spieler != null) {
                return spieler.nebenrolle;
            } else {
                return this;
            }
        } catch (NullPointerException e) {
            System.out.println(this.getName() + " nicht gefunden");
        }

        return this;
    }

    public NebenrollenType getType() {
        return new Aktiv();
    }

    public boolean showTarnumhang(Nebenrolle requester, Spieler spieler) {
        return spieler != null && (spieler.nebenrolle.equals(Tarnumhang.name) || (playerIsSchamanin(spieler) && thisRolleIsNotBuerger(requester)));
    }

    private boolean playerIsSchamanin(Spieler player) {
        return player.hauptrolle.equals(Schamanin.name);
    }

    private boolean thisRolleIsNotBuerger(Nebenrolle requester) {
        Spieler spieler = game.findSpielerPerRolle(requester.getName());

        return !spieler.hauptrolle.getFraktion().equals(new Bürger());
    }
}
