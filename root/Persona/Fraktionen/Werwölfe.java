package root.Persona.Fraktionen;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Persona.Fraktion;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.WerwölfeZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.Zeigekarte;
import root.Persona.Rollen.Hauptrollen.Werwölfe.*;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Spieler;
import root.mechanics.Opfer;

import java.awt.*;
import java.util.ArrayList;

public class Werwölfe extends Fraktion {
    public static String title = "Opfer wählen";
    public static final String beschreibung = "Die Werwölfe erwachen und die Wölfe wählen ein Opfer aus";
    public static StatementType statementType = StatementType.FRAKTION_CHOOSE_ONE;
    public static final String name = "Werwölfe";
    public static final Color farbe = Color.green;
    public static final String imagePath = ImagePath.WÖLFE_ICON; //sollte es das noch geben?
    public static final Zeigekarte zeigekarte = new WerwölfeZeigekarte();

    @Override
    public void processChosenOption(String chosenOption) {
        Spieler chosenPlayer = game.findSpieler(chosenOption);
        if (chosenPlayer != null) {
            Opfer.addVictim(chosenPlayer, this);
        }
    }

    @Override
    public FrontendControl getDropdownOptions() {
        FrontendControl frontendControl = new FrontendControl();

        frontendControl.typeOfContent = FrontendControlType.DROPDOWN_LIST_IMAGE;
        frontendControl.strings = game.getLivingPlayerOrNoneStrings();
        frontendControl.imagePath = zeigekarte.imagePath;

        return frontendControl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getBeschreibung() {
        return beschreibung;
    }

    @Override
    public StatementType getStatementType() {
        return statementType;
    }

    @Override
    public Color getFarbe() {
        return farbe;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public Zeigekarte getZeigeKarte() {
        return zeigekarte;
    }

    public static boolean isTötend(String hauptrolle) {
        ArrayList<String> tötend = new ArrayList<>();

        tötend.add(Alphawolf.name);
        tötend.add(Blutwolf.name);
        tötend.add(Geisterwolf.name);
        tötend.add(Schreckenswolf.name);
        tötend.add(Werwolf.name);
        tötend.add(Wölfin.name);

        return tötend.contains(hauptrolle);
    }
}
