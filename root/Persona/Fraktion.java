package root.Persona;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Persona.Fraktionen.SchattenpriesterFraktion;
import root.Persona.Fraktionen.Werwölfe;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.BürgerZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.Zeigekarte;
import root.Persona.Rollen.Hauptrollen.Werwölfe.Chemiker;
import root.Phases.NormalNight;
import root.Spieler;
import root.mechanics.KillLogik.Opfer;

import java.util.ArrayList;
import java.util.List;

public class Fraktion extends Persona {
    public Zeigekarte zeigekarte = new BürgerZeigekarte();

    public static List<Spieler> getFraktionsMembers(String fraktion) {
        List<Spieler> fraktionsMembers = new ArrayList<>();

        for (Spieler currentSpieler : game.spieler) {
            if (currentSpieler.lebend && currentSpieler.hauptrolle.fraktion.equals(fraktion)) {
                fraktionsMembers.add(currentSpieler);
            }
        }

        return fraktionsMembers;
    }

    public int getNumberOfFraktionsMembersInGame() {
        int numberOfFraktionsMembersInGame = 0;

        for (Hauptrolle currentHautprolle : game.hauptrollenInGame) {
            if (currentHautprolle.fraktion.equals(this)) {
                numberOfFraktionsMembersInGame++;
            }
        }

        return numberOfFraktionsMembersInGame;
    }

    public static List<String> getFraktionsMemberStrings(String fraktion) {
        List<String> fraktionsMembers = new ArrayList<>();

        for (Spieler currentSpieler : game.spieler) {
            if (currentSpieler.lebend && currentSpieler.hauptrolle.fraktion.equals(fraktion)) {
                fraktionsMembers.add(currentSpieler.name);
            }
        }

        return fraktionsMembers;
    }

    public FrontendControl getFraktionsMemberOrNonFrontendControl(Hauptrolle rolle) {
        FrontendControl frontendControl = new FrontendControl();

        frontendControl.typeOfContent = FrontendControlType.DROPDOWN_LIST;
        frontendControl.dropdownStrings = getFraktionsMemberStrings(rolle.fraktion.name);
        frontendControl.dropdownStrings.add("");
        if (!rolle.spammable && rolle.besuchtLastNight != null) {
            frontendControl.dropdownStrings.remove(rolle.besuchtLastNight.name);
        }

        return frontendControl;
    }

    public static boolean fraktionContainedInNight(String fraktion) {
        if (getFraktionStrings().contains(fraktion)) {
            return !fraktionOffenkundigTot(fraktion);
        } else {
            return false;
        }
    }

    public static boolean fraktionLebend(String fraktion) {
        for (Spieler currentSpieler : game.spieler) {
            if (currentSpieler.hauptrolle.fraktion.equals(fraktion) && currentSpieler.lebend) {
                return true;
            }
        }

        return false;
    }

    public static boolean fraktionOffenkundigTot(String fraktion) {
        if (fraktion.equals(SchattenpriesterFraktion.NAME)) {
            return false;
        }
        if (fraktion.equals(Werwölfe.NAME) && game.getHauptrolleInGameNames().contains(Chemiker.NAME)) {
            return false;
        }

        int numberHauptrollenInGame = 0;
        for (Hauptrolle hauptrolle : game.hauptrollenInGame) {
            if (hauptrolle.fraktion.equals(fraktion)) {
                numberHauptrollenInGame++;
            }
        }

        int numberMitteHauptrollen = 0;
        for (Hauptrolle mitteHauptrolle : game.mitteHauptrollen) {
            if (mitteHauptrolle.fraktion.equals(fraktion)) {
                numberMitteHauptrollen++;
            }
        }

        return numberMitteHauptrollen >= numberHauptrollenInGame;
    }

    public static boolean fraktionOpfer(String fraktion) {
        for (Spieler currentSpieler : game.spieler) {
            String fraktionSpieler = currentSpieler.hauptrolle.fraktion.name;

            if (fraktion.equals(Werwölfe.NAME)) {
                if (fraktionSpieler.equals(fraktion) && currentSpieler.hauptrolle.killing) {
                    if (currentSpieler.lebend && !Opfer.isOpfer(currentSpieler.name)) {
                        return false;
                    }
                }
            } else {
                if (fraktionSpieler.equals(fraktion)) {
                    if (currentSpieler.lebend && !Opfer.isOpfer(currentSpieler.name)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean fraktionAktiv(String fraktion) {
        List<Spieler> livingSpieler = game.getLivingSpieler();

        for (Opfer opfer : NormalNight.opfer) {
            livingSpieler.remove(opfer.spieler);
        }

        for (Spieler currentSpieler : livingSpieler) {
            String fraktionSpieler = currentSpieler.hauptrolle.fraktion.name;

            if (fraktion.equals(Werwölfe.NAME)) {
                if (fraktionSpieler.equals(fraktion) && currentSpieler.hauptrolle.killing) {
                    if (currentSpieler.aktiv) {
                        return true;
                    }
                }
            } else {
                if (fraktionSpieler.equals(fraktion)) {
                    if (currentSpieler.aktiv) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static List<Fraktion> getLivingFraktionen() {
        List<Fraktion> allFraktionen = new ArrayList<>();

        for (String currentFraktion : getLivingFraktionStrings()) {
            allFraktionen.add(Fraktion.findFraktion(currentFraktion));
        }

        return allFraktionen;
    }

    public static List<String> getLivingFraktionStrings() {
        List<String> allFraktionen = new ArrayList<>();

        for (Spieler currentSpieler : game.spieler) {
            if (currentSpieler.lebend) {
                String currentSpielerFraktion = currentSpieler.hauptrolle.fraktion.name;

                if (!allFraktionen.contains(currentSpielerFraktion)) {
                    allFraktionen.add(currentSpielerFraktion);
                }
            }
        }

        return allFraktionen;
    }

    public static List<String> getFraktionStrings() {
        List<String> allFraktionen = new ArrayList<>();

        for (Hauptrolle hauptrolle : game.hauptrollenInGame) {
            String currentFratkion = hauptrolle.fraktion.name;
            if (!allFraktionen.contains(currentFratkion)) {
                allFraktionen.add(currentFratkion);
            }
        }

        return allFraktionen;
    }

    public static List<String> getLivingFraktionOrNoneStrings() {
        List<String> allFraktionen = getLivingFraktionStrings();
        allFraktionen.add("");

        return allFraktionen;
    }

    public static List<String> getFraktionOrNoneStrings() {
        List<String> allFraktionen = getFraktionStrings();
        allFraktionen.add("");

        return allFraktionen;
    }

    public static FrontendControl getLivigFraktionOrNoneFrontendControl() {
        FrontendControl frontendControl = new FrontendControl();

        frontendControl.typeOfContent = FrontendControlType.DROPDOWN_LIST;
        frontendControl.dropdownStrings = getLivingFraktionOrNoneStrings();

        return frontendControl;
    }

    public static FrontendControl getFraktionOrNoneFrontendControl() {
        FrontendControl frontendControl = new FrontendControl();

        frontendControl.typeOfContent = FrontendControlType.DROPDOWN_LIST;
        frontendControl.dropdownStrings = getFraktionOrNoneStrings();

        return frontendControl;
    }

    public static Fraktion findFraktion(String searchedFraktion) {
        for (Hauptrolle currentHautprolle : game.hauptrollen) {
            if (currentHautprolle.fraktion.equals(searchedFraktion)) {
                return currentHautprolle.fraktion;
            }
        }

        return null;
    }
}
