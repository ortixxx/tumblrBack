package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;

public class Interface extends JFrame implements ActionListener {
    private JLabel tumblrLabel;
    private JTextField tumblr;
    private JButton playButton;
    private static JTextArea jTextArea;
    private JScrollPane jScrollPane;
    private JMenuBar jMenuBar;
    private JMenu help, donate, file;
    private JMenuItem about, buyMeACoffee, exit;
    private About aboutDialog;
    private Desktop desktop;

    public Interface(){
        super("Tumblr Backup by Luis Ortiz");
        buildInterface();
        buildActions();
    }

    private void buildInterface(){
        tumblrLabel = new JLabel("Input your tumblr username");
        tumblrLabel.setBounds(5, 5, 200, 27);
        add(tumblrLabel);

        tumblr = new JTextField("0rtix");
        tumblr.setBounds(205, 5, 195, 27);
        tumblr.selectAll();
        add(tumblr);

        playButton = new JButton("Play");
        playButton.setBounds(405, 5, 80, 27);
        add(playButton);

        jTextArea = new JTextArea("");
        jTextArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) jTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setBounds(5, 37, 480, 200);
        add(jScrollPane);

        file = new JMenu("File");
        exit = new JMenuItem("Exit");
        donate = new JMenu("Donate");
        buyMeACoffee = new JMenuItem("Buy me a coffee");
        help = new JMenu("Help");
        about = new JMenuItem("About");

        file.setMnemonic(KeyEvent.VK_F);
        exit.setMnemonic(KeyEvent.VK_E);
        donate.setMnemonic(KeyEvent.VK_D);
        buyMeACoffee.setMnemonic(KeyEvent.VK_B);
        help.setMnemonic(KeyEvent.VK_H);
        about.setMnemonic(KeyEvent.VK_A);

        jMenuBar = new JMenuBar();
        jMenuBar.add(file);
        file.add(exit);
        jMenuBar.add(donate);
        donate.add(buyMeACoffee);
        jMenuBar.add(help);
        help.add(about);

        aboutDialog = new About();

        setSize(490,265);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(jMenuBar);
        setVisible(true);
    }

    private void buildActions(){
        desktop = java.awt.Desktop.getDesktop();

        tumblr.addActionListener(this);
        playButton.addActionListener(this);

        exit.addActionListener(this);
        buyMeACoffee.addActionListener(this);
        about.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exit){
            int option = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Warning", JOptionPane.YES_NO_OPTION);
            if (option == 0){
                System.exit(0);
            }

            return;
        }

        if(e.getSource()==buyMeACoffee){
            try{
                desktop.browse(java.net.URI.create("https://www.paypal.me/ortixxx"));
            }catch(Exception err){
                JOptionPane.showMessageDialog(null,"Error: "+err);
            }

            return;
        }

        if(e.getSource()==about){
            aboutDialog.showAbout();
            return;
        }

        if (e.getSource()==tumblr || e.getSource()==playButton ) {
            play();
        }
    }

    private void play(){
        Crawler.clearVector();
        jTextArea.setText("");
        String URL = "http://"+tumblr.getText()+".tumblr.com";
        int option = JOptionPane.showConfirmDialog(null, URL+" is correct?", "Play!", JOptionPane.YES_NO_OPTION);
        if (option != 0){
            return;
        }

        tumblr.setEnabled(false);
        playButton.setEnabled(false);

        int i = 1;
        Crawler crawler;
        while(true){
            crawler = new Crawler(URL+"/page/"+i);
            try {
                crawler.crawl();
            } catch (Exception e) {
                jTextArea.append("Error in : "+URL+"/page/"+i+"\n");
                break;
            }
            i++;
        }

        JOptionPane.showMessageDialog(null,"Done ~ ");
        tumblr.setEnabled(true);
        playButton.setEnabled(true);
        tumblr.selectAll();
    }

    public static JTextArea getjTextArea() {
        return jTextArea;
    }

    public static void main(String[]args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Crawler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Crawler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Crawler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Crawler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        new Interface();
    }
}
