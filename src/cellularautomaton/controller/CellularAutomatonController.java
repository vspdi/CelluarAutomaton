package cellularautomaton.controller;

import cellularautomaton.controller.locale.StringEnumeration;
import cellularautomaton.model.CellularAutomaton;
import cellularautomaton.view.AutomatonView;
import cellularautomaton.view.util.IOwnEnumeration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

/**
 * Created by Viktor Spadi on 14.10.2015.
 *
 *
 */
public class CellularAutomatonController extends AbstractController<AutomatonView, Object> {
    // Attributes //////////////////////////////////////////////////////////////////////////////////////////////////////
    private MenuController menuController;
    private StateController stateController;
    private ToolbarController toolbarController;
    private PoppulationController poppulationController;
    private PopupController popupController;
    private AutomatonLoaderController automatonLoaderController;

    public MenuController getMenuController() {
        return menuController;
    }

    public StateController getStateController() {
        return stateController;
    }

    public ToolbarController getToolbarController() {
        return toolbarController;
    }

    public PoppulationController getPoppulationController() {
        return poppulationController;
    }

    public PopupController getPopupController() { return popupController; }

    public AutomatonLoaderController getAutomatonLoaderController() { return automatonLoaderController; }

    // Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    * Constructor
    *
    * @param viewController
    *
    */
    public CellularAutomatonController(AutomatonView automatonView) {
        super(automatonView, null);
        this.menuController = new MenuController(getView().getMenuBar(), this);
        this.stateController = new StateController(getView().getStateContainer(), this);
        this.toolbarController = new ToolbarController(getView().getToolbar(), this);
        this.poppulationController = new PoppulationController(getView().getPopulationContainer(), this);
        this.popupController = new PopupController(getView().getPopupMenu(), this);
        this.automatonLoaderController = new AutomatonLoaderController(getView().getAutomatonClassChooser(), this);
        bindEvents();
    }

    /*
     * Pass call to the view
     */
    public void open() {
        this.getView().open();
    }

    public void bindModel(CellularAutomaton cellularAutomaton) {
        super.bindModel(cellularAutomaton);
        this.menuController.bindModel(cellularAutomaton);
        this.stateController.bindModel(cellularAutomaton);
        this.toolbarController.bindModel(cellularAutomaton);
        this.poppulationController.bindModel(cellularAutomaton);
        this.popupController.bindModel(cellularAutomaton);
        this.automatonLoaderController.bindModel(cellularAutomaton);

        // observer binding
        this.getModel().addObserver(this.getView());

        // additional suff like first cell obtain
        this.stateController.bindStates();
        this.getView().setModel(this.getModel());
    }

    public void bindEvents() {
        // Terminate
        this.getView().getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                invokeClose();
            }
        });

        this.getView().getToolbar().getSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                getModel().setSimInterval(((JSlider)e.getSource()).getValue());
            }
        });
    }

    public void invokeClose() {
        // TODO Logic for save or sth.
        getModel().terminate(false, this);
    }

    public void getNewAutomatonSize() {
        try {
            int width = Integer.parseInt(this.getView().getChangeSizeWindow().getWidthField().getText());
            int height = Integer.parseInt(this.getView().getChangeSizeWindow().getHeightField().getText());
            boolean valid = true;

            if(width <= 0) {
                this.getView().getChangeSizeWindow().getWidthField().setText("");
                valid = false;
            }
            if(height <= 0) {
                this.getView().getChangeSizeWindow().getHeightField().setText("");
                valid = false;
            }

            if(valid) {
                this.getModel().setSize(width, height);
                this.getView().getChangeSizeWindow().setVisible(false);
            }
        } catch(Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StringEnumeration enm = ((IOwnEnumeration)e.getSource()).getEnumeration();
        switch (enm) {
            case MI_QUIT:
                getModel().terminate(true, this);
                break;
            case MI_CHANGE_SIZE:
                this.getView().getChangeSizeWindow().reset();
                this.getView().getChangeSizeWindow().setVisible(true);
                this.getView().getChangeSizeWindow().toFront();
                break;
            case W_CHANGED_SIZE:
                getNewAutomatonSize();
                break;
            case MI_RANDOM:
                this.getModel().randomPopulation();
                break;
            case MI_TORUS:
                this.getModel().setTorus(!this.getModel().isTorus());
                break;
            case MI_DELETE:
                this.getModel().clearPopulation();
                break;
            case MI_ZOOM_IN:
                this.getView().getPopulationContainer().increaseCellSize();
                break;
            case MI_ZOOM_OUT:
                this.getView().getPopulationContainer().decreaseCellSize();
                break;
            case MI_STEP:
                this.getModel().nextGeneration();
                break;
            case MI_START:
                this.getModel().setRunning(true);
                break;
            case MI_STOP:
                this.getModel().setRunning(false);
                break;
            case MI_LOAD:
            case MS_LOAD:
                getAutomatonLoaderController().loadAutomaton();
                break;
            default:
                //System.out.println("Event: "+ enm.name());
        }
    }
}
