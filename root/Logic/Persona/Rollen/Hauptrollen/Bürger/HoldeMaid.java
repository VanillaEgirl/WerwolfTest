package root.Logic.Persona.Rollen.Hauptrollen.Bürger;

import root.Frontend.FrontendControl;
import root.Logic.Persona.Fraktion;
import root.Logic.Persona.Fraktionen.Bürger;
import root.Logic.Persona.Hauptrolle;
import root.Logic.Phases.Statement.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Logic.Spieler;
import root.Logic.Game;

public class HoldeMaid extends Hauptrolle {
    public static final String ID = "ID_Holde_Maid";
    public static final String NAME = "Holde Maid";
    public static final String IMAGE_PATH = ImagePath.HOLDE_MAID_KARTE;
    public static final Fraktion FRAKTION = new Bürger();

    public static final String STATEMENT_ID = ID;
    public static final String STATEMENT_TITLE = "Mitspieler offenbaren";
    public static final String STATEMENT_BESCHREIBUNG = "Holde Maid erwacht und offenbart sich einem Mitspieler";
    public static final StatementType STATEMENT_TYPE = StatementType.ROLLE_CHOOSE_ONE;

    public HoldeMaid() {
        this.id = ID;
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.fraktion = FRAKTION;

        this.statementID = STATEMENT_ID;
        this.statementTitle = STATEMENT_TITLE;
        this.statementBeschreibung = STATEMENT_BESCHREIBUNG;
        this.statementType = STATEMENT_TYPE;

        this.spammable = false;
    }

    @Override
    public FrontendControl getDropdownOptionsFrontendControl() {
        return Game.game.getSpielerFrontendControl(this, true);
    }

    @Override
    public void processChosenOption(String chosenOption) {
        Spieler chosenSpieler = Game.game.findSpieler(chosenOption);
        if (chosenSpieler != null) {
            besucht = chosenSpieler;
        }
    }
}
