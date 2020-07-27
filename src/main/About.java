package main;

import javax.swing.*;
import java.awt.*;

public class About extends JDialog {

    public About() {
        super(Interface.getFrames()[0], true);

        JLabel label = new JLabel("    Developed by Luis Ortiz");
        JLabel label1 = new JLabel("    Email: leo_spider2@hotmail.com");
        JLabel label2 = new JLabel("    Twitter: ortix_x");
        setTitle("About");
        setLayout(new GridLayout(3,0));
        setSize(300, 120);
        add(label);
        add(label1);
        add(label2);

        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void showAbout() {
        setVisible(true);
    }
}
