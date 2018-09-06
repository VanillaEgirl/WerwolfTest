package root.Persona.Rollen.Nebenrollen;

import root.Persona.Fraktionen.Bürger;
import root.Persona.Nebenrolle;
import root.Persona.Rollen.Constants.NebenrollenType.Informativ;
import root.Persona.Rollen.Constants.NebenrollenType.NebenrollenType;
import root.Persona.Rollen.Hauptrollen.Bürger.Schamanin;
import root.Phases.NightBuilding.Constants.StatementType;
import root.ResourceManagement.ImagePath;
import root.Spieler;
import root.mechanics.Liebespaar;

import java.util.Objects;

/**
 * Created by Steve on 12.11.2017.
 */
public class Analytiker extends Nebenrolle {
    public static String title = "Spieler wählen";
    public static final String beschreibung = "Analytiker erwacht und wählt zwei Spieler, der Erzähler sagt ihm ob diese in derselben Fraktion sind";
    public static StatementType statementType = StatementType.ROLLE_SPECAL;

    public static final String name = "Analytiker";
    public static final String imagePath = ImagePath.ANALYTIKER_KARTE;
    public static boolean spammable = true;
    public NebenrollenType type = new Informativ();
    public static final String GLEICH = "gleich";
    public static final String UNGLEICH = "ungleich";
    public Spieler besuchtAnalysieren = null;

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
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public boolean isSpammable() {
        return spammable;
    }

    @Override
    public NebenrollenType getType() {
        return type;
    }

    public boolean showTarnumhang(Spieler spieler1, Spieler spieler2) {
        return tarnumhangIsUnterDenSpielern(spieler1, spieler2) ||
                (schamaninIsUnterDenSpielern(spieler1, spieler2) && analytikerIsNotBürger());
    }

    private boolean tarnumhangIsUnterDenSpielern(Spieler spieler1, Spieler spieler2) {
        return spieler1.nebenrolle.equals(Tarnumhang.name) || spieler2.nebenrolle.equals(Tarnumhang.name);
    }

    private boolean schamaninIsUnterDenSpielern(Spieler spieler1, Spieler spieler2) {
        return spieler1.hauptrolle.equals(Schamanin.name) || spieler2.hauptrolle.equals(Schamanin.name);
    }

    private boolean analytikerIsNotBürger() {
        Spieler analytikerSpieler = game.findSpielerPerRolle(name);

        return !analytikerSpieler.hauptrolle.getFraktion().equals(Bürger.name);
    }

    public String analysiere(Spieler spieler1, Spieler spieler2) {
        besucht = spieler1;
        besuchtAnalysieren = spieler2;
        String name1 = spieler1.name;
        String name2 = spieler2.name;

        String fraktion1 = spieler1.hauptrolle.getFraktion().getName();
        String fraktion2 = spieler2.hauptrolle.getFraktion().getName();

        if (Objects.equals(fraktion1, fraktion2)) {
            return GLEICH;
        } else {
            Liebespaar liebespaar = game.liebespaar;
            if (liebespaar != null && liebespaar.spieler1 != null) {
                String liebespartner1 = liebespaar.spieler1.name;
                String liebespartner2 = liebespaar.spieler2.name;

                if (Objects.equals(name1, liebespartner1) && Objects.equals(name2, liebespartner2) ||
                        Objects.equals(name2, liebespartner1) && Objects.equals(name1, liebespartner2)) {
                    return GLEICH;
                } else {
                    return UNGLEICH;
                }
            } else {
                return UNGLEICH;
            }
        }
    }
}