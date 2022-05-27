package views;

import engine.Game;
import model.world.Champion;
import model.world.Cover;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class gameController implements ActionListener {
    private Game game;
    private JFrame frame;
    private JPanel removal;
    private JPanel board = new JPanel();
    private JPanel covers = new JPanel();
    private JPanel champions = new JPanel(new BorderLayout());
    private JPanel actions = new JPanel();
    private JButton[][] buttons = new JButton[5][5];
    JTextArea info = new JTextArea();
    JTextArea title = new JTextArea();

    public gameController(Game game, JFrame frame, JPanel removal) {
        this.game = new Game(game.getFirstPlayer(), game.getSecondPlayer());
        this.frame = frame;
        this.frame.remove(removal);
        board.setLayout(new GridLayout(5, 5));

        this.game.placeChampions();
        for (int i = 0; i < this.game.getBoard().length; i++) {
            for (int j = 0; j < this.game.getBoard().length; j++) {
                if (this.game.getBoard()[i][j] == null) {
                    JButton bt = new JButton();
                    bt.setName("null");
                    bt.addActionListener(this);
                    board.add(bt);
                    buttons[i][j] = bt;
                } else if (this.game.getBoard()[i][j] instanceof Cover) {
                    ImageIcon icon = new ImageIcon("Cover.png");
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(newimg);
                    JButton cover = new JButton(icon);
                    cover.setName("cover");
                    cover.addActionListener(this);
                    buttons[i][j] = cover;
                    board.add(cover);
                } else if (this.game.getBoard()[i][j] instanceof Champion) {
                    String s = ((Champion) this.game.getBoard()[i][j]).getName();
                    ImageIcon icon = new ImageIcon(s + ".png");
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(newimg);
                    JButton champ = new JButton(icon);
                    champ.setName("Champion");
                    champ.addActionListener(this);
                    buttons[i][j] = champ;
                    board.add(champ);
                }
            }
        }
        frame.add(board);
        this.frame.revalidate();
        this.frame.repaint();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                if (buttons[i][j] == (JButton) e.getSource()) {
                    x = i;
                    y = j;
                }
            }
        }
        if (((JButton) e.getSource()).getName().equals("cover")) {
            Cover s = (Cover) game.getBoard()[x][y];
            JOptionPane.showMessageDialog(null, "Health = " + s.getCurrentHP(), null, JOptionPane.PLAIN_MESSAGE);

        }
        if (((JButton) e.getSource()).getName().equals("Champion")) {
            champions.remove(info);
            this.frame.remove(champions);
            title.setText("Champion Stats");
            title.setFont(new Font("Arial", Font.BOLD, 18));
            champions.add(title,BorderLayout.NORTH);
            String r = ((Champion) (game.getBoard()[x][y])).toString();
            for (int i = 0; i < ((Champion) (game.getBoard()[x][y])).getAppliedEffects().size(); i++) {
                r += "Name: " + ((Champion) (game.getBoard()[x][y])).getAppliedEffects().get(i).getName() + "\n"
                        + "Duration = " + ((Champion) (game.getBoard()[x][y])).getAppliedEffects().get(i).getDuration() + "\n" + "\n";
            }
            info.setText(r);
            info.setEditable(false);
            champions.add(info, BorderLayout.CENTER);
            this.frame.add(champions, BorderLayout.WEST);
            this.frame.revalidate();
            this.frame.repaint();
        }
    }
}
