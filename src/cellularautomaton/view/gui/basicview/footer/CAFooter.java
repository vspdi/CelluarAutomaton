package cellularautomaton.view.gui.basicview.footer;

import cellularautomaton.controller.locale.StringEnumeration;
import cellularautomaton.view.util.IOwnEnumeration;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Viktor Spadi on 17.10.2015.
 */
public class CAFooter extends JPanel implements IOwnEnumeration{
    private JLabel infoLabel;

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public CAFooter() {
        super();

        // Componentstyle
        setBackground(Color.decode("0xDCFAFA"));
        this.infoLabel = new JLabel();
        this.infoLabel.setText("Herzlich Willkommen");
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add components
        add(this.infoLabel);
    }

    @Override
    public StringEnumeration getEnumeration() {
        return StringEnumeration.CA_FOOTER;
    }
}
