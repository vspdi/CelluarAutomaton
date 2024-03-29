package cellularautomaton.view;

import cellularautomaton.controller.locale.*;
import cellularautomaton.event.AutomatonEventEnum;
import cellularautomaton.model.CellularAutomaton;
import cellularautomaton.view.gui.basicview.components.CAJScrollPane;
import cellularautomaton.view.gui.basicview.menu.CAJPopupMenu;
import cellularautomaton.view.gui.basicview.windows.*;
import cellularautomaton.view.gui.basicview.footer.CAFooter;
import cellularautomaton.view.gui.basicview.menu.CAMenuBar;
import cellularautomaton.view.gui.basicview.states.*;
import cellularautomaton.view.gui.basicview.toolbar.CAToolbar;
import cellularautomaton.view.gui.basicview.windows.editor.CAAutomatonEditorWindow;
import cellularautomaton.view.util.IOwnEnumeration;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by Viktor Spadi on 14.10.2015.
 * Basic implementation of the interface AbstractAutomatonView
 */
public class AutomatonView implements IOwnEnumeration, Observer {
    // Attributes //////////////////////////////////////////////////////////////////////////////////////////////////////
    private StringController stringController;
    private JFrame frame;
    private CAMenuBar menuBar;
    private CAToolbar toolbar;
    private CAFooter footer;
    private CAStateContainer stateContainer;
    private JScrollPane stateScrollPane;
    private CAPopulationContainer populationContainer;
    private CAJScrollPane populationScrollPane;
    private CellularAutomaton automaton;
    private CAChangeSizeWindow changeSizeWindow;
    private CAChangeCellColorWindow changeCellColorWindow;
    private CAJPopupMenu popupMenu;
    private CAJAutomatonClassChooser automatonClassChooser;
    private CANewAutomatonWindow newAutomatonWindow;
    private CAAutomatonEditorWindow automatonEditorWindow;

    public void setModel(CellularAutomaton automaton) {
        this.automaton = automaton;
        this.populationContainer.setColorMapping(this.automaton.getColorMapping());
        this.repaint();
        this.toolbar.getTorusButton().setSelected(this.automaton.isTorus());
    }

    public CAChangeSizeWindow getChangeSizeWindow() {
        return changeSizeWindow;
    }

    public CAChangeCellColorWindow getChangeCellColorWindow() {
        return changeCellColorWindow;
    }

    public JFrame getFrame() {
        return frame;
    }

    public CAMenuBar getMenuBar() {
        return menuBar;
    }

    public CAToolbar getToolbar() {
        return toolbar;
    }

    public CAFooter getFooter() {
        return footer;
    }

    public CAStateContainer getStateContainer() {
        return stateContainer;
    }

    public JScrollPane getStateScrollPane() {
        return stateScrollPane;
    }

    public CAPopulationContainer getPopulationContainer() {
        return populationContainer;
    }

    public JScrollPane getPopulationScrollPane() {
        return populationScrollPane;
    }

    public CAJPopupMenu getPopupMenu() { return popupMenu; }

    public CAJAutomatonClassChooser getAutomatonClassChooser() { return automatonClassChooser; }

    public CANewAutomatonWindow getNewAutomatonWindow() { return newAutomatonWindow; }

    public CAAutomatonEditorWindow getAutomatonEditorWindow() { return automatonEditorWindow; }


    // Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Constructor
     */
    public AutomatonView() {
        super();
        this.stringController = StringController.getInstance();
        init();
    }

    /*
     * Mehtod to show the window
     */
    public void open() {
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /*
     * Method to initialize all components
     */
    public void init() {
        // Create Components
        this.frame = new JFrame(this.stringController.get(StringEnumeration.WINDOW_TITLE));
        this.menuBar = new CAMenuBar(this.stringController);
        this.toolbar = new CAToolbar(StringController.getInstance());
        this.footer = new CAFooter();
        this.populationContainer = new CAPopulationContainer();
        this.populationScrollPane = new CAJScrollPane(this.populationContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.populationContainer.setScrollPane(this.populationScrollPane);

        this.stateContainer = new CAStateContainer();
        this.stateScrollPane = new JScrollPane(this.stateContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setJMenuBar(this.menuBar);

        // Create additional Windows
        this.changeSizeWindow = new CAChangeSizeWindow();
        this.changeSizeWindow.setParentFrame(this.frame);
        this.changeCellColorWindow = new CAChangeCellColorWindow();
        this.popupMenu = new CAJPopupMenu();
        this.automatonClassChooser = new CAJAutomatonClassChooser();
        this.newAutomatonWindow = new CANewAutomatonWindow();
        this.newAutomatonWindow.setParentFrame(this.frame);
        this.automatonEditorWindow = new CAAutomatonEditorWindow();
        this.automatonEditorWindow.setParentFrame(this.frame);

        // Add components
        this.frame.add(this.toolbar, BorderLayout.PAGE_START);
        this.frame.add(this.footer, BorderLayout.SOUTH);
        this.frame.add(this.stateScrollPane, BorderLayout.WEST);
        this.frame.add(this.populationScrollPane, BorderLayout.CENTER);
        this.frame.setPreferredSize(new Dimension(850,600));
    }

    public void repaint() {
        this.getPopulationContainer().drawPopulation(this.automaton.getPopulation());
    }

    @Override
    public void update(Observable o, Object arg) {
        CellularAutomaton automaton = (CellularAutomaton)o;
        AutomatonEventEnum evt = (AutomatonEventEnum)arg;
        switch (evt) {
            case CLOSE:
                break;
            case TORUS_CHANGED:
                this.toolbar.getTorusButton().setSelected(automaton.isTorus());
                break;
            case NEW_AUTOMATON:
                this.toolbar.getTorusButton().setSelected(automaton.isTorus());
                this.populationContainer.setColorMapping(this.automaton.getColorMapping());
                this.populationContainer.fitPopulation();
                repaint();
                break;
            case SIZE_CHANGED:
                // Fix-> dont know why but i have to...
                this.populationContainer.fitPopulation();
                repaint();
                this.populationContainer.fitPopulation();
                repaint();
                break;
            case CELL_CHANGED:
                repaint();
                break;
            case COLOR_CHANGED:
                this.stateContainer.setColorMappting(this.automaton.getColorMapping());
                this.populationContainer.setColorMapping(this.automaton.getColorMapping());
                this.populationContainer.fitPopulation();
                repaint();
                break;
            case INTERVAL_CHANGED:
                break;
            case SIMSTATE_CHANGED:
                this.toolbar.updateSimulationState(this.automaton.isRunning());
                this.menuBar.updateSimulationState(this.automaton.isRunning());
                break;
            default:
                System.out.printf("Undhandled Update from Model %s", evt);
        }
    }

    @Override
    public StringEnumeration getEnumeration() {
        return StringEnumeration.CA_MAINWINDOW;
    }
}
