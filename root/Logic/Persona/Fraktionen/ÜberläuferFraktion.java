package root.Logic.Persona.Fraktionen;

import root.Logic.Persona.Fraktion;
import root.Logic.Persona.Rollen.Constants.Zeigekarten.FraktionsZeigekarten.ÜberläuferZeigekarte;
import root.Logic.Persona.Rollen.Constants.Zeigekarten.Zeigekarte;
import root.ResourceManagement.ImagePath;

import java.awt.*;

public class ÜberläuferFraktion extends Fraktion {
    public static final String ID = "ID_Überläufer_Fraktion";
    public static final String NAME = "Überläufer";
    public static final String IMAGE_PATH = ImagePath.ÜBERLÄUFER_ICON;
    public static final Color COLOR = Color.white;
    public static final Zeigekarte ZEIGEKARTE = new ÜberläuferZeigekarte();

    public ÜberläuferFraktion() {
        this.id = ID;
        this.name = NAME;
        this.imagePath = IMAGE_PATH;
        this.color = COLOR;
        this.zeigekarte = ZEIGEKARTE;
    }
}
