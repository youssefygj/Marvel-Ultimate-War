package views;

import engine.Game;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.world.Champion;
import model.world.Cover;
import model.world.Direction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class gameController implements ActionListener, KeyListener{
    private Game game;
    private JFrame frame;
    private JPanel removal;
    private JPanel board = new JPanel();
    private JPanel covers = new JPanel();
    private JPanel champions = new JPanel(new BorderLayout());
    private JPanel actions = new JPanel();
    private JButton[][] buttons = new JButton[5][5];
    private JTextArea info = new JTextArea();
    private JTextArea title = new JTextArea();
    private JButton attack;
    private JTextArea turnorder=new JTextArea();
    private JButton move;
    private boolean pressed;
    private boolean pressedmove;
    public gameController(Game game, JFrame frame, JPanel removal) {
        this.game = new Game(game.getFirstPlayer(), game.getSecondPlayer());
        this.frame = frame;
        this.frame.remove(removal);
        board.setLayout(new GridLayout(5, 5));
        attack=new JButton("Attack");
        actions.setLayout(new BorderLayout());
        attack.setName("attack");
        attack.addActionListener(this);
        move=new JButton("move");
        move.setName("move");
        move.addActionListener(this);
        move.addKeyListener(this);
        actions.add(attack,BorderLayout.CENTER);
        actions.add(move,BorderLayout.NORTH);
        this.frame.add(actions,BorderLayout.EAST);
        this.frame.addKeyListener(this);
        this.frame.setFocusable(true);
        turnorder.setEditable(false);

        this.frame.add(turnorder,BorderLayout.SOUTH);
        attack.addKeyListener(this);
        actions.addKeyListener(this);
        this.game.placeChampions();

        this.frame.setVisible(true);
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
        this.frame.add(board);
        turnorder.setText(this.game.getTurnOrder().toString());
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
        if (((JButton) e.getSource()).getName().equals("attack")){
            pressed=true;

        }
        if (((JButton) e.getSource()).getName().equals("move")){
            pressedmove=true;

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar()=='w' &&pressed){

            try {
                this.game.attack(Direction.UP);
                System.out.println("LOL");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            }
        }
        if (e.getKeyChar()=='s' &&pressed){
            try {
                this.game.attack(Direction.DOWN);

            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            }
        }
        if (e.getKeyChar()=='a' &&pressed){
            try {
                this.game.attack(Direction.LEFT);
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            }
        }
        if (e.getKeyChar()=='d' &&pressed){
            try {
                this.game.attack(Direction.RIGHT);
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
            }
        }
        if (e.getKeyChar()=='w' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            JButton temp;
            Point test=this.game.getCurrentChampion().getLocation();
            temp=buttons[test.x][test.y];
            try {
                this.game.move(Direction.UP);
                System.out.println("LOL");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
             catch (UnallowedMovementException ex) {
                 JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                 return;
            }
            buttons[test.x+1][test.y]=temp;
            buttons[test.x][test.y]=new JButton();
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.remove(buttons[i][j]);
                }
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='s' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            try {
                this.game.move(Direction.DOWN);

            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            catch (UnallowedMovementException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.remove(buttons[i][j]);
                }
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='a' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            try {
                this.game.move(Direction.LEFT);
                System.out.println("LOL");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            catch (UnallowedMovementException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.remove(buttons[i][j]);
                }
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='d' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            try {
                this.game.move(Direction.RIGHT);
                System.out.println("LOL");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            catch (UnallowedMovementException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.remove(buttons[i][j]);
                }
            }
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        frame.repaint();
        frame.revalidate();
        board.repaint();
        board.revalidate();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressed=false;
        pressedmove=false;
    }
}
