package root.Frontend.Frame;

import root.Frontend.Factories.ErzählerPageFactory;
import root.Frontend.FrontendControl;
import root.Frontend.Page.Page;
import root.Frontend.Page.PageTable;
import root.Phases.*;
import root.ResourceManagement.FileManager;
import root.ResourceManagement.ResourcePath;
import root.Rollen.Hauptrolle;
import root.Rollen.Nebenrolle;
import root.Spieler;
import root.mechanics.Torte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ErzählerFrame extends MyFrame implements ActionListener {
    public SpielerFrame spielerFrame;
    public ÜbersichtsFrame übersichtsFrame;
    public Page savePage;
    public ArrayList<Page> setupPages;

    FileManager fileManager;

    public static Object lock;

    public int mode = ErzählerFrameMode.setup;

    public ErzählerPageFactory pageFactory;

    public ArrayList<JButton> goBackButtons;
    public ArrayList<JButton> goNextButtons;

    public Page startPage;
    public JButton startJButton;
    public JButton loadLastGameJButton;
    public JButton loadLastCompositionJButton;

    public Page playerSetupPage;
    public JLabel mainRoleCounterJLabel;
    public PageTable playerTable;
    public PageTable deletePlayerTable;
    public ArrayList<JButton> deletePlayerButtons;
    public JButton playerGoBackJButton;
    public JButton playerGoNextJButton;
    public JTextField addPlayerTxtField;
    public JButton addPlayerButton;

    public Page mainRoleSetupPage;
    public JLabel secondaryRoleCounterJLabel;
    public JLabel numberOfPlayersJLabel;
    public JButton mainRoleGoBackJButton;
    public JButton mainRoleGoNextJButton;
    public ArrayList<JButton> mainRoleButtons;
    public PageTable mainRoleLabelTable;
    public PageTable deleteMainRoleTable;
    public ArrayList<JButton> deleteMainRoleButtons;
    public JButton addAllMainRolesJButton;

    public Page secondaryRoleSetupPage;
    public JButton secondaryRoleGoBackJButton;
    public JButton secondaryRoleGoNextJButton;
    public ArrayList<JButton> secondaryRoleButtons;
    public PageTable secondaryRoleLabelTable;
    public PageTable deleteSecondaryRoleTable;
    public ArrayList<JButton> deleteSecondaryRoleButtons;
    public JButton addAllSecondaryRolesJButton;

    public Page playerSpecifiyPage;
    public JButton playerSpecifyGoNextJButton;
    public JButton playerSpecifyGoBackJButton;
    public PageTable playerSpecifyTable;
    public PageTable mainRoleSpecifyTable;
    public PageTable secondaryRoleSpecifyTable;
    public PageTable deleteSpecifyPlayerTable;
    public ArrayList<JButton> deleteSpecifyPlayerButtons;

    public ArrayList<Spieler> playersSpecified; // in game object bewegen

    public String chosenOption1;
    public String chosenOption2;
    String chosenOption3;

    public JComboBox comboBox1;
    public JComboBox comboBox2;
    public JComboBox comboBox3;
    public JButton okButton;
    public JButton nextJButton;
    public JButton goBackJButton;
    public JButton nachzüglerJButton;
    public JButton umbringenJButton;
    public JButton priesterJButton;
    public JButton richterinJButton;
    public JButton respawnFramesJButton;

    public Page tortenPage;
    public JButton addPlayerTortenButton;
    public ArrayList<JButton> deleteTortenPlayerButtons;

    public ErzählerFrame() {
        calcFrameSize();

        WINDOW_TITLE = "Erzähler Fenster";

        Hauptrolle.generateAllAvailableMainRoles();
        Nebenrolle.generateAllAvailableSecondaryRoles();

        goBackButtons = new ArrayList<>();
        goNextButtons = new ArrayList<>();

        playersSpecified = new ArrayList<>();

        setupPages = new ArrayList<>();

        chosenOption1 = "";
        chosenOption2 = "";
        chosenOption3 = "";

        comboBox1 = new JComboBox();
        comboBox2 = new JComboBox();
        comboBox3 = new JComboBox();

        pageFactory = new ErzählerPageFactory(this);

        deletePlayerButtons = new ArrayList<JButton>();
        deleteTortenPlayerButtons = new ArrayList<JButton>();
        mainRoleButtons = new ArrayList<JButton>();
        deleteMainRoleButtons = new ArrayList<JButton>();
        secondaryRoleButtons = new ArrayList<JButton>();
        deleteSecondaryRoleButtons = new ArrayList<JButton>();
        deleteSpecifyPlayerButtons = new ArrayList<JButton>();

        frameJpanel = generateDefaultPanel();

        generateAllPages();

        currentPage = startPage;

        buildScreenFromPage(startPage);

        showFrame();

        fileManager = new FileManager();
        fileManager.createFolderInAppdata();
    }

    public void generateAllPages() {
        setupPages = new ArrayList<>();
        startPage = pageFactory.generateStartPage();
        playerSetupPage = pageFactory.generatePlayerSetupPage();
        mainRoleSetupPage = pageFactory.generateMainRoleSetupPage();
        secondaryRoleSetupPage = pageFactory.generateSecondaryRoleSetupPage();
        playerSpecifiyPage = pageFactory.generatePlayerSpecifiyPage();
    }

    public void refreshPlayerPage(){
        refreshPlayerTable();
        refreshDeletePlayerTable();
        refreshNumberOfPlayersLabel();
        buildScreenFromPage(playerSetupPage);
        addPlayerTxtField.requestFocusInWindow();
    }

    public void refreshPlayerTable() {
        playerTable.tableElements.clear();

        for(Spieler spieler : Spieler.spieler) {
            playerTable.add(new JLabel(spieler.name));
        }
    }

    public void refreshDeletePlayerTable(){
        deletePlayerTable.tableElements.clear();
        deletePlayerButtons.clear();

        for(Spieler spieler : Spieler.spieler) {
            JButton deleteButton = pageFactory.pageElementFactory.generateDeleteButton();
            deletePlayerTable.add(deleteButton);
            deletePlayerButtons.add(deleteButton);
        }
    }

    public void refreshMainRolePage(){
        refreshMainRoleTable();
        refreshDeleteMainRoleTable();
        refreshMainRoleCounterLabel();
        disableMainRoleButtons();
        buildScreenFromPage(mainRoleSetupPage);
    }

    public void refreshDeleteMainRoleTable(){
        deleteMainRoleTable.tableElements.clear();
        deleteMainRoleButtons.clear();

        for(Hauptrolle hauptrolle : Hauptrolle.mainRolesInGame) {
            JButton deleteButton = pageFactory.pageElementFactory.generateDeleteButton();
            deleteMainRoleTable.add(deleteButton);
            deleteMainRoleButtons.add(deleteButton);
        }
    }

    public void refreshMainRoleTable() {
        mainRoleLabelTable.tableElements.clear();

        for(Hauptrolle hauptrolle: Hauptrolle.mainRolesInGame) {
            mainRoleLabelTable.add(new JLabel(hauptrolle.getName()));
        }
    }

    public void disableMainRoleButtons() {
        for(JButton button : mainRoleButtons) {
            button.setEnabled(true);
            button.setBackground(Hauptrolle.findHauptrolle(button.getText()).getFraktion().getFarbe());
        }

        for(Hauptrolle hauptrolle : Hauptrolle.mainRolesInGame) {
            for(JButton button : mainRoleButtons) {
                int occurrences = Hauptrolle.numberOfOccurencesOfMainRoleInGame(hauptrolle);
                if(button.getText().equals(hauptrolle.getName()) && hauptrolle.getNumberOfPossibleInstances()<=occurrences){
                    if(button.isEnabled()) {
                        disableButton(button);
                    }
                }
            }
        }
    }

    public void disableSecondaryRoleButtons() {
        for(JButton button : secondaryRoleButtons) {
            button.setEnabled(true);
            button.setBackground(Nebenrolle.defaultFarbe);
        }

        for(Nebenrolle nebenrolle: Nebenrolle.secondaryRolesInGame) {
            for(JButton button : secondaryRoleButtons) {
                int occurrences = Nebenrolle.numberOfOccurencesOfSecondaryRoleInGame(nebenrolle);
                if(button.getText().equals(nebenrolle.getName()) && nebenrolle.getNumberOfPossibleInstances()<=occurrences){
                    if(button.isEnabled()) {
                        disableButton(button);
                    }
                }
            }
        }
    }

    public void disableButton(JButton button) {
        button.setEnabled(false);
        Color oldColor = button.getBackground();
        int offset = 128;
        int newRed = oldColor.getRed()+offset;
        int newBlue = oldColor.getBlue()+offset;
        int newGreen = oldColor.getGreen()+offset;
        if(newRed>255)
            newRed=255;
        if(newBlue>255)
            newBlue=255;
        if(newGreen>255)
            newGreen=255;
        Color newColor = new Color(newRed, newGreen, newBlue);
        button.setBackground(newColor);
    }

    public void refreshSecondaryRolePage(){
        refreshSecondaryRoleTable();
        refreshDeleteSecondaryRoleTable();
        refreshSecondaryRoleCounterLabel();
        disableSecondaryRoleButtons();
        buildScreenFromPage(secondaryRoleSetupPage);
    }

    public void refreshSecondaryRoleTable() {
        secondaryRoleLabelTable.tableElements.clear();

        for(Nebenrolle nebenrolle: Nebenrolle.secondaryRolesInGame) {
            secondaryRoleLabelTable.add(new JLabel(nebenrolle.getName()));
        }
    }

    public void refreshDeleteSecondaryRoleTable(){
        deleteSecondaryRoleTable.tableElements.clear();
        deleteSecondaryRoleButtons.clear();

        for(Nebenrolle nebenrolle : Nebenrolle.secondaryRolesInGame) {
            JButton deleteButton = pageFactory.pageElementFactory.generateDeleteButton();
            deleteSecondaryRoleTable.add(deleteButton);
            deleteSecondaryRoleButtons.add(deleteButton);
        }
    }

    public void refreshNumberOfPlayersLabel() {
        numberOfPlayersJLabel.setText(pageFactory.pageElementFactory.generateNumberOfPLayersLabelTitle());
    }

    public void refreshMainRoleCounterLabel() {
        mainRoleCounterJLabel.setText(pageFactory.pageElementFactory.generateMainRoleCounterLabelTitle());
    }

    public void refreshSecondaryRoleCounterLabel() {
        secondaryRoleCounterJLabel.setText(pageFactory.pageElementFactory.generateSecondaryRoleCounterLabelTitle());
    }

    public void refreshSpecifyPlayerPage(){
        refreshSpecifyComboBoxes();
        refreshSecifyPlayerTable();
        refreshSecifyMainRoleTable();
        refreshSecifySecondaryRoleTable();
        refreshDeleteSecifyPlayerTable();
        buildScreenFromPage(playerSpecifiyPage);
    }

    public void refreshSpecifyComboBoxes() {
        DefaultComboBoxModel model1 = new DefaultComboBoxModel(getPlayersUnspecifiedStrings().toArray());
        comboBox1.setModel(model1);
        DefaultComboBoxModel model2 = new DefaultComboBoxModel(getMainRolesUnspecifiedStrings().toArray());
        comboBox2.setModel(model2);
        DefaultComboBoxModel model3 = new DefaultComboBoxModel(getSecondaryRolesUnspecifiedStrings().toArray());
        comboBox3.setModel(model3);
    }

    public void refreshSecifyPlayerTable() {
        playerSpecifyTable.tableElements.clear();

        for(Spieler spieler : playersSpecified) {
            playerSpecifyTable.add(new JLabel(spieler.name));
        }
    }

    public void refreshSecifyMainRoleTable() {
        mainRoleSpecifyTable.tableElements.clear();

        for(String mainRoleName : getMainRolesSpecifiedStrings()) {
            if(mainRoleName == "" || mainRoleName == null) {
                mainRoleName = Hauptrolle.defaultHauptrolle.getName();
            }
            mainRoleSpecifyTable.add(new JLabel(mainRoleName));
        }
    }

    public void refreshSecifySecondaryRoleTable() {
        secondaryRoleSpecifyTable.tableElements.clear();

        for(String secondaryRoleName : getSecondaryRoleSpecifiedStrings()) {
            if(secondaryRoleName == "" || secondaryRoleName == null) {
                secondaryRoleName = Nebenrolle.defaultNebenrolle.getName();
            }
            secondaryRoleSpecifyTable.add(new JLabel(secondaryRoleName));
        }
    }

    public void refreshDeleteSecifyPlayerTable() {
        deleteSpecifyPlayerTable.tableElements.clear();
        deleteSpecifyPlayerButtons.clear();

        for(Spieler spieler : playersSpecified) {
            JButton deleteButton = pageFactory.pageElementFactory.generateDeleteButton();
            deleteSpecifyPlayerTable.add(deleteButton);
            deleteSpecifyPlayerButtons.add(deleteButton);
        }
    }

    public void removeSpecifiedIndex(int index) {
        if(deleteSpecifyPlayerButtons.size()>index) {
            deleteSpecifyPlayerButtons.remove(index);
        }

        playersSpecified.remove(index);
    }

    public void refreshTortenPage() {
        refreshTortenPlayerTable();
        refreshDeleteTortenPlayerTable();
        refreshTortenComboBoxes();

        buildScreenFromPage(tortenPage);
    }

    public void refreshTortenPlayerTable() {
        playerTable.tableElements.clear();

        for(Spieler spieler : Torte.tortenEsser) {
            playerTable.add(new JLabel(spieler.name));
        }
    }

    public void refreshDeleteTortenPlayerTable() {
        deletePlayerTable.tableElements.clear();
        deleteTortenPlayerButtons.clear();

        for(Spieler spieler : Torte.tortenEsser) {
            JButton deleteButton = pageFactory.pageElementFactory.generateDeleteButton();
            deletePlayerTable.add(deleteButton);
            deleteTortenPlayerButtons.add(deleteButton);
        }
    }

    public void refreshTortenComboBoxes() {
        ArrayList<String> nichtTortenEsser = Spieler.getLivigPlayerStrings();
        ArrayList<String> tortenEsser = new ArrayList<String>();
        for(Spieler spieler : Torte.tortenEsser) {
            tortenEsser.add(spieler.name);
        }
        nichtTortenEsser.removeAll(tortenEsser);
        DefaultComboBoxModel model1 = new DefaultComboBoxModel(nichtTortenEsser.toArray());
        comboBox1.setModel(model1);
    }

    public void setUpVariables() {
        Spieler.spieler.clear();
        Hauptrolle.mainRolesInGame.clear();
        Nebenrolle.secondaryRolesInGame.clear();
        playersSpecified.clear();
    }

    public void nextPage() {
        if(currentPage!=null) {
            if(currentPage!=playerSpecifiyPage && setupPages.contains(currentPage)) {
                int index = setupPages.indexOf(currentPage);
                generateAllPages();
                currentPage = setupPages.get(index + 1);
            }
            buildScreenFromPage(currentPage);
            refreshPage(currentPage);
            if(mode == ErzählerFrameMode.setup) {
                spielerFrame.refreshSetupPage();
            }
        }
    }

    public void prevPage() {
        int index = setupPages.indexOf(currentPage);
        generateAllPages();
        currentPage = setupPages.get(index-1);
        buildScreenFromPage(currentPage);
        refreshPage(currentPage);
        if(mode == ErzählerFrameMode.setup) {
            spielerFrame.refreshSetupPage();
        }
        if(currentPage.equals(startPage)){
            spielerFrame.dispatchEvent(new WindowEvent(spielerFrame, WindowEvent.WINDOW_CLOSING));
            playersSpecified = new ArrayList<>();
        }
    }

    public void refreshPage(Page page){
        if(page == playerSetupPage)
            refreshPlayerPage();
        else if (page == mainRoleSetupPage)
            refreshMainRolePage();
        else if (page == secondaryRoleSetupPage)
            refreshSecondaryRolePage();
        else if (page == playerSpecifiyPage)
            refreshSpecifyPlayerPage();
    }

    public void actionPerformed (ActionEvent ae) {
        if(goNextButtons.contains(ae.getSource())){
            if(ae.getSource() == loadLastGameJButton) {
                setUpVariables();
                fileManager.read(ResourcePath.LAST_GAME_FILE);
                this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                spielerFrame = new SpielerFrame(this);
                spielerFrame.refreshPlayerSetupPage();
            } else if(ae.getSource() == loadLastCompositionJButton) {
                setUpVariables();
                fileManager.readComposition(ResourcePath.LAST_GAME_COMPOSITION_FILE);
                playersSpecified = (ArrayList)Spieler.spieler.clone();
                this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                spielerFrame = new SpielerFrame(this);
                spielerFrame.refreshPlayerSetupPage();
            } else if(ae.getSource() == startJButton) {
                setUpVariables();
                this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                spielerFrame = new SpielerFrame(this);
                spielerFrame.refreshPlayerSetupPage();
            }

            if(ae.getSource() == secondaryRoleGoNextJButton) {
                fileManager.write(ResourcePath.LAST_GAME_FILE, Spieler.getLivigPlayerStrings(),
                        Hauptrolle.getMainRoleInGameNames(), Nebenrolle.getSecondaryRoleInGameNames());
            }

            if(ae.getSource() == playerSpecifyGoNextJButton) {
                if(playersSpecified.size() == Spieler.spieler.size()) {
                    PhaseManager.firstnight(this);
                    fileManager.writeComposition(ResourcePath.LAST_GAME_COMPOSITION_FILE, Spieler.getLivigPlayer(),
                            getMainRolesUnspecifiedStrings(), getSecondaryRolesUnspecifiedStrings());
                } else {
                    try {
                        String spielerName = (String) comboBox1.getSelectedItem();
                        Spieler spieler = Spieler.findSpieler(spielerName);
                        playersSpecified.add(spieler);

                        String hauptrolle = (String) comboBox2.getSelectedItem();
                        spieler.hauptrolle = Hauptrolle.findHauptrolle(hauptrolle);
                        if(spieler.hauptrolle == null) {
                            spieler.hauptrolle = Hauptrolle.defaultHauptrolle;
                        }

                        String nebenrolle = (String) comboBox3.getSelectedItem();
                        spieler.nebenrolle = Nebenrolle.findNebenrolle(nebenrolle);
                        if(spieler.nebenrolle == null) {
                            spieler.nebenrolle = Nebenrolle.defaultNebenrolle;
                        }

                        refreshSpecifyPlayerPage();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("No player in game.");
                    }
                }
            } else if(ae.getSource() == secondaryRoleGoNextJButton) {
                playerSpecifiyPage = pageFactory.generatePlayerSpecifiyPage();
            }

            nextPage();
        } else if(goBackButtons.contains(ae.getSource())){
            if(mode == ErzählerFrameMode.nachzüglerSetup || mode == ErzählerFrameMode.umbringenSetup ||
                    mode == ErzählerFrameMode.priesterSetup ||mode == ErzählerFrameMode.richterinSetup){
                mode = ErzählerFrameMode.getPhaseMode();
                buildScreenFromPage(pageFactory.generateDefaultDayPage());
            }else {
                prevPage();
            }
        } else if (ae.getSource() == addPlayerButton || ae.getSource() == addPlayerTxtField && mode == ErzählerFrameMode.setup) {
            if (!addPlayerTxtField.getText().equals("") && !Spieler.spielerExists(addPlayerTxtField.getText())) {
                String newPlayerName = addPlayerTxtField.getText();
                Spieler newPlayer = new Spieler(newPlayerName);
                Spieler.spieler.add(newPlayer);
                addPlayerTxtField.setText("");

                refreshPlayerPage();
                spielerFrame.refreshPlayerSetupPage();
            }
        } else if (ae.getSource() == addPlayerTortenButton) {
            try {
                String spielerName = comboBox1.getSelectedItem().toString();
                Spieler spieler = Spieler.findSpieler(spielerName);
                Torte.tortenEsser.add(spieler);
                refreshTortenPage();
            } catch(NullPointerException e) {}
        }else if (deleteTortenPlayerButtons.contains(ae.getSource())) {
            for (int i = 0; i < deleteTortenPlayerButtons.size(); i++) {
                if (ae.getSource() == deleteTortenPlayerButtons.get(i)) {
                    deleteTortenPlayerButtons.remove(i);

                    Torte.tortenEsser.remove(i);

                    refreshTortenPage();
                }
            }
        } else if (deletePlayerButtons.contains(ae.getSource())) {
            for (int i = 0; i < deletePlayerButtons.size(); i++) {
                if (ae.getSource() == deletePlayerButtons.get(i)) {
                    deletePlayerButtons.remove(i);
                    String spielerName = Spieler.spieler.get(i).name;

                    if(playersSpecified.contains(spielerName)) {
                        int index = playersSpecified.indexOf(spielerName);
                        removeSpecifiedIndex(index);
                    }

                    Spieler.spieler.remove(i);

                    refreshPlayerPage();
                    spielerFrame.refreshPlayerSetupPage();
                }
            }
        } else if (addAllMainRolesJButton == ae.getSource()) {
            Hauptrolle.addAllMainRoles();
            refreshMainRolePage();
            spielerFrame.refreshMainRoleSetupPage();
        } else if (mainRoleButtons.contains(ae.getSource())) {
            for (int i = 0; i < mainRoleButtons.size(); i++) {
                if (ae.getSource() == mainRoleButtons.get(i)) {
                    String mainRoleName = mainRoleButtons.get(i).getText();
                    Hauptrolle newMainRole = Hauptrolle.findHauptrolle(mainRoleName);
                    Hauptrolle.mainRolesInGame.add(newMainRole);

                    refreshMainRolePage();
                    spielerFrame.refreshMainRoleSetupPage();
                }
            }
        } else if (deleteMainRoleButtons.contains(ae.getSource())) {
            for (int i = 0; i < deleteMainRoleButtons.size(); i++) {
                if (ae.getSource() == deleteMainRoleButtons.get(i)) {
                    deleteMainRoleButtons.remove(i);
                    String mainRoleName = Hauptrolle.mainRolesInGame.get(i).getName();

                    ArrayList<String> mainRolesSpecified = getMainRolesSpecifiedStrings();

                    if(mainRolesSpecified.contains(mainRoleName)) {
                        int index = mainRolesSpecified.indexOf(mainRoleName);
                        removeSpecifiedIndex(index);
                    }

                    Hauptrolle.mainRolesInGame.remove(i);

                    refreshMainRolePage();
                    spielerFrame.refreshMainRoleSetupPage();
                }
            }
        }  else if (addAllSecondaryRolesJButton == ae.getSource()) {
            Nebenrolle.addAllSecondaryRoles();
            refreshSecondaryRolePage();
            spielerFrame.refreshSecondaryRoleSetupPage();
        } else if (secondaryRoleButtons.contains(ae.getSource())) {
            for (int i = 0; i < secondaryRoleButtons.size(); i++) {
                if (ae.getSource() == secondaryRoleButtons.get(i)) {
                    String secondaryRoleName = secondaryRoleButtons.get(i).getText();
                    Nebenrolle newSecondaryRole = Nebenrolle.findNebenrolle(secondaryRoleName);
                    Nebenrolle.secondaryRolesInGame.add(newSecondaryRole);

                    refreshSecondaryRolePage();
                    spielerFrame.refreshSecondaryRoleSetupPage();
                }
            }
        } else if (deleteSecondaryRoleButtons.contains(ae.getSource())) {
            for (int i = 0; i < deleteSecondaryRoleButtons.size(); i++) {
                if (ae.getSource() == deleteSecondaryRoleButtons.get(i)) {
                    deleteSecondaryRoleButtons.remove(i);
                    String secondaryRoleName = Nebenrolle.secondaryRolesInGame.get(i).getName();

                    ArrayList<String> secondaryRolesSpecified = getMainRolesSpecifiedStrings();

                    if(secondaryRolesSpecified.contains(secondaryRoleName)) {
                        int index = secondaryRolesSpecified.indexOf(secondaryRoleName);
                        removeSpecifiedIndex(index);
                    }

                    Nebenrolle.secondaryRolesInGame.remove(i);

                    refreshSecondaryRolePage();
                    spielerFrame.refreshSecondaryRoleSetupPage();
                }
            }
        } else if (deleteSpecifyPlayerButtons.contains(ae.getSource())) {
            for (int i = 0; i < deleteSpecifyPlayerButtons.size(); i++) {
                if (ae.getSource() == deleteSpecifyPlayerButtons.get(i)) {
                    removeSpecifiedIndex(i);

                    refreshSpecifyPlayerPage();
                }
            }
        } else if (ae.getSource() == nextJButton) {
            try {
                if(comboBox1 != null) {
                    chosenOption1 = (String) comboBox1.getSelectedItem();
                }

                if(comboBox2 != null) {
                    chosenOption2 = (String) comboBox2.getSelectedItem();
                }

                if(comboBox3 != null) {
                    chosenOption3 = (String) comboBox3.getSelectedItem();
                }
            }catch (NullPointerException e) {
                System.out.println("some comboboxes (1,2,3) might not be initialized.");
            }

            spielerFrame.mode = SpielerFrameMode.blank;
            spielerFrame.buildScreenFromPage(spielerFrame.blankPage);

            continueThreads();

            try{
                if(übersichtsFrame != null) {
                    if(mode == ErzählerFrameMode.ersteNacht) {
                        übersichtsFrame.übersichtsPage = übersichtsFrame.pageFactory.generateÜbersichtsPage();
                    }
                    übersichtsFrame.refreshÜbersichtsPage();
                }
            } catch (NullPointerException e) {
                System.out.println("Übersichtsframe seems to be not there. (yet?)");
            }

        } else if (ae.getSource() == comboBox1 && ((JComboBox)ae.getSource()).hasFocus()) {
            try {
                if(spielerFrame.mode == SpielerFrameMode.dropDownText) {
                    if(spielerFrame.comboBox1Label != null && comboBox1 != null) {
                        spielerFrame.comboBox1Label.setText(comboBox1.getSelectedItem().toString());
                    }
                } else if (spielerFrame.mode == SpielerFrameMode.dropDownImage) {
                    Hauptrolle hauptrolle = Hauptrolle.findHauptrolle((String) comboBox1.getSelectedItem());
                    String imagePath = hauptrolle.getImagePath();
                    Page imagePage = spielerFrame.pageFactory.generateStaticImagePage(spielerFrame.title, imagePath);
                    spielerFrame.buildScreenFromPage(imagePage);
                    spielerFrame.mode = SpielerFrameMode.dropDownImage;
                } else if (spielerFrame.mode == SpielerFrameMode.freibierPage || spielerFrame.mode == SpielerFrameMode.listMirrorPage) {
                    Page dropDownPage = spielerFrame.pageFactory.generateDropdownPage(spielerFrame.title, 1);
                    spielerFrame.buildScreenFromPage(dropDownPage);
                    spielerFrame.comboBox1Label.setText(comboBox1.getSelectedItem().toString());
                    spielerFrame.mode = SpielerFrameMode.dropDownText;
                }
            } catch (NullPointerException e) {
                System.out.println("Combobox1 might not be initialized.");
            }
        }else if (ae.getSource() == comboBox2 && ((JComboBox)ae.getSource()).hasFocus()) {
            try {
                if(spielerFrame.mode == SpielerFrameMode.dropDownText) {
                    if(spielerFrame.comboBox2Label != null && comboBox2 != null) {
                        spielerFrame.comboBox2Label.setText(comboBox2.getSelectedItem().toString());
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Combobox2 might not be initialized.");
            }
        }else if (ae.getSource() == comboBox3 && ((JComboBox)ae.getSource()).hasFocus()) {
            try {
                if(spielerFrame.mode == SpielerFrameMode.dropDownText) {
                    if(spielerFrame.comboBox3Label != null && comboBox3 != null) {
                        spielerFrame.comboBox3Label.setText(comboBox3.getSelectedItem().toString());
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Combobox3 might not be initialized.");
            }
        } else if (ae.getSource() == nachzüglerJButton || ae.getSource() == addPlayerTxtField && mode == ErzählerFrameMode.nachzüglerSetup) {
            if(mode == ErzählerFrameMode.nachzüglerSetup) {
                if (!addPlayerTxtField.getText().equals("") && !Spieler.spielerExists(addPlayerTxtField.getText())) {
                    try {
                        if(comboBox1 != null) {
                            chosenOption1 = (String) comboBox1.getSelectedItem();
                        }

                        if(comboBox2 != null) {
                            chosenOption2 = (String) comboBox2.getSelectedItem();
                        }
                    }catch (NullPointerException e) {
                        System.out.println("some comboboxes (1,2) might not be initialized.");
                    }

                    String name = addPlayerTxtField.getText();
                    String hauptrolle = chosenOption1;
                    String nebenrolle = chosenOption2;

                    Spieler.newSpieler(name, hauptrolle, nebenrolle);

                    addPlayerTxtField.setText("");

                    mode = ErzählerFrameMode.getPhaseMode();
                    buildScreenFromPage(pageFactory.generateDefaultDayPage());

                    if(übersichtsFrame != null) {
                        übersichtsFrame.refreshÜbersichtsPage();
                    }
                }
            } else {
                mode = ErzählerFrameMode.nachzüglerSetup;

                spielerFrame.mode = SpielerFrameMode.blank;
                buildScreenFromPage(pageFactory.generateNachzüglerPage());
            }
        } else if (ae.getSource() == umbringenJButton) {
            if(mode == ErzählerFrameMode.umbringenSetup) {
                try {
                    if(comboBox1 != null) {
                        chosenOption1 = (String) comboBox1.getSelectedItem();
                    }
                }catch (NullPointerException e) {
                    System.out.println("combobox1 might not be initialized.");
                }
                Spieler spieler = Spieler.findSpieler(chosenOption1);

                if(spieler!=null) {
                    Tag.umbringenSpieler = spieler;
                    Tag.umbringenButton = true;
                } else {
                    buildScreenFromPage(pageFactory.generateDefaultDayPage());
                }

                mode = ErzählerFrameMode.getPhaseMode();

                if(übersichtsFrame != null) {
                    übersichtsFrame.refreshÜbersichtsPage();
                }

                if(spieler!=null) {
                    continueThreads();
                }
            } else {
                mode = ErzählerFrameMode.umbringenSetup;

                spielerFrame.mode = SpielerFrameMode.blank;
                buildScreenFromPage(pageFactory.generateUmbringenPage());
            }
        } else if (ae.getSource() == priesterJButton) {
            if(mode == ErzählerFrameMode.priesterSetup) {
                try {
                    if(comboBox1 != null) {
                        chosenOption1 = (String) comboBox1.getSelectedItem();
                    }

                    if(comboBox2 != null){
                        chosenOption2 = (String) comboBox2.getSelectedItem();
                    }
                }catch (NullPointerException e) {
                    System.out.println("comboboxes(1,2) might not be initialized.");
                }
                String priester = chosenOption1;
                String spieler = chosenOption2;
                Tag.bürgen(priester, spieler);

                mode = ErzählerFrameMode.getPhaseMode();
                buildScreenFromPage(pageFactory.generateDefaultDayPage());
            } else {
                mode = ErzählerFrameMode.priesterSetup;

                spielerFrame.mode = SpielerFrameMode.blank;
                buildScreenFromPage(pageFactory.generatePriesterPage());
            }
        }else if (ae.getSource() == richterinJButton) {
            if(mode == ErzählerFrameMode.richterinSetup) {
                try {
                    if(comboBox1 != null) {
                        chosenOption1 = (String) comboBox1.getSelectedItem();
                    }

                    if(comboBox2 != null){
                        chosenOption2 = (String) comboBox2.getSelectedItem();
                    }
                }catch (NullPointerException e) {
                    System.out.println("comboboxes(1,2) might not be initialized.");
                }

                String richterin = chosenOption1;
                String spieler = chosenOption2;
                Tag.verurteilen(richterin, spieler);

                mode = ErzählerFrameMode.getPhaseMode();
                buildScreenFromPage(pageFactory.generateDefaultDayPage());
            } else {
                mode = ErzählerFrameMode.richterinSetup;

                spielerFrame.mode = SpielerFrameMode.blank;
                buildScreenFromPage(pageFactory.generateRichterinPage());
            }
        } else if(ae.getSource() == respawnFramesJButton) {
            respawnFrames();
        }
    }

    public void respawnFrames(){
        spielerFrame.dispatchEvent(new WindowEvent(spielerFrame, WindowEvent.WINDOW_CLOSING));
        übersichtsFrame.dispatchEvent(new WindowEvent(übersichtsFrame, WindowEvent.WINDOW_CLOSING));

        int spielerFrameMode = spielerFrame.mode;
        savePage = spielerFrame.currentPage;
        spielerFrame = new SpielerFrame(this);
        spielerFrame.mode = spielerFrameMode;
        spielerFrame.buildScreenFromPage(savePage);

        übersichtsFrame = new ÜbersichtsFrame(this);

        FrontendControl.spielerFrame = spielerFrame;
        if(PhaseMode.phase == PhaseMode.tag || PhaseMode.phase == PhaseMode.freibierTag) {
            spielerFrame.generateDayPage();
        } else if (PhaseMode.phase == PhaseMode.nacht || PhaseMode.phase == PhaseMode.ersteNacht) {
            spielerFrame.buildScreenFromPage(savePage);
        }
    }

    public void continueThreads(){
        try {
            if (mode == ErzählerFrameMode.ersteNacht) {
                synchronized (ErsteNacht.lock) {
                    ErsteNacht.lock.notify();
                }
            } else if (mode == ErzählerFrameMode.tag || mode == ErzählerFrameMode.freibierTag || mode == ErzählerFrameMode.umbringenSetup) {
                synchronized (Tag.lock) {
                    Tag.lock.notify();
                }
            } else if (mode == ErzählerFrameMode.nacht) {
                synchronized (Nacht.lock) {
                    Nacht.lock.notify();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Somehting went wrong with the Phases. (phasemode might be set wrong)");
        }
    }

    public ArrayList<Spieler> getPlayersUnspecified() {
        ArrayList<Spieler> playersUnspecified = (ArrayList)Spieler.spieler.clone();
        playersUnspecified.removeAll(playersSpecified);
        return playersUnspecified;
    }

    public ArrayList<String> getPlayersUnspecifiedStrings() {
        ArrayList<String> playersUnspecifiedStrings = new ArrayList<>();

        for(Spieler spieler : getPlayersUnspecified()) {
            playersUnspecifiedStrings.add(spieler.name);
        }

        return playersUnspecifiedStrings;
    }

    public ArrayList<Hauptrolle> getMainRolesSpecified() {
        ArrayList<Hauptrolle> mainRolesSpecified = new ArrayList<>();

        for(Spieler spieler : playersSpecified) {
            mainRolesSpecified.add(spieler.hauptrolle);
        }

        return mainRolesSpecified;
    }

    public ArrayList<String> getMainRolesSpecifiedStrings() {
        ArrayList<String> mainRolesSpecifiedStrings = new ArrayList<>();

        for(Hauptrolle hauptrolle : getMainRolesSpecified()) {
            if(hauptrolle!=null) {
                mainRolesSpecifiedStrings.add(hauptrolle.getName());
            }
        }

        return mainRolesSpecifiedStrings;
    }

    public ArrayList<Hauptrolle> getMainRolesUnspecified() {
        ArrayList<Hauptrolle> mainRolesUnspecified = (ArrayList)Hauptrolle.mainRolesInGame.clone();

        myRemoveAllHauptrollen(mainRolesUnspecified, getMainRolesSpecified());

        return mainRolesUnspecified;
    }

    public ArrayList<String> getMainRolesUnspecifiedStrings() {
        ArrayList<String> mainRolesUnspecifiedStrings = new ArrayList<>();

        for(Hauptrolle hauptrolle : getMainRolesUnspecified()) {
            mainRolesUnspecifiedStrings.add(hauptrolle.getName());
        }

        return mainRolesUnspecifiedStrings;
    }

    public ArrayList<Nebenrolle> getSecondaryRolesSpecified() {
        ArrayList<Nebenrolle> secondaryRolesSpecified = new ArrayList<>();

        for(Spieler spieler : playersSpecified) {
            secondaryRolesSpecified.add(spieler.nebenrolle);
        }

        return secondaryRolesSpecified;
    }

    public ArrayList<String> getSecondaryRoleSpecifiedStrings() {
        ArrayList<String> secondaryRolesSpecifiedStrings = new ArrayList<>();

        for(Nebenrolle nebenrolle : getSecondaryRolesSpecified()) {
            if(nebenrolle!=null) {
                secondaryRolesSpecifiedStrings.add(nebenrolle.getName());
            }
        }

        return secondaryRolesSpecifiedStrings;
    }

    public ArrayList<Nebenrolle> getSecondaryRolesUnspecified() {
        ArrayList<Nebenrolle> secondaryRolesUnspecified = (ArrayList)Nebenrolle.secondaryRolesInGame.clone();

        myRemoveAllNebenrollen(secondaryRolesUnspecified, getSecondaryRolesSpecified());

        return secondaryRolesUnspecified;
    }

    public ArrayList<String> getSecondaryRolesUnspecifiedStrings() {
        ArrayList<String> secondaryRolesUnspecifiedStrings = new ArrayList<>();

        for(Nebenrolle nebenrolle : getSecondaryRolesUnspecified()) {
            secondaryRolesUnspecifiedStrings.add(nebenrolle.getName());
        }

        return secondaryRolesUnspecifiedStrings;
    }

    public ArrayList<Hauptrolle> myRemoveAllHauptrollen(ArrayList<Hauptrolle> list, ArrayList<Hauptrolle> elementsToRemove) {
        for(Hauptrolle hauptrolle : elementsToRemove){
            int index = list.indexOf(hauptrolle);
            list.remove(index);
        }

        return list;
    }

    public ArrayList<Nebenrolle> myRemoveAllNebenrollen(ArrayList<Nebenrolle> list, ArrayList<Nebenrolle> elementsToRemove) {
        for(Nebenrolle nebenrolle : elementsToRemove){
            int index = list.indexOf(nebenrolle);
            list.remove(index);
        }

        return list;
    }
}
