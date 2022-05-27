package views;

import engine.Game;
import engine.Player;
import engine.gameListener;
import model.world.Champion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class startController implements ActionListener, MouseListener {
    private JTextField field1;
    private JTextField field2;
    private JFrame z;
    private JPanel x;
    private JPanel y;
    private JPanel leaderselection = new JPanel();
    private JPanel sleaderselection = new JPanel();
    private Game game;
    private JTextArea lmao = new JTextArea();
    private JTextArea stats;
    private JFrame l = new JFrame();
    private int c = 0;
    private ArrayList<JButton> buttons = new ArrayList<>();
    private ArrayList<JButton> buttons1 = new ArrayList<>();

    public startController() {
        z = new JFrame();

        x = new JPanel();
        z.setDefaultCloseOperation(EXIT_ON_CLOSE);
        x.setLayout(new FlowLayout());
        JLabel enter1 = new JLabel("First Player:");
        JLabel enter2 = new JLabel("Second Player:");
        field1 = new JTextField(35);
        field2 = new JTextField(35);
        z.setBounds(500, 500, 500, 200);
        x.add(enter1);
        x.add(field1);
        x.add(enter2);
        x.add(field2);
        z.add(x, BorderLayout.CENTER);
        z.setVisible(true);
        z.setResizable(false);
        x.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton test = new JButton("DONE!");
        test.setName("test");
        test.addActionListener(this);
        x.add(test, BorderLayout.SOUTH);
        x.revalidate();
        x.repaint();
    }

    public static void main(String[] args) {
        startController s = new startController();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (((JButton) e.getSource()).getName().equals("test")) {
            String name1 = field1.getText();
            String name2 = field2.getText();
            JButton testnew = new JButton("Test");
            y = new JPanel();
            y.setLayout(new GridLayout(5, 3));
            Player first = new Player(name1);
            Player second = new Player(name2);
            game = new Game(first, second);
            try {
                game.loadAbilities("Abilities.csv");
                game.loadChampions("Champions.csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < game.getAvailableChampions().size(); i++) {
                String s = ((Champion)game.getAvailableChampions().get(i)).getName();
                ImageIcon icon = new ImageIcon(s+".png");
                Image img = icon.getImage() ;
                Image newimg = img.getScaledInstance( 100, 80,  java.awt.Image.SCALE_SMOOTH );
                icon = new ImageIcon(newimg);
                JButton b = new JButton(icon);
                b.setFocusPainted(false);
                b.setName(game.getAvailableChampions().get(i).getName());
                b.setBorderPainted(true);
                b.setBorder(new LineBorder(Color.BLACK));
                b.addActionListener(this);
                b.setVisible(true);
                b.addMouseListener(this);
                b.setFont(new Font("Arial", Font.BOLD, 18));
                buttons.add(b);
                y.add(b);
            }
            z.remove(x);
            z.add(y, BorderLayout.CENTER);
            z.setBounds(0, 0, 1920, 1080);
            JOptionPane.showMessageDialog(null, "FirstPlayer select", null, JOptionPane.PLAIN_MESSAGE);

            z.revalidate();
            z.repaint();


        } else if (((JButton) e.getSource()).getName().equals("fpossibleleaders")) {
            for (int i = 0; i < game.getFirstPlayer().getTeam().size(); i++) {
                if (game.getFirstPlayer().getTeam().get(i).getName().equals(((JButton) e.getSource()).getText())) {
                    game.getFirstPlayer().setLeader(game.getFirstPlayer().getTeam().get(i));
                    sleaderselection.setLayout(null);
                    int loc = 660;
                    for (int j = 0; j < game.getSecondPlayer().getTeam().size(); j++) {
                        String s = ((Champion)game.getSecondPlayer().getTeam().get(j)).getName();
                        ImageIcon icon = new ImageIcon(s+".png");
                        Image img = icon.getImage() ;
                        Image newimg = img.getScaledInstance( 100, 80,  java.awt.Image.SCALE_SMOOTH );
                        icon = new ImageIcon(newimg);
                        JButton b = new JButton(game.getSecondPlayer().getTeam().get(j).getName());
                        b.setIcon(icon);
                        b.setBounds(loc, 440, 150, 150);
                        b.setName("spossibleleaders");
                        b.setBorderPainted(true);
                        b.setBorder(new LineBorder(Color.BLACK));
                        b.addActionListener(this);
                        loc = loc + 200;
                        sleaderselection.add(b);

                    }
                    JTextArea selectiontext = new JTextArea(game.getSecondPlayer().getName() + " please select your leader");
                    selectiontext.setEditable(false);
                    selectiontext.setBounds(800, 340, 10000, 10000);
                    selectiontext.setFont(new Font("Arial", Font.BOLD, 18));
                    Color color = new Color(152, 251, 152, 0);
                    selectiontext.setBackground(color);
                    sleaderselection.add(selectiontext, new GridLayout());
                    z.remove(leaderselection);
                    z.add(sleaderselection);
                    z.revalidate();
                    z.repaint();
                }
            }
        } else if (((JButton) e.getSource()).getName().equals("spossibleleaders")) {
            for (int i = 0; i < game.getSecondPlayer().getTeam().size(); i++) {
                if (game.getSecondPlayer().getTeam().get(i).getName().equals(((JButton) e.getSource()).getText())) {
                    game.getSecondPlayer().setLeader(game.getSecondPlayer().getTeam().get(i));
                }
            }
            gameController x = new gameController(game, z, sleaderselection);

        } else {
            JButton a = (JButton) e.getSource();
            int i = buttons.indexOf(a);
            Champion current = game.getAvailableChampions().get(i);
            if (c < 3) {
                game.getFirstPlayer().getTeam().add(current);
                c++;
            } else if (c < 6) {
                game.getSecondPlayer().getTeam().add(current);
                c++;
            }
            a.setEnabled(false);
            if (c == 3)
                JOptionPane.showMessageDialog(null, "SecondPlayer select", null, JOptionPane.PLAIN_MESSAGE);
            if (c == 6) {
                leaderselection.setLayout(null);
                int loc = 660;
                for (int j = 0; j < game.getFirstPlayer().getTeam().size(); j++) {
                    String s = ((Champion)game.getFirstPlayer().getTeam().get(j)).getName();
                    ImageIcon icon = new ImageIcon(s+".png");
                    Image img = icon.getImage() ;
                    Image newimg = img.getScaledInstance( 100, 80,  java.awt.Image.SCALE_SMOOTH );
                    icon = new ImageIcon(newimg);
                    JButton b = new JButton(game.getFirstPlayer().getTeam().get(j).getName());
                    b.setIcon(icon);
                    b.setBounds(loc, 440, 150, 150);
                    b.setName("fpossibleleaders");
                    b.setBorderPainted(true);
                    b.setBorder(new LineBorder(Color.BLACK));
                    b.addActionListener(this);
                    loc = loc + 200;
                    leaderselection.add(b);
                }
                JTextArea selectiontext = new JTextArea(game.getFirstPlayer().getName() + " please select your leader");
                selectiontext.setEditable(false);
                selectiontext.setBounds(800, 340, 1000, 100);
                selectiontext.setFont(new Font("Arial", Font.BOLD, 18));
                Color color = new Color(152, 251, 152, 0);
                selectiontext.setBackground(color);
                leaderselection.add(selectiontext);
                z.remove(y);
                z.remove(stats);
                z.remove(lmao);
                z.add(leaderselection);
                z.revalidate();
                z.repaint();
            }


        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JButton a = (JButton) e.getSource();
        int i = buttons.indexOf(a);
        Champion current = game.getAvailableChampions().get(i);
        z.remove(lmao);
        stats = new JTextArea(current.toString());
        stats.setEditable(false);
        z.add(stats, BorderLayout.SOUTH);
        z.revalidate();
        z.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        z.remove(stats);
        if (c < 3)
            lmao = new JTextArea("First Player Hover over the buttons to see the stats of each champion" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n");
        else
            lmao = new JTextArea("Second Player Hover over the buttons to see the stats of each champion" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n");
        lmao.setEditable(false);
        z.add(lmao, BorderLayout.SOUTH);

        z.revalidate();
        z.repaint();
    }
}
