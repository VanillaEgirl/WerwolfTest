package root.Frontend.Utils.PageRefresher.InteractivePages;

import root.Frontend.Page.PageTable;
import root.Frontend.Utils.PageRefresher.InteractivePages.InteractiveElementsDtos.RolePageElementsDto;
import root.Frontend.Utils.PageRefresher.Models.RefreshedPage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class RolePage extends RefreshedPage {
    RolePageElementsDto interactiveElementsDto;

    JLabel counterLabel;
    private PageTable roleButtonTable;
    List<JButton> roleButtons;
    PageTable labelTable;
    PageTable deleteTable;
    List<JButton> deleteButtons = new ArrayList<>();
    private JButton addAllRolesButton;
    private JButton next;
    private JButton back;

    @Override
    protected void setupObjects() {
        roleButtons = new ArrayList<>();
        roleButtonTable = new PageTable();
        counterLabel = new JLabel();
        labelTable = new PageTable();
        deleteTable = new PageTable();
        addAllRolesButton = new JButton();
        next = new JButton();
        back = new JButton();
    }

    @Override
    public void processActionEvent(ActionEvent ae) {
        if (next.equals(ae.getSource())) {
            next();
        } else if (back.equals(ae.getSource())) {
            erzählerFrame.prevPage();
        } else if (roleButtons.contains(ae.getSource())) {
            addRolle(ae);
        } else if (addAllRolesButton.equals(ae.getSource())) {
            addAllRollen();
            refresh();
        } else if (deleteButtons.contains(ae.getSource())) {
            deleteRolle(ae);
        }
    }

    protected void next() {
        erzählerFrame.nextPage();
    }

    @Override
    protected void setupPageElementsDtos() {
        interactiveElementsDto = new RolePageElementsDto(counterLabel, roleButtonTable, roleButtons, labelTable, deleteTable,
                addAllRolesButton, next, back);
    }

    String getCounterLabelText(int numberOfSpieler, int numberOfRollen) {
        return pageFactory.pageElementFactory.generateCounterLabelString(numberOfSpieler, numberOfRollen);
    }

    public void refresh() {
        pageRefresher.refreshPage();
        if (spielerFrame != null) {
            refreshSpielerFrame();
        }
    }

    protected void refreshSpielerFrame() {
    }

    protected void addRolle(ActionEvent ae) {
    }

    protected void deleteRolle(ActionEvent ae) {
    }

    protected void addAllRollen() {
    }
}
