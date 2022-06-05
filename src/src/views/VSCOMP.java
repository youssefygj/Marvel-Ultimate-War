package views;

import engine.Game;
import engine.Player;
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
import java.util.Random;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class VSCOMP implements ActionListener, MouseListener {
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
    public VSCOMP() {
        z = new JFrame();
        x = new JPanel();
        z.setDefaultCloseOperation(EXIT_ON_CLOSE);
        x.setLayout(new FlowLayout());
        JLabel enter1 = new JLabel("First Player:");

        field1 = new JTextField(35);

        z.setBounds(500, 500, 500, 200);
        x.add(enter1);
        x.add(field1);


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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (((JButton) e.getSource()).getName().equals("test")) {
            String name1 = field1.getText();

            if (name1.equals("")) {
                JOptionPane.showMessageDialog(null, "Enter a valid name", null, JOptionPane.ERROR_MESSAGE);
                return;
            }
            JButton testnew = new JButton("Test");
            y = new JPanel();
            y.setLayout(new GridLayout(5, 3));
            Player first = new Player(name1);
            Player second = new Player("COMP");
            game = new Game(first,second );
            try {
                game.loadAbilities("Abilities.csv");
                game.loadChampions("Champions.csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < game.getAvailableChampions().size(); i++) {
                String s = ((Champion) game.getAvailableChampions().get(i)).getName();
                ImageIcon icon = new ImageIcon(s + ".png");
                Image img = icon.getImage();
                Image newimg = img.getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
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


        }
        else if (((JButton) e.getSource()).getName().equals("fpossibleleaders")) {
            for (int i = 0; i < game.getFirstPlayer().getTeam().size(); i++) {
                if (game.getFirstPlayer().getTeam().get(i).getName().equals(((JButton) e.getSource()).getText())) {
                    game.getFirstPlayer().setLeader(game.getFirstPlayer().getTeam().get(i));
                    sleaderselection.setLayout(null);
                    int loc = 660;
                    Random random = new Random();
                    int randomindex= random.nextInt(2);
                    game.getSecondPlayer().setLeader(game.getSecondPlayer().getTeam().get(randomindex));
                    z.remove(leaderselection);
                    gameController x = new gameController(game, z, sleaderselection,true);
                }
            }
        }
        else {
            JButton a = (JButton) e.getSource();
            int i = buttons.indexOf(a);
            Champion current = game.getAvailableChampions().get(i);
            if (c < 3) {
                game.getFirstPlayer().getTeam().add(current);
                c++;
            }
            buttons.remove(a);
            a.setEnabled(false);
            game.getAvailableChampions().remove(current);
            if (c == 3){
                for(int l=0;l<3;l++){
                    Random random = new Random();
                    int randomindex= random.nextInt(game.getAvailableChampions().size()-1 );
                    game.getSecondPlayer().getTeam().add(game.getAvailableChampions().get(randomindex));
                    game.getAvailableChampions().remove(randomindex);
                    c++;
                }

            }


            if (c == 6) {
                leaderselection.setLayout(null);
                int loc = 660;
                for (int j = 0; j < game.getFirstPlayer().getTeam().size(); j++) {
                    String s = ((Champion) game.getFirstPlayer().getTeam().get(j)).getName();
                    ImageIcon icon = new ImageIcon(s + ".png");
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(100, 80, java.awt.Image.SCALE_SMOOTH);
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
            lmao = new JTextArea("First Player Hover over the buttons to see the stats of each champion" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n");
        else
            lmao = new JTextArea("Second Player Hover over the buttons to see the stats of each champion" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n");
        lmao.setEditable(false);
        z.add(lmao, BorderLayout.SOUTH);

        z.revalidate();
        z.repaint();
    }
}
