package root.Persona.Rollen.Nebenrollen;

import root.Persona.Nebenrolle;
import root.Persona.Rollen.Constants.NebenrollenType.NebenrollenType;
import root.Persona.Rollen.Constants.NebenrollenType.Passiv;
import root.ResourceManagement.ImagePath;

public class ReineSeele extends Nebenrolle {
    public static final String name = "Reine Seele";
    public static final String imagePath = ImagePath.REINE_SEELE_KARTE;
    public static boolean unique = false;
    public static boolean spammable = false;
    public boolean dayInvincibility = true;
    public NebenrollenType type = new Passiv();

    @Override
    public String getName() {
        return name;
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
}