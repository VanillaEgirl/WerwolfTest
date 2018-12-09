package root;

import root.Frontend.Frame.ErzählerFrame;
import root.Frontend.Frame.SpielerFrame;
import root.Frontend.Frame.ÜbersichtsFrame;
import root.Frontend.FrontendControl;
import root.Frontend.Page.Page;
import root.Frontend.Utils.PageRefresher.Models.LoadMode;
import root.Logic.Game;
import root.Logic.Phases.PhaseManager;
import root.Logic.Phases.PhaseMode;
import root.ResourceManagement.DataManager;
import root.Utils.ListHelper;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class GameController {
    private static DataManager dataManager;

    public static ErzählerFrame erzählerFrame;
    public static SpielerFrame spielerFrame;
    public static ÜbersichtsFrame übersichtsFrame;

    static void startProgram() {
        erzählerFrame = new ErzählerFrame();
        dataManager = new DataManager();
    }

    public static void setupGame(LoadMode loadMode) {
        new Game();
        erzählerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        spielerFrame = new SpielerFrame(erzählerFrame);

        if (loadMode == LoadMode.COMPOSITION) {
            dataManager.loadLastComposition();
        } else if (loadMode == LoadMode.GAME) {
            dataManager.loadLastGame();
            Game.game.spielerSpecified = ListHelper.cloneList(Game.game.spieler);
        }

        erzählerFrame.generateAllPageRefreshers();
    }

    public static void startGame() {
        übersichtsFrame = new ÜbersichtsFrame(erzählerFrame.frameJpanel.getHeight() + ÜbersichtsFrame.spaceFromErzählerFrame);
        erzählerFrame.toFront();
        Game.game.startGame(erzählerFrame, spielerFrame, übersichtsFrame);
        writeGame();
    }

    public static void writeComposition() {
        //TODO thread composition writing
        dataManager.writeComposition();
    }

    public static void writeGame() {
        //TODO thread composition writing
        dataManager.writeGame();
    }

    public static void respawnFrames() {
        spielerFrame.dispatchEvent(new WindowEvent(spielerFrame, WindowEvent.WINDOW_CLOSING));
        übersichtsFrame.dispatchEvent(new WindowEvent(übersichtsFrame, WindowEvent.WINDOW_CLOSING));

        int spielerFrameMode = spielerFrame.mode;
        Page savePage = spielerFrame.currentPage;
        spielerFrame = new SpielerFrame(erzählerFrame);
        spielerFrame.mode = spielerFrameMode;
        spielerFrame.buildScreenFromPage(savePage);

        übersichtsFrame = new ÜbersichtsFrame(erzählerFrame.frameJpanel.getHeight() + ÜbersichtsFrame.spaceFromErzählerFrame);

        FrontendControl.spielerFrame = spielerFrame;
        if (PhaseManager.phaseMode == PhaseMode.DAY || PhaseManager.phaseMode == PhaseMode.FREIBIER_DAY) {
            spielerFrame.generateDayPage();
        } else if (PhaseManager.phaseMode == PhaseMode.NORMAL_NIGHT || PhaseManager.phaseMode == PhaseMode.SETUP_NIGHT) {
            spielerFrame.buildScreenFromPage(savePage);
        }
    }
}
