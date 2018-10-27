package root.Persona.Rollen.Hauptrollen.Bürger;

import root.Frontend.FrontendControl;
import root.Persona.Bonusrolle;
import root.Persona.Fraktion;
import root.Persona.Fraktionen.Bürger;
import root.Persona.Hauptrolle;
import root.Persona.Rollen.Bonusrollen.*;
import root.Persona.Rollen.Constants.BonusrollenType.Tarnumhang_BonusrollenType;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.BürgerZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.SchattenpriesterZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.VampiereZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.WerwölfeZeigekarte;
import root.Persona.Rollen.Constants.Zeigekarten.Zeigekarte;
import root.Persona.Rollen.Hauptrollen.Werwölfe.Wolfsmensch;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Spieler;

public class Seherin extends Hauptrolle {
    public static final String STATEMENT_ID = "Seherin";
    public static final String STATEMENT_TITLE = "Spieler wählen";
    public static final String STATEMENT_BESCHREIBUNG = "Seherin erwacht und lässt sich Auskunft über die Fraktion eines Mitspielers geben";
    public static final StatementType STATEMENT_TYPE = StatementType.ROLLE_CHOOSE_ONE_INFO;

    public static final String NAME = "Seherin";
    public static final String IMAGE_PATH = ImagePath.SEHERIN_KARTE;
    public static final Fraktion FRAKTION = new Bürger();

    public Seherin() {
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.fraktion = FRAKTION;

        this.statementID = STATEMENT_ID;
        this.statementTitle = STATEMENT_TITLE;
        this.statementBeschreibung = STATEMENT_BESCHREIBUNG;
        this.statementType = STATEMENT_TYPE;

        this.spammable = true;
    }

    @Override
    public FrontendControl getDropdownOptionsFrontendControl() {
        return game.getMitspielerCheckSpammableFrontendControl(this);
    }

    @Override
    public FrontendControl processChosenOptionGetInfo(String chosenOption) {
        Spieler chosenSpieler = game.findSpieler(chosenOption);

        if (chosenSpieler != null) {
            besucht = chosenSpieler;
            Zeigekarte zeigekarte = chosenSpieler.hauptrolle.fraktion.getZeigeKarte();

            Bonusrolle bonusrolle = chosenSpieler.bonusrolle;
            Hauptrolle hauptrolle = chosenSpieler.hauptrolle;
            if (bonusrolle.equals(Lamm.NAME) || hauptrolle.equals(Wolfsmensch.NAME)) {
                zeigekarte = new BürgerZeigekarte();
            }
            if (bonusrolle.equals(Wolfspelz.NAME)) {
                zeigekarte = new WerwölfeZeigekarte();
            }
            if (bonusrolle.equals(Vampirumhang.NAME)) {
                zeigekarte = new VampiereZeigekarte();
            }
            if (bonusrolle.equals(Schattenkutte.NAME)) {
                zeigekarte = new SchattenpriesterZeigekarte();
            }
            if (bonusrolle.equals(Tarnumhang.NAME)) {
                zeigekarte = new Tarnumhang_BonusrollenType();
            }

            return new FrontendControl(zeigekarte);
        }

        return new FrontendControl();
    }
}
