package root.mechanics;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Persona.Fraktion;
import root.Persona.Fraktionen.Vampire;
import root.Persona.Fraktionen.Werwölfe;
import root.Persona.Rollen.Bonusrollen.Prostituierte;
import root.Persona.Rollen.Bonusrollen.Vampirumhang;
import root.Persona.Rollen.Bonusrollen.Wolfspelz;
import root.Persona.Rollen.Hauptrollen.Bürger.Riese;
import root.Spieler;

import java.util.ArrayList;

public class Opfer {
    public static Game game;

    public static ArrayList<Opfer> possibleOpfer = new ArrayList<>();
    public static ArrayList<Opfer> deadOpfer = new ArrayList<>();

    public Spieler opfer;
    public Spieler täter;
    public Fraktion täterFraktion;
    public boolean fraktionsTäter;

    public Opfer(Spieler opfer, Spieler täter) {
        this.opfer = opfer;
        this.täter = täter;
        this.täterFraktion = täter.hauptrolle.fraktion;
        this.fraktionsTäter = false;
    }

    public Opfer(Spieler opfer, Fraktion fraktion) {
        this.opfer = opfer;
        this.täterFraktion = fraktion;
        this.fraktionsTäter = true;
    }

    public static ArrayList<String> getOpferOrNonStrings() {
        ArrayList<String> toteOrNon = new ArrayList<>();

        for (Opfer currentOpfer : Opfer.deadOpfer) {
            if (!toteOrNon.contains(currentOpfer.opfer.name))
                toteOrNon.add(currentOpfer.opfer.name);
        }

        toteOrNon.add("");

        return toteOrNon;
    }

    public static FrontendControl getOpferOrNonFrontendControl() {
        FrontendControl frontendControl = new FrontendControl();

        frontendControl.typeOfContent = FrontendControlType.DROPDOWN_LIST;
        frontendControl.dropdownStrings = getOpferOrNonStrings();

        return frontendControl;
    }

    public static ArrayList<Spieler> getErweckbareOpfer() {
        ArrayList<Spieler> erweckbareOrNon = new ArrayList<>();

        for (Opfer currentOpfer : Opfer.deadOpfer) {
            if (currentOpfer.opfer.ressurectable) {
                if (!erweckbareOrNon.contains(currentOpfer.opfer))
                    erweckbareOrNon.add(currentOpfer.opfer);
            }
        }

        return erweckbareOrNon;
    }

    public static ArrayList<String> getErweckbareStringsOrNon() {
        ArrayList<Spieler> erweckbareOpfer = getErweckbareOpfer();
        ArrayList<String> erweckbareStrings = new ArrayList<>();

        for (Spieler currentOpfer : erweckbareOpfer) {
            erweckbareStrings.add(currentOpfer.name);
        }

        erweckbareStrings.add("");

        return erweckbareStrings;
    }

    public static void addOpfer(Spieler opfer, Spieler täter) {
        possibleOpfer.add(new Opfer(opfer, täter));

        String opferBonusrolle = opfer.bonusrolle.name;
        String täterFraktion = täter.hauptrolle.fraktion.name;

        if (täter.hauptrolle.name.equals(Riese.NAME)) {
            addDeadOpfer(opfer, täter, true);
        } else {
            if (!opfer.geschützt) {
                if (!(opferBonusrolle.equals(Vampirumhang.NAME) && täterFraktion.equals(Vampire.NAME) ||
                        opferBonusrolle.equals(Wolfspelz.NAME) && täterFraktion.equals(Werwölfe.NAME))) {
                    addDeadOpfer(opfer, täter);
                }
            }
        }
    }

    public static void addOpfer(Spieler opfer, Fraktion täterFraktion) {
        possibleOpfer.add(new Opfer(opfer, täterFraktion));

        String opferBonusrolle = opfer.bonusrolle.name;

        if (täterFraktion.equals(Werwölfe.NAME) && Werwölfe.blutWolfIsAktiv()) {
            addDeadOpfer(opfer, täterFraktion);
        } else {
            if (!opfer.geschützt) {
                if (!(opferBonusrolle.equals(Vampirumhang.NAME) && täterFraktion.equals(Vampire.NAME) ||
                        opferBonusrolle.equals(Wolfspelz.NAME) && täterFraktion.equals(Werwölfe.NAME))) {
                    addDeadOpfer(opfer, täterFraktion);
                }
            }
        }
    }

    private static void addDeadOpfer(Spieler opfer, Spieler täter) {
        addDeadOpfer(opfer, täter, false);
    }

    private static void addDeadOpfer(Spieler opfer, Spieler täter, boolean riese) {
        addDeadOpfer(new Opfer(opfer, täter), riese);
    }

    private static void addDeadOpfer(Spieler opfer, Fraktion täter) {
        addDeadOpfer(new Opfer(opfer, täter), false);
    }

    private static void addDeadOpfer(Opfer opfer, boolean riese) {
        Spieler prostituierteSpieler = game.findSpielerPerRolle(Prostituierte.NAME);
        String hostProstituierte = "";
        if (prostituierteSpieler != null) {
            hostProstituierte = Prostituierte.host.name;
        }

        if (riese) {
            opfer.opfer.ressurectable = false;
        }
        if (!opfer.opfer.equals(prostituierteSpieler) || riese) {
            deadOpfer.add(opfer);
        }

        if (prostituierteSpieler != null) {
            if (opfer.opfer.name.equals(hostProstituierte)) {
                if (!prostituierteSpieler.geschützt || riese) {
                    Opfer prostituierteOpferopfer;
                    if (opfer.fraktionsTäter) {
                        prostituierteOpferopfer = new Opfer(prostituierteSpieler, opfer.täterFraktion);
                    } else {
                        prostituierteOpferopfer = new Opfer(prostituierteSpieler, opfer.täter);
                    }
                    deadOpfer.add(prostituierteOpferopfer);
                }
            }
        }
    }

    public static void removeOpfer(Spieler opfer) {
        deadOpfer.removeIf(deadOpfer -> deadOpfer.opfer.equals(opfer));
    }

    public static Opfer findOpfer(String name) {
        for (Opfer currentOpfer : deadOpfer) {
            if (currentOpfer.opfer.name.equals(name)) {
                return currentOpfer;
            }
        }

        return null;
    }

    public static boolean isOpfer(String name) {
        for (Opfer currentOpfer : deadOpfer) {
            if (currentOpfer.opfer.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOpferPerRolle(String name) {
        for (Opfer currentOpfer : deadOpfer) {
            if (currentOpfer.opfer.hauptrolle.name.equals(name) || currentOpfer.opfer.bonusrolle.name.equals(name)) {
                return true;
            }
        }

        return false;
    }
}
