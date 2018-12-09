package root.Logic.Persona;

import root.Frontend.FrontendControl;
import root.Logic.Game;
import root.Logic.KillLogic.Opfer;
import root.Logic.Persona.Rollen.Hauptrollen.Bürger.Irrlicht;
import root.Logic.Persona.Rollen.Hauptrollen.Bürger.Sammler;
import root.Logic.Phases.Statement.Constants.StatementState;
import root.Logic.Spieler;

import java.util.List;
import java.util.stream.Collectors;

public class Rolle extends Persona {
    public Spieler besucht;
    public Spieler besuchtLastNight;
    public int abilityCharges = 1;

    public boolean spammable = false;
    public boolean selfuseable = true;
    public int numberOfPossibleInstances = 1;

    public static List<String> getMitteHauptrollenNames() {
        return Game.game.mitteHauptrollen.stream()
                .map(rolle -> rolle.name)
                .collect(Collectors.toList());
    }

    public static Rolle findRolle(String rolleID) { //TODO move to Game
        Rolle wantedRolle;

        wantedRolle = Game.game.findHauptrolle(rolleID);
        if (wantedRolle != null)
            return wantedRolle;

        wantedRolle = Game.game.findBonusrolle(rolleID);
        if (wantedRolle != null)
            return wantedRolle;

        return null;
    }

    public static Rolle findRollePerName(String wantedName) { //TODO move to Game
        Rolle wantedRolle;

        wantedRolle = Game.game.findHauptrollePerName(wantedName);
        if (wantedRolle != null)
            return wantedRolle;

        wantedRolle = Game.game.findBonusrollePerName(wantedName);
        if (wantedRolle != null)
            return wantedRolle;

        return null;
    }

    public static int numberOfOccurencesOfRolleInGame(Rolle rolle) {
        Hauptrolle wantedHauptrolle = Game.game.findHauptrolle(rolle.id);
        if (wantedHauptrolle != null)
            return Game.game.numberOfOccurencesOfHauptrolleInGame(wantedHauptrolle);

        Bonusrolle wantedBonusrolle = Game.game.findBonusrolle(rolle.id);
        if (wantedBonusrolle != null)
            return Game.game.numberOfOccurencesOfBonusrolleInGame(wantedBonusrolle);

        return 0;
    }

    public static boolean hauptRolleContainedInNight(String rolleID) {
        if (Game.game.getHauptrolleInGameIDs().contains(rolleID)) {
            for (Rolle currentRolle : Game.game.mitteHauptrollen) {
                if (currentRolle.equals(rolleID)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean rolleContainedInNight(String rolleID) {
        if (Game.game.getHauptrolleInGameIDs().contains(rolleID) || Game.game.getBonusrolleInGameIDs().contains(rolleID)) {
            if (rolleLebend(rolleID)) {
                return true;
            }

            if (hauptRolleContainedInNight(Sammler.ID) && Sammler.isSammlerRolle(rolleID)) {
                return true;
            }

            for (Rolle currentRolle : Game.game.mitteHauptrollen) {
                if (currentRolle.equals(rolleID)) {
                    return false;
                }
            }

            for (Rolle currentRolle : Game.game.mitteBonusrollen) {
                if (currentRolle.equals(rolleID)) {
                    return false;
                }
            }

            return fraktionContainedInNight(rolleID);
        } else {
            return false;
        }
    }

    private static boolean fraktionContainedInNight(String rolleID) {
        Rolle rolle = Rolle.findRolle(rolleID);
        if (rolle instanceof Hauptrolle) {
            Hauptrolle hauptrolle = (Hauptrolle) rolle;

            return Fraktion.fraktionContainedInNight(hauptrolle.fraktion.id);
        } else {
            return true;
        }
    }

    public static boolean rolleLebend(String rolleID) {
        for (Spieler currentSpieler : Game.game.spieler) {
            if (currentSpieler.hauptrolle.equals(rolleID) && currentSpieler.lebend) {
                return true;
            }
            if (currentSpieler.bonusrolle.equals(rolleID) && currentSpieler.lebend) {
                return true;
            }
        }

        return false;
    }

    public static boolean rolleAktiv(String rolleID) {
        if (Irrlicht.ID.equals(rolleID)) { //TODO find better solution
            return true;
        }

        for (Spieler currentSpieler : Game.game.spieler) {
            if (currentSpieler.hauptrolle.equals(rolleID) && currentSpieler.aktiv) {
                return true;
            }
            if (currentSpieler.bonusrolle.equals(rolleID) && currentSpieler.aktiv) {
                return true;
            }
        }

        return false;
    }

    public static StatementState getState(Rolle rolle) {
        Spieler spieler;

        if (!rolleContainedInNight(rolle.id)) {
            return StatementState.INVISIBLE_NOT_IN_GAME;
        }

        if (Irrlicht.ID.equals(rolle.id)) {
            return StatementState.NORMAL;
        }

        spieler = Game.game.findSpielerPerRolle(rolle.id);
        if (spieler == null) {
            return StatementState.NOT_IN_GAME;
        }

        if (!spieler.lebend) {
            return StatementState.NOT_IN_GAME;
        }
        if (Opfer.isOpfer(spieler)) {
            return StatementState.DEAD;
        }
        if (!spieler.aktiv) {
            return StatementState.DEAKTIV;
        }
        if (rolle.abilityCharges <= 0) {
            return StatementState.AUFGEBRAUCHT;
        }
        return StatementState.NORMAL;
    }

    public FrontendControl processChosenOptionsGetInfo(String chosenOption1, String chosenOption2) {
        return new FrontendControl();
    }
}
