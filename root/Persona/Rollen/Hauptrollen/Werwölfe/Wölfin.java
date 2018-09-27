package root.Persona.Rollen.Hauptrollen.Werwölfe;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Persona.Fraktion;
import root.Persona.Fraktionen.Werwölfe;
import root.Persona.Hauptrolle;
import root.Persona.Rollen.Constants.WölfinState;
import root.Persona.Rollen.Nebenrollen.Tarnumhang;
import root.Phases.Nacht;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Spieler;
import root.mechanics.Opfer;

public class Wölfin extends Hauptrolle {
    public static final String STATEMENT_TITLE = "Opfer wählen";
    public static final String STATEMENT_BESCHREIBUNG = "Wölfin erwacht und wählt ein Opfer aus, wenn sie das tut, erfährt das Dorf ihre Bonusrolle";
    public static final StatementType STATEMENT_TYPE = StatementType.ROLLE_CHOOSE_ONE;

    public static final String SECOND_STATEMENT_TITLE = "Wölfin";
    public static final String WÖLFIN_NEBENROLLE = "Das Dorf erfährt die Bonusrolle der Wölfin";
    public static final String SECOND_STATEMENT_BESCHREIBUNG = WÖLFIN_NEBENROLLE;
    public static final StatementType SECOND_STATEMENT_TYPE = StatementType.ROLLE_INFO;

    public static final String NAME = "Wölfin";
    public static final String IMAGE_PATH = ImagePath.WÖLFIN_KARTE;
    public static final Fraktion FRAKTION = new Werwölfe();
    public static WölfinState state = WölfinState.WARTEND;

    public Wölfin() {
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.fraktion = FRAKTION;

        this.statementTitle = STATEMENT_TITLE;
        this.statementBeschreibung = STATEMENT_BESCHREIBUNG;
        this.statementType = STATEMENT_TYPE;

        this.secondStatementTitle = SECOND_STATEMENT_TITLE;
        this.secondStatementBeschreibung = SECOND_STATEMENT_BESCHREIBUNG;
        this.secondStatementType = SECOND_STATEMENT_TYPE;

        this.killing = true;
    }

    @Override
    public FrontendControl getDropdownOptions() {
        return game.getPlayerCheckSpammableFrontendControl(this);
    }

    @Override
    public void processChosenOption(String chosenOption) {
        Spieler chosenPlayer = game.findSpieler(chosenOption);
        state = WölfinState.FERTIG;
        if (chosenPlayer != null) {
            besucht = chosenPlayer;

            Spieler täter = game.findSpielerPerRolle(NAME);
            Opfer.addVictim(chosenPlayer, täter);
        }
    }

    @Override
    public FrontendControl getInfo() {
        if (Nacht.wölfinKilled) {
            Spieler wölfinSpieler = Nacht.wölfinSpieler;
            if (wölfinSpieler != null) {
                String imagePath = wölfinSpieler.bonusrolle.imagePath;
                if (wölfinSpieler.bonusrolle.name.equals(Tarnumhang.NAME)) {
                    imagePath = ImagePath.TARNUMHANG;
                }
                return new FrontendControl(FrontendControlType.IMAGE, imagePath);
            }
        }

        return new FrontendControl();
    }
}