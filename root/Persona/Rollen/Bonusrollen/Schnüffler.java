package root.Persona.Rollen.Bonusrollen;

import root.Frontend.FrontendControl;
import root.Frontend.Utils.DropdownOptions;
import root.Persona.Bonusrolle;
import root.Persona.Rollen.Constants.BonusrollenType.BonusrollenType;
import root.Persona.Rollen.Constants.BonusrollenType.Informativ;
import root.Persona.Rollen.Constants.DropdownConstants;
import root.Persona.Rollen.Constants.SchnüfflerInformation;
import root.Persona.Rollen.SchnüfflerInformationGenerator;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Spieler;
import root.mechanics.Game;

import java.util.ArrayList;
import java.util.List;

public class Schnüffler extends Bonusrolle {
    public static final String ID = "ID_Schnüffler";
    public static final String NAME = "Schnüffler";
    public static final String IMAGE_PATH = ImagePath.SCHNÜFFLER_KARTE;
    public static final BonusrollenType TYPE = new Informativ();

    public static final String STATEMENT_ID = ID;
    public static final String STATEMENT_TITLE = "Spieler wählen";
    public static final String STATEMENT_BESCHREIBUNG = "Schnüffler erwacht und lässt sich Auskunft über einen Mitspieler geben";
    public static final StatementType STATEMENT_TYPE = StatementType.ROLLE_CHOOSE_ONE_INFO;

    public static int MAX_ANZAHL_AN_INFORMATIONEN = 4;

    public List<SchnüfflerInformation> informationen = new ArrayList<>(); //TODO wenn dieb schnüffler nimmt dann neu anlegen

    public Schnüffler() {
        this.id = ID;
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.type = TYPE;

        this.statementID = STATEMENT_ID;
        this.statementTitle = STATEMENT_TITLE;
        this.statementBeschreibung = STATEMENT_BESCHREIBUNG;
        this.statementType = STATEMENT_TYPE;

        this.spammable = false;
    }

    @Override
    public FrontendControl getDropdownOptionsFrontendControl() {
        List<String> dropdownStrings = removePreviousSpieler(Game.game.getLivingSpielerOrNoneStrings());

        return new FrontendControl(new DropdownOptions(dropdownStrings, DropdownConstants.EMPTY));
    }

    private List<String> removePreviousSpieler(List<String> spieler) {
        for (SchnüfflerInformation information : informationen) {
            spieler.remove(information.spielerName);
        }

        return spieler;
    }

    @Override
    public FrontendControl processChosenOptionGetInfo(String chosenOption) {
        Spieler chosenSpieler = Game.game.findSpieler(chosenOption);

        if (chosenSpieler != null) {
            besucht = chosenSpieler;

            SchnüfflerInformationGenerator informationGenerator = new SchnüfflerInformationGenerator(chosenSpieler);
            SchnüfflerInformation information = informationGenerator.generateInformation();
            informationen.add(information);

            if (informationen.size() > MAX_ANZAHL_AN_INFORMATIONEN) {
                informationen.remove(0);
            }

            String pageTitle = chosenSpieler.name;
            return new FrontendControl(informationen, pageTitle);
        }

        return new FrontendControl();
    }
}
