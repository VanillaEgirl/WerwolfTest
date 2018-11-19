package root.Persona.Rollen.Bonusrollen;

import root.Frontend.FrontendControl;
import root.Frontend.Utils.DropdownOptions;
import root.Persona.Bonusrolle;
import root.Persona.Rollen.Constants.BonusrollenType.Aktiv;
import root.Persona.Rollen.Constants.BonusrollenType.BonusrollenType;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;

import java.util.ArrayList;
import java.util.List;

public class Konditor extends Bonusrolle {
    public static final String GUT = "Gut";
    public static final String SCHLECHT = "Schlecht";

    public static final String ID = "ID_Konditor";
    public static final String NAME = "Konditor";
    public static final String IMAGE_PATH = ImagePath.KONDITOR_KARTE;
    public static final BonusrollenType TYPE = new Aktiv();

    public static final String STATEMENT_ID = ID;
    public static final String STATEMENT_TITLE = "Torte";
    public static final String STATEMENT_BESCHREIBUNG = "Konditor erwacht und entscheidet sich ob es eine gute oder schlechte Torte gibt";
    public static final StatementType STATEMENT_TYPE = StatementType.ROLLE_SPECAL;

    public Konditor() {
        this.id = ID;
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.type = TYPE;

        this.statementID = STATEMENT_ID;
        this.statementTitle = STATEMENT_TITLE;
        this.statementBeschreibung = STATEMENT_BESCHREIBUNG;
        this.statementType = STATEMENT_TYPE;
    }

    @Override
    public FrontendControl getDropdownOptionsFrontendControl() {
        return getTortenOptionsFrontendControl();
    }

    public static FrontendControl getTortenOptionsFrontendControl() {
        List<String> dropdownStrings = new ArrayList<>();
        dropdownStrings.add(GUT);
        dropdownStrings.add(SCHLECHT);

        return new FrontendControl(new DropdownOptions(dropdownStrings));
    }
}
