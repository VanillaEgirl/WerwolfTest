package root.Rollen.Hauptrollen.Bürger;

import root.Frontend.Constants.FrontendControlType;
import root.Frontend.FrontendControl;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Rollen.Fraktion;
import root.Rollen.Fraktionen.Bürger;
import root.Rollen.Hauptrolle;
import root.Rollen.Nebenrollen.Tarnumhang;
import root.Spieler;

public class Späher extends Hauptrolle
{
    private static final String TÖTEND_TITLE = "Tötend";
    private static final String NICHT_TÖTEND_TITLE = "Nicht Tötend";
    private static final String TARNUMHANG_TITLE = "Tarnumhang";

    public static String title = "Spieler wählen";
    public static final String beschreibung = "Späher erwacht und lässt sich Auskunft über einen Mitspieler geben";
    public static StatementType statementType = StatementType.ROLLE_CHOOSE_ONE_INFO;

    public static final String name = "Späher";
    public static Fraktion fraktion = new Bürger();
    public static final String imagePath = ImagePath.SPÄHER_KARTE;
    public static boolean spammable = true;

    @Override
    public FrontendControl getDropdownOptions() {
        return game.getMitspielerCheckSpammableFrontendControl(this);
    }

    @Override
    public FrontendControl processChosenOptionGetInfo(String chosenOption) {
        Spieler chosenPlayer = game.findSpieler(chosenOption);

        if(chosenPlayer != null) {
            besucht = chosenPlayer;

            if(chosenPlayer.nebenrolle.getName().equals(Tarnumhang.name)) {
                return new FrontendControl(FrontendControlType.IMAGE, TARNUMHANG_TITLE, ImagePath.TARNUMHANG);
            }

            if(chosenPlayer.hauptrolle.isKilling()) {
                abilityCharges--;

                return new FrontendControl(FrontendControlType.IMAGE, TÖTEND_TITLE, ImagePath.TÖTEND);
            } else {
                return new FrontendControl(FrontendControlType.IMAGE, NICHT_TÖTEND_TITLE, ImagePath.NICHT_TÖTEND);
            }
        }

        return new FrontendControl();
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
    public Fraktion getFraktion() {
        return fraktion;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public boolean isSpammable() {
        return spammable;
    }
}
