package root.Logic.Phases;

import root.Controller.FrontendObject;
import root.Frontend.Utils.DropdownOptions;
import root.Logic.Game;
import root.Logic.KillLogic.Angriff;
import root.Logic.KillLogic.Opfer;
import root.Logic.KillLogic.Selbstmord;
import root.Logic.Liebespaar;
import root.Logic.Persona.Bonusrolle;
import root.Logic.Persona.Fraktion;
import root.Logic.Persona.Fraktionen.SchattenpriesterFraktion;
import root.Logic.Persona.Fraktionen.Vampire;
import root.Logic.Persona.Fraktionen.Werwölfe;
import root.Logic.Persona.Hauptrolle;
import root.Logic.Persona.Rolle;
import root.Logic.Persona.Rollen.Bonusrollen.*;
import root.Logic.Persona.Rollen.Constants.BonusrollenType.Tarnumhang_BonusrollenType;
import root.Logic.Persona.Rollen.Constants.SchnüfflerInformation;
import root.Logic.Persona.Rollen.Constants.Zeigekarten.*;
import root.Logic.Persona.Rollen.Hauptrollen.Bürger.Irrlicht;
import root.Logic.Persona.Rollen.Hauptrollen.Bürger.Wirt;
import root.Logic.Persona.Rollen.Hauptrollen.Schattenpriester.Schattenpriester;
import root.Logic.Persona.Rollen.Hauptrollen.Vampire.GrafVladimir;
import root.Logic.Persona.Rollen.Hauptrollen.Werwölfe.Blutwolf;
import root.Logic.Persona.Rollen.Hauptrollen.Überläufer.Henker;
import root.Logic.Phases.NightBuilding.NormalNightStatementBuilder;
import root.Logic.Phases.Statement.Constants.IndieStatements;
import root.Logic.Phases.Statement.Constants.ProgramStatements;
import root.Logic.Phases.Statement.Constants.StatementState;
import root.Logic.Phases.Statement.Statement;
import root.Logic.Phases.Statement.StatementDependency.StatementDependency;
import root.Logic.Phases.Statement.StatementDependency.StatementDependencyFraktion;
import root.Logic.Phases.Statement.StatementDependency.StatementDependencyRolle;
import root.Logic.Phases.Statement.StatementDependency.StatementDependencyStatement;
import root.Logic.Spieler;
import root.Logic.Torte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NormalNight extends Thread {
    public static List<Statement> statements;
    public static Object lock;

    public static List<Angriff> angriffe = new ArrayList<>();
    public static List<Opfer> opfer = new ArrayList<>();
    public List<String> opferDerNacht;

    public static List<Spieler> spielerAwake = new ArrayList<>();
    public static Spieler gefälschterSpieler;
    public static Spieler getarnterSpieler;
    public static Spieler torteSpieler;
    private static boolean letzteTorteGut;


    public void run() {
        lock = new Object();
        synchronized (lock) {
            FrontendObject dropdownOptions;
            FrontendObject info;

            String chosenOption;
            String chosenOptionLastStatement = null;
            Spieler chosenSpieler;

            Rolle rolle = null;
            Fraktion fraktion = null;

            String erzählerInfoIconImagePath;

            statements = NormalNightStatementBuilder.normalNightBuildStatements();

            beginNight();

            System.out.println(PhaseManager.nightCount + ". Nacht beginnt!");

            for (Statement statement : statements) {
                refreshStatementStates();

                chosenOption = null;

                if (statement.state != StatementState.INVISIBLE_NOT_IN_GAME) {
                    setSpielerAwake(statement);

                    StatementDependency dependency = statement.dependency;
                    while (dependency instanceof StatementDependencyStatement) {
                        dependency = ((StatementDependencyStatement) dependency).statement.dependency;
                    }

                    if (dependency instanceof StatementDependencyRolle) {
                        rolle = ((StatementDependencyRolle) dependency).rolle;
                    }
                    if (dependency instanceof StatementDependencyFraktion) {
                        fraktion = ((StatementDependencyFraktion) dependency).fraktion;
                    }

                    switch (statement.type) {
                        case SHOW_TITLE:
                            showTitle(statement);
                            break;

                        case ROLLE_CHOOSE_ONE:
                            dropdownOptions = rolle.getFrontendObject();
                            chosenOption = showFrontendObject(statement, dropdownOptions);
                            rolle.processChosenOption(chosenOption);
                            break;

                        case ROLLE_CHOOSE_ONE_INFO:
                            dropdownOptions = rolle.getFrontendObject();
                            chosenOption = showFrontendObject(statement, dropdownOptions);
                            info = rolle.processChosenOptionGetInfo(chosenOption);
                            showFrontendObject(statement, info);
                            break;

                        case ROLLE_INFO:
                            info = rolle.getInfo();
                            showFrontendObject(statement, info);
                            break;

                        case FRAKTION_CHOOSE_ONE:
                            dropdownOptions = fraktion.getFrontendObject();
                            chosenOption = showFrontendObject(statement, dropdownOptions);
                            fraktion.processChosenOption(chosenOption);
                            break;
                    }

                    switch (statement.id) {
                        case Wirt.STATEMENT_ID:
                            if (Wirt.JA.equals(chosenOption)) {
                                Game.game.freibier = true;
                            }
                            break;

                        case ProgramStatements.SCHÜTZE_ID:
                            setSchütze();
                            break;

                        case Henker.STATEMENT_ID:
                            dropdownOptions = rolle.getFrontendObject();
                            chosenOption = showFrontendObject(statement, dropdownOptions);
                            rolle.processChosenOption(chosenOption);

                            while (Henker.pagecounter < Henker.numberOfPages) {
                                if (FrontendObject.erzählerFrame.next) {
                                    henkerNächsteSeite();
                                } else {
                                    henkerSeiteZurück();
                                }
                            }

                            if (rolle.besucht != null) {
                                Henker henker = ((Henker) rolle);
                                info = henker.processChosenOptionsGetInfo(Henker.chosenHauptrolle.name, Henker.chosenBonusrolle.name);
                                showFrontendObject(statement, info);
                            }
                            break;

                        case SchattenpriesterFraktion.NEUER_SCHATTENPRIESTER:
                            chosenSpieler = Game.game.findSpieler(chosenOptionLastStatement);
                            List<String> neueSchattenpriester = new ArrayList<>();
                            erzählerInfoIconImagePath = ""; //TODO causes problem "1 Image could not be found at location: "
                            if (chosenSpieler != null) {
                                String neuerSchattenpriester = chosenSpieler.name;
                                neueSchattenpriester.add(neuerSchattenpriester);
                                if (SchattenpriesterFraktion.spielerToChangeCards != null) {
                                    neueSchattenpriester.add(SchattenpriesterFraktion.spielerToChangeCards.name);
                                    SchattenpriesterFraktion.spielerToChangeCards = null;
                                }

                                if (!chosenSpieler.hauptrolle.fraktion.equals(SchattenpriesterFraktion.ID)) {
                                    erzählerInfoIconImagePath = Schattenkutte.IMAGE_PATH;
                                }
                            }
                            showListShowImage(statement, neueSchattenpriester, SchattenpriesterFraktion.IMAGE_PATH, erzählerInfoIconImagePath);
                            break;

                        case Nachtfürst.TÖTEN_ID:
                            Nachtfürst nachtfürst = (Nachtfürst) rolle;
                            dropdownOptions = nachtfürst.getSecondFrontendObject();
                            chosenOption = showFrontendObject(statement, dropdownOptions);
                            nachtfürst.processSecondChosenOption(chosenOption);
                            break;

                        case Irrlicht.STATEMENT_ID:
                            dropdownOptions = rolle.getFrontendObject();
                            showFrontendObject(statement, dropdownOptions);
                            break;

                        case Irrlicht.INFO:
                            info = Irrlicht.processFlackerndeIrrlichter(FrontendObject.getFlackerndeIrrlichter());
                            showFrontendObject(statement, info);
                            break;

                        case Analytiker.STATEMENT_ID:
                            Analytiker analytiker = (Analytiker) rolle;

                            DropdownOptions analytikerDropdownOptions = analytiker.getDropdownOptions();
                            showDropdownPage(statement, analytikerDropdownOptions, analytikerDropdownOptions);

                            Spieler chosenSpieler1 = Game.game.findSpieler(FrontendObject.erzählerFrame.chosenOption1);
                            Spieler chosenSpieler2 = Game.game.findSpieler(FrontendObject.erzählerFrame.chosenOption2);

                            if (chosenSpieler1 != null && chosenSpieler2 != null) {
                                if (analytiker.showTarnumhang(chosenSpieler1, chosenSpieler2)) {
                                    showZeigekarte(statement, new Tarnumhang_BonusrollenType());
                                } else {
                                    String answer = analytiker.analysiere(chosenSpieler1, chosenSpieler2);
                                    showList(statement, answer);
                                }
                            }
                            break;

                        case Konditor.STATEMENT_ID:
                            if (gibtEsTorte()) {
                                Torte.torte = true;

                                dropdownOptions = rolle.getFrontendObject();
                                chosenOption = showKonditorDropdownPage(statement, dropdownOptions);
                                rolle.processChosenOption(chosenOption);

                                Torte.gut = chosenOption.equals(Konditor.GUT);
                            }
                            break;

                        case Konditorlehrling.STATEMENT_ID:
                            if (!gibtEsTorte()) {
                                Konditorlehrling konditorlehrling = (Konditorlehrling) rolle;

                                DropdownOptions dropdownOptionsSpieler = konditorlehrling.getDropdownOptionsSpieler();
                                DropdownOptions dropdownOptionsTorte = Konditor.getTortenOptions();
                                showDropdownPage(statement, dropdownOptionsSpieler, dropdownOptionsTorte);

                                konditorlehrling.processChosenOption(FrontendObject.erzählerFrame.chosenOption1);
                            }
                            break;

                        case IndieStatements.OPFER_ID:
                            setOpfer();

                            opferDerNacht = opfer.stream()
                                    .map(opfer -> opfer.spieler.name).distinct()
                                    .collect(Collectors.toList());

                            FrontendObject.erzählerListPage(statement, IndieStatements.OPFER_TITLE, opferDerNacht);
                            for (String opfer : opferDerNacht) {
                                FrontendObject.spielerAnnounceOpferPage(Game.game.findSpieler(opfer));
                                waitForAnswer();
                            }

                            checkVictory();
                            break;

                        case ProgramStatements.TORTE_ID:
                            if (Torte.torte) {
                                FrontendObject.erzählerTortenPage();
                                FrontendObject.showZeigekarteOnSpielerScreen(new Torten_Zeigekarte());

                                waitForAnswer();
                                Torte.setTortenEsser(FrontendObject.getTortenesser());
                            } else {
                                if (torteSpieler != null) {
                                    dropdownOptions = Torte.getFrontendObject();
                                    statement.title = Torte.TORTENSTUECK_TITLE;
                                    chosenOption = showFrontendObject(statement, dropdownOptions);

                                    if (chosenOption.equals(Torte.TORTE_NEHMEN)) {
                                        Torte.tortenStück = true;
                                        Torte.setTortenEsser(Collections.singletonList(torteSpieler.name));
                                    }
                                }
                            }
                            break;
                    }

                    chosenOptionLastStatement = chosenOption;

                    if (Game.game.freibier) {
                        break;
                    }
                }

                statement.alreadyOver = true;
            }
        }

        cleanUpNight();

        PhaseManager.nextPhase();
    }

    public void beginNight() {
        for (Spieler currentSpieler : Game.game.spieler) {
            currentSpieler.ressurectable = !currentSpieler.hauptrolle.fraktion.equals(Vampire.ID);
        }

        angriffe = new ArrayList<>();
        opfer = new ArrayList<>();

        for (Hauptrolle currentHauptrolle : Game.game.hauptrollen) {
            currentHauptrolle.besuchtLastNight = currentHauptrolle.besucht;
            currentHauptrolle.besucht = null;
        }

        for (Bonusrolle currentBonusrolle : Game.game.bonusrollen) {
            currentBonusrolle.besuchtLastNight = currentBonusrolle.besucht;
            currentBonusrolle.besucht = null;

            if (currentBonusrolle.equals(Analytiker.ID)) {
                ((Analytiker) currentBonusrolle).besuchtAnalysieren = null;
            }
        }

        if (Rolle.rolleLebend(Prostituierte.ID)) {
            Prostituierte.host = Game.game.findSpielerPerRolle(Prostituierte.ID);
        }

        for (Spieler currentSpieler : Game.game.spieler) {
            Hauptrolle hauptrolleSpieler = currentSpieler.hauptrolle;

            if (hauptrolleSpieler.equals(Schattenpriester.ID)) {
                if (((Schattenpriester) hauptrolleSpieler).neuster) {
                    currentSpieler.geschützt = true;
                    ((Schattenpriester) hauptrolleSpieler).neuster = false;
                }
            }
        }

        if (Torte.torte) {
            for (Spieler currentSpieler : Torte.tortenEsser) {
                if (Torte.gut) {
                    currentSpieler.geschützt = true;
                } else {
                    currentSpieler.aktiv = false;
                }
            }
        }

        if (Torte.tortenStück) {
            for (Spieler currentSpieler : Torte.tortenEsser) {
                if (Torte.stückGut) {
                    currentSpieler.geschützt = true;
                } else {
                    currentSpieler.aktiv = false;
                }
            }
        }

        Torte.tortenEsser.clear();
        Torte.torte = false;
        letzteTorteGut = Torte.gut;
        Torte.gut = false;

        GrafVladimir.verschleierterSpieler = null;
        getarnterSpieler = null;
        gefälschterSpieler = null;
        torteSpieler = null;

        Henker.pagecounter = 0;
    }

    private void refreshStatementStates() {
        for (Statement statement : statements) {
            if (!statement.alreadyOver) {
                statement.refreshState();
            }
        }
    }

    public void setSchütze() {
        for (Spieler currentSpieler : Game.game.spieler) {
            if (currentSpieler.bonusrolle.equals(DunklesLicht.ID)) {
                currentSpieler.geschützt = true;
            }
        }

        setNachtfürstSchutz();
    }

    private void setNachtfürstSchutz() {
        Rolle rolle = Rolle.findRolle(Nachtfürst.ID);

        if (rolle != null) {
            Nachtfürst nachtfürst = (Nachtfürst) rolle;

            if (!nachtfürst.isTötendeFraktion() && nachtfürst.guessedRight) {
                Spieler nachtfürstSpieler = Game.game.findSpielerPerRolle(nachtfürst.id);
                if (nachtfürstSpieler != null) {
                    nachtfürstSpieler.geschützt = true;
                }
            }
        }
    }

    public void setOpfer() {
        checkLiebespaar();
        killOpfer();
    }

    public void setSpielerAwake(Statement statement) {
        spielerAwake.clear();
        if (statement.dependency instanceof StatementDependencyFraktion) {
            StatementDependencyFraktion statementDependencyFraktion = (StatementDependencyFraktion) statement.dependency;
            spielerAwake.addAll(Fraktion.getFraktionsMembers(statementDependencyFraktion.fraktion.id));
        } else if (statement.dependency instanceof StatementDependencyRolle) {
            StatementDependencyRolle statementDependencyRolle = (StatementDependencyRolle) statement.dependency;
            spielerAwake.add(Game.game.findSpielerPerRolle(statementDependencyRolle.rolle.id));
        }
    }

    private void cleanUpNight() {
        checkNachtfürstGuess();

        for (Spieler currentSpieler : Game.game.spieler) {
            currentSpieler.aktiv = true;
            currentSpieler.geschützt = false;
            currentSpieler.ressurectable = true;
        }
    }

    private void checkNachtfürstGuess() {
        Rolle rolle = Rolle.findRolle(Nachtfürst.ID);

        if (rolle != null) {
            Nachtfürst nachtfürst = (Nachtfürst) rolle;

            int anzahlOpferDerNacht = getAnzahlOpferDerNacht();
            nachtfürst.checkGuess(anzahlOpferDerNacht);
        }
    }

    private int getAnzahlOpferDerNacht() {
        return opferDerNacht.size();
    }

    public void killOpfer() {
        for (Opfer currentOpfer : opfer) {
            if (Rolle.rolleLebend(Blutwolf.ID)) {
                if (currentOpfer.täterFraktion != null && currentOpfer.täterFraktion.equals(Werwölfe.ID)) {
                    Blutwolf.deadStacks++;
                    if (Blutwolf.deadStacks >= 2) {
                        Blutwolf.deadly = true;
                    }
                }
            }

            Game.game.killSpieler(currentOpfer.spieler);
        }
    }

    public void checkLiebespaar() {
        boolean spieler1Lebend = true;
        boolean spieler2Lebend = true;

        Liebespaar liebespaar = Game.game.liebespaar;

        if (liebespaar != null && liebespaar.spieler1 != null && liebespaar.spieler2 != null) {

            for (Opfer currentOpfer : opfer) {
                if (currentOpfer.spieler.equals(liebespaar.spieler1)) {
                    spieler1Lebend = false;
                }
                if (currentOpfer.spieler.equals(liebespaar.spieler2)) {
                    spieler2Lebend = false;
                }
            }

            if (spieler1Lebend && !spieler2Lebend) {
                Selbstmord.execute(liebespaar.spieler1);
            }

            if (!spieler1Lebend && spieler2Lebend) {
                Selbstmord.execute(liebespaar.spieler2);
            }
        }
    }

    public static boolean gibtEsTorte() {
        return opfer.size() == 0 &&
                !letzteTorteGut &&
                Rolle.rolleLebend(Konditor.ID) &&
                !Opfer.isOpferPerRolle(Konditor.ID) &&
                Rolle.rolleAktiv(Konditor.ID);
    }

    private void checkVictory() {
        Winner winner = Game.game.checkVictory();

        if (winner != Winner.NO_WINNER) {
            showEndScreenPage(winner);
        }
    }

    private String showFrontendObject(Statement statement, FrontendObject frontendObject) {
        if (frontendObject.title == null) {
            frontendObject.title = statement.title;
        }

        switch (statement.state) {
            case NORMAL:
                switch (frontendObject.typeOfContent) {
                    case TITLE:
                        showTitle(statement, frontendObject.title);
                        break;

                    case DROPDOWN:
                        showDropdown(statement, frontendObject.title, frontendObject.dropdownOptions);
                        return FrontendObject.erzählerFrame.chosenOption1;

                    case DROPDOWN_LIST:
                        showDropdownList(statement, frontendObject.title, frontendObject.dropdownOptions, frontendObject.hatZurückButton);
                        return FrontendObject.erzählerFrame.chosenOption1;

                    case DROPDOWN_SEPARATED_LIST:
                        showDropdownSeperatedList(statement, frontendObject.title, frontendObject.dropdownOptions, frontendObject.displayedStrings, frontendObject.hatZurückButton);
                        return FrontendObject.erzählerFrame.chosenOption1;

                    case DROPDOWN_IMAGE:
                        showDropdownShowImage(statement, frontendObject.title, frontendObject.dropdownOptions, frontendObject.imagePath);
                        return FrontendObject.erzählerFrame.chosenOption1;

                    case LIST:
                        showList(statement, frontendObject.title, frontendObject.displayedStrings, frontendObject.hatZurückButton);
                        break;

                    case LIST_WITH_NOTE:
                        showListWithNote(statement, frontendObject.title, frontendObject.displayedStrings, frontendObject.hatZurückButton, frontendObject.note);
                        break;

                    case IMAGE:
                        showImage(statement, frontendObject.title, frontendObject.imagePath);
                        break;

                    case CARD:
                        showCard(statement, frontendObject.title, frontendObject.imagePath);
                        break;

                    case LIST_IMAGE:
                        showListShowImage(statement, frontendObject.title, frontendObject.displayedStrings, frontendObject.imagePath);
                        break;

                    case SCHNÜFFLER_INFO:
                        showSchnüfflerInfo(statement, frontendObject.informationen);
                        break;

                    case IRRLICHT_DROPDOWN:
                        showIrrlichtDropdown(statement, frontendObject.title, frontendObject.dropdownOptions);
                        break;

                    case TWO_IMAGES:
                        showTwoImages(statement, frontendObject.title, frontendObject.imagePath, frontendObject.imagePath2, frontendObject.displayedStrings, frontendObject.hatZurückButton);
                        break;

                }
                break;

            case AUFGEBRAUCHT:
                showAufgebrauchtPages(statement, frontendObject);
                break;

            case DEAKTIV:
                showDeaktivPages(statement, frontendObject);
                break;

            case DEAD:
                showTotPages(statement, frontendObject);
                break;

            case NOT_IN_GAME:
                showAusDemSpielPages(statement, frontendObject);
                break;
        }

        return null;
    }

    private void showTwoImages(Statement statement, String title, String imagePath, String imagePath2, List<String> displayedStrings, boolean hatZurückButton) {
        FrontendObject.erzählerListPage(statement, title, displayedStrings, hatZurückButton);
        FrontendObject.spielerTwoImagePage(title, imagePath, imagePath2);

        waitForAnswer();
    }

    private void showIrrlichtDropdown(Statement statement, String title, DropdownOptions dropdownStrings) {
        FrontendObject.irrlichtDropdownPage(statement, dropdownStrings);
        FrontendObject.spielerTitlePage(title);

        waitForAnswer();
    }

    private void showDropdownShowImage(Statement statement, String title, DropdownOptions strings, String imagePath) {
        FrontendObject.erzählerDropdownPage(statement, strings);
        FrontendObject.spielerDropdownMirrorImagePage(title, imagePath);

        waitForAnswer();
    }

    public void showDropdownPage(Statement statement, DropdownOptions dropdownOptions1, DropdownOptions dropdownOptions2) {
        switch (statement.state) {
            case NORMAL:
                FrontendObject.erzählerDropdownPage(statement, dropdownOptions1, dropdownOptions2);
                FrontendObject.spielerDropdownPage(statement.title, 2);
                break;

            case DEAKTIV:
                Deaktiviert deaktiviert = new Deaktiviert();
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), getEmptyDropdownOptions(), deaktiviert.imagePath);
                FrontendObject.showZeigekarteOnSpielerScreen(deaktiviert);
                break;

            case DEAD:
                Tot tot = new Tot();
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), getEmptyDropdownOptions(), tot.imagePath);
                FrontendObject.showZeigekarteOnSpielerScreen(tot);
                break;

            case NOT_IN_GAME:
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), getEmptyDropdownOptions(), new AusDemSpiel().imagePath);
                FrontendObject.spielerDropdownPage(statement.title, 2);
                break;
        }

        waitForAnswer();
    }

    public String showKonditorDropdownPage(Statement statement, FrontendObject frontendObject) {
        FrontendObject.erzählerDropdownPage(statement, frontendObject.dropdownOptions);
        FrontendObject.spielerDropdownPage(statement.title, 1);

        waitForAnswer();

        return FrontendObject.erzählerFrame.chosenOption1;
    }

    private void showEndScreenPage(Winner winner) {
        FrontendObject.erzählerEndScreenPage(winner);
        FrontendObject.spielerEndScreenPage(winner);

        waitForAnswer();
    }

    private void showZeigekarte(Statement statement, Zeigekarte zeigekarte) {
        FrontendObject.erzählerIconPicturePage(statement, zeigekarte.imagePath);
        FrontendObject.spielerIconPicturePage(zeigekarte.title, zeigekarte.imagePath);

        waitForAnswer();
    }

    //TODO Cases die sowieso gleich aussehen zusammenfassen
    public void showAufgebrauchtPages(Statement statement, FrontendObject frontendObject) {
        Zeigekarte aufgebraucht = new Aufgebraucht();

        switch (frontendObject.typeOfContent) {
            case DROPDOWN:
            case DROPDOWN_LIST:
            case DROPDOWN_SEPARATED_LIST:
            case DROPDOWN_IMAGE:
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), aufgebraucht.imagePath);
                break;

            case LIST:
            case LIST_IMAGE:
                FrontendObject.erzählerListPage(statement, getEmptyStringList(), aufgebraucht.imagePath);
                break;

            case TITLE:
            case IMAGE:
            case CARD:
            case SCHNÜFFLER_INFO:
            default:
                FrontendObject.erzählerIconPicturePage(statement, aufgebraucht.imagePath);
                break;
        }

        FrontendObject.showZeigekarteOnSpielerScreen(aufgebraucht);
        waitForAnswer();
    }

    public void showDeaktivPages(Statement statement, FrontendObject frontendObject) {
        Zeigekarte deaktiviert = new Deaktiviert();

        switch (frontendObject.typeOfContent) {
            case DROPDOWN:
            case DROPDOWN_LIST:
            case DROPDOWN_SEPARATED_LIST:
            case DROPDOWN_IMAGE:
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), deaktiviert.imagePath);
                break;

            case LIST:
            case LIST_IMAGE:
                FrontendObject.erzählerListPage(statement, getEmptyStringList(), deaktiviert.imagePath);
                break;

            case TITLE:
            case IMAGE:
            case CARD:
            case SCHNÜFFLER_INFO:
            default:
                FrontendObject.erzählerIconPicturePage(statement, deaktiviert.imagePath);
                break;
        }

        FrontendObject.showZeigekarteOnSpielerScreen(deaktiviert);
        waitForAnswer();
    }

    public void showTotPages(Statement statement, FrontendObject frontendObject) {
        Zeigekarte tot = new Tot();

        switch (frontendObject.typeOfContent) {
            case DROPDOWN:
            case DROPDOWN_LIST:
            case DROPDOWN_SEPARATED_LIST:
            case DROPDOWN_IMAGE:
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), tot.imagePath);
                break;
            case LIST:
            case LIST_IMAGE:
                FrontendObject.erzählerListPage(statement, getEmptyStringList(), tot.imagePath);
                break;

            case TITLE:
            case IMAGE:
            case CARD:
            case SCHNÜFFLER_INFO:
            default:
                FrontendObject.erzählerIconPicturePage(statement, tot.imagePath);
                break;
        }

        FrontendObject.showZeigekarteOnSpielerScreen(tot);
        waitForAnswer();
    }

    public void showAusDemSpielPages(Statement statement, FrontendObject frontendObject) {
        Zeigekarte ausDemSpiel = new AusDemSpiel();

        switch (frontendObject.typeOfContent) {
            case DROPDOWN:
            case DROPDOWN_LIST:
            case DROPDOWN_SEPARATED_LIST:
            case DROPDOWN_IMAGE:
                FrontendObject.erzählerDropdownPage(statement, getEmptyDropdownOptions(), ausDemSpiel.imagePath);
                FrontendObject.spielerDropdownPage(statement.title, 1);
                break;

            case LIST:
            case LIST_IMAGE:
                FrontendObject.erzählerListPage(statement, getEmptyStringList(), ausDemSpiel.imagePath);
                FrontendObject.spielerListPage(statement.title, getEmptyStringList());
                break;

            case TITLE:
            case IMAGE:
            case CARD:
            case SCHNÜFFLER_INFO:
            default:
                FrontendObject.erzählerIconPicturePage(statement, ausDemSpiel.imagePath);
                FrontendObject.spielerIconPicturePage(statement.title, "");
                break;
        }

        waitForAnswer();
    }

    public void showTitle(Statement statement) {
        showTitle(statement, statement.title);
    }

    public void showTitle(Statement statement, String title) {
        FrontendObject.erzählerDefaultNightPage(statement);
        FrontendObject.spielerTitlePage(title);

        waitForAnswer();
    }

    public void showDropdown(Statement statement, String title, DropdownOptions dropdownOptions) {
        FrontendObject.erzählerDropdownPage(statement, dropdownOptions);
        FrontendObject.spielerDropdownPage(title, 1);

        waitForAnswer();
    }

    public void showDropdownList(Statement statement, String title, DropdownOptions dropdownOptions, boolean hatZurückButton) {
        FrontendObject.erzählerDropdownPage(statement, dropdownOptions, hatZurückButton);
        FrontendObject.spielerDropdownListPage(title, dropdownOptions);

        waitForAnswer();
    }

    public void showDropdownSeperatedList(Statement statement, String title, DropdownOptions dropdownStrings, List<String> listStrings, boolean hatZurückButton) {
        FrontendObject.erzählerDropdownPage(statement, dropdownStrings, hatZurückButton);
        FrontendObject.spielerDropdownListPage(title, listStrings);

        waitForAnswer();
    }

    public void showList(Statement statement, String string) {
        List<String> list = new ArrayList<>();
        list.add(string);
        showList(statement, list);
    }

    public void showList(Statement statement, List<String> strings) {
        showList(statement, statement.title, strings, false);
    }

    public void showList(Statement statement, String title, List<String> strings, boolean hatZurückButton) {
        FrontendObject.erzählerListPage(statement, title, strings, hatZurückButton);
        FrontendObject.spielerListPage(title, strings);

        waitForAnswer();
    }

    public void showListWithNote(Statement statement, String title, List<String> strings, boolean hatZurückButton, String note) {
        FrontendObject.erzählerListPage(statement, title, strings, hatZurückButton);
        FrontendObject.spielerListPageWithNote(title, strings, note);

        waitForAnswer();
    }

    public void showImage(Statement statement, String imagePath) {
        showImage(statement, statement.title, imagePath);
    }

    public void showImage(Statement statement, String title, String imagePath) {
        FrontendObject.erzählerIconPicturePage(statement, title, imagePath);
        FrontendObject.spielerIconPicturePage(title, imagePath);

        waitForAnswer();
    }

    public void showCard(Statement statement, String title, String imagePath) {
        FrontendObject.erzählerCardPicturePage(statement, title, imagePath);
        FrontendObject.spielerCardPicturePage(title, imagePath);

        waitForAnswer();
    }

    public void showListShowImage(Statement statement, String string, String spielerImagePath) {
        List<String> list = new ArrayList<>();
        list.add(string);
        showListShowImage(statement, statement.title, list, spielerImagePath);
    }

    public void showListShowImage(Statement statement, List<String> strings, String spielerImagePath, String erzählerImagePath) {
        showListShowImage(statement, statement.title, strings, spielerImagePath, erzählerImagePath);
    }

    public void showListShowImage(Statement statement, String title, List<String> strings, String spielerImagePath) {
        FrontendObject.erzählerListPage(statement, strings);
        FrontendObject.spielerIconPicturePage(title, spielerImagePath);

        waitForAnswer();
    }

    public void showListShowImage(Statement statement, String title, List<String> strings, String spielerImagePath, String erzählerImagePath) {
        FrontendObject.erzählerListPage(statement, strings, erzählerImagePath);
        FrontendObject.spielerIconPicturePage(title, spielerImagePath);

        waitForAnswer();
    }

    public void showSchnüfflerInfo(Statement statement, List<SchnüfflerInformation> informationen) {
        FrontendObject.erzählerDefaultNightPage(statement);
        FrontendObject.spielerSchnüfflerInfoPage(informationen);

        waitForAnswer();
    }

    public static List<String> getEmptyStringList() {
        List<String> emptyContent = new ArrayList<>();
        emptyContent.add("");
        return emptyContent;
    }

    public static DropdownOptions getEmptyDropdownOptions() {
        return new DropdownOptions(getEmptyStringList());
    }

    public void waitForAnswer() {
        FrontendObject.refreshÜbersichtsFrame();
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void henkerNächsteSeite() {
        Henker henker = (Henker) Game.game.findHauptrolle(Henker.ID);
        Henker.pagecounter++;
        if (Henker.pagecounter < Henker.numberOfPages) {
            FrontendObject frontendObject = henker.getPage(FrontendObject.erzählerFrame.chosenOption1);
            showHenkerPage(frontendObject);
        }
    }

    public void henkerSeiteZurück() {
        Henker henker = (Henker) Game.game.findHauptrolle(Henker.ID);
        if (Henker.pagecounter > 0) {
            Henker.pagecounter--;
            FrontendObject frontendObject = henker.getPage();
            showHenkerPage(frontendObject);
        }
    }

    public void showHenkerPage(FrontendObject frontendObject) {
        Statement henkerStatement = statements.stream()
                .filter(statement -> statement.id.equals(Henker.STATEMENT_ID))
                .findAny().orElse(null);

        showFrontendObject(henkerStatement, frontendObject);
    }
}
