package root.mechanics;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Persona.Fraktion;
import root.Persona.Fraktionen.Vampire;
import root.Persona.Fraktionen.Werwölfe;
import root.Persona.Rollen.Hauptrollen.Bürger.Riese;
import root.Persona.Rollen.Nebenrollen.Prostituierte;
import root.Persona.Rollen.Nebenrollen.Vampirumhang;
import root.Persona.Rollen.Nebenrollen.Wolfspelz;
import root.Spieler;

import java.util.ArrayList;

public class Opfer {
    public static Game game;

    public static ArrayList<Opfer> possibleVictims = new ArrayList<>();
    public static ArrayList<Opfer> deadVictims = new ArrayList<>();

    public Spieler opfer;
    public Spieler täter;
    public Fraktion täterFraktion;
    public boolean fraktionsTäter;

    public Opfer(Spieler opfer, Spieler täter) {
        this.opfer = opfer;
        this.täter = täter;
        this.täterFraktion = täter.hauptrolle.getFraktion();
        this.fraktionsTäter = false;
    }

    public Opfer(Spieler opfer, Fraktion fraktion) {
        this.opfer = opfer;
        this.täterFraktion = fraktion;
        this.fraktionsTäter = true;
    }

    public static ArrayList<String> getOpferOrNonStrings() {
        ArrayList<String> toteOrNon = new ArrayList<>();

        for (Opfer currentOpfer : Opfer.deadVictims) {
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

        for (Opfer currentOpfer : Opfer.deadVictims) {
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

    public static void addVictim(Spieler opfer, Spieler täter) {
        possibleVictims.add(new Opfer(opfer, täter));

        String opferNebenrolle = opfer.nebenrolle.getName();
        String täterFraktion = täter.hauptrolle.getFraktion().getName();

        if (täter.hauptrolle.getName().equals(Riese.name)) {
            addDeadVictim(opfer, täter, true);
        } else {
            if (!opfer.geschützt) {
                if (!(opferNebenrolle.equals(Vampirumhang.name) && täterFraktion.equals(Vampire.name) ||
                        opferNebenrolle.equals(Wolfspelz.name) && täterFraktion.equals(Werwölfe.name))) {
                    addDeadVictim(opfer, täter);
                }
            }
        }
    }

    public static void addVictim(Spieler opfer, Fraktion täterFraktion) {
        possibleVictims.add(new Opfer(opfer, täterFraktion));

        String opferNebenrolle = opfer.nebenrolle.getName();

        if (!opfer.geschützt || (täterFraktion.equals(Werwölfe.name) && Werwölfe.blutWolfIsAktiv())) {
            if (!(opferNebenrolle.equals(Vampirumhang.name) && täterFraktion.equals(Vampire.name) ||
                    opferNebenrolle.equals(Wolfspelz.name) && täterFraktion.equals(Werwölfe.name) && !Werwölfe.blutWolfIsAktiv()))
            {
                addDeadVictim(opfer, täterFraktion);
            }
        }
    }

    private static void addDeadVictim(Spieler opfer, Spieler täter) {
        addDeadVictim(opfer, täter, false);
    }

    private static void addDeadVictim(Spieler opfer, Spieler täter, boolean riese) {
        addDeadVictim(new Opfer(opfer, täter), riese);
    }

    private static void addDeadVictim(Spieler opfer, Fraktion täter) {
        addDeadVictim(new Opfer(opfer, täter), false);
    }

    private static void addDeadVictim(Opfer opfer, boolean riese) {
        Spieler prostituierteSpieler = game.findSpielerPerRolle(Prostituierte.name);
        String hostProstituierte = "";
        if (prostituierteSpieler != null) {
            hostProstituierte = Prostituierte.host.name;
        }

        if (riese) {
            opfer.opfer.ressurectable = false;
        }
        String opferNebenrolle = opfer.opfer.nebenrolle.getName();
        if (!opfer.opfer.equals(prostituierteSpieler) || riese) {
            deadVictims.add(opfer);
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
                    deadVictims.add(prostituierteOpferopfer);
                }
            }
        }
    }

    public static void removeVictim(Spieler opfer) {
        ArrayList<Opfer> opfersToRemove = new ArrayList<>();

        for (Opfer currentVictim : deadVictims) {
            if (currentVictim.opfer.name.equals(opfer.name)) {
                opfersToRemove.add(currentVictim);
            }
        }

        deadVictims.removeAll(opfersToRemove);
    }

    public static Opfer findOpfer(String name) {
        for (Opfer currentOpfer : deadVictims) {
            if (currentOpfer.opfer.name.equals(name)) {
                return currentOpfer;
            }
        }

        return null;
    }

    public static boolean isOpfer(String name) {
        for (Opfer currentOpfer : deadVictims) {
            if (currentOpfer.opfer.name.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOpferPerRolle(String name) {
        for (Opfer currentOpfer : deadVictims) {
            if (currentOpfer.opfer.hauptrolle.getName().equals(name) || currentOpfer.opfer.nebenrolle.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
