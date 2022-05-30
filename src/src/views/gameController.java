package views;

import engine.Game;
import exceptions.*;
import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;
import model.world.Direction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class gameController implements ActionListener, KeyListener, MouseListener {
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
    private JButton endturn=new JButton("End your turn");
    private JButton cast= new JButton("Cast your ability");
    private JPanel selections= new JPanel(new GridLayout(4,1));
    private Ability temp=null;
    private int firstcoordinate;
    private int secondcoordinate;
    private boolean choosing=false;
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
        actions.add(attack,BorderLayout.CENTER);

        move=new JButton("move");
        move.setName("move");
        move.addActionListener(this);
        move.addKeyListener(this);
        actions.add(move,BorderLayout.NORTH);

        endturn.addActionListener(this);
        endturn.setName("endturn");
        actions.add(endturn,BorderLayout.SOUTH);

        cast.addActionListener(this);
        cast.setName("cast");
        actions.add(cast,BorderLayout.EAST);

        this.frame.add(actions,BorderLayout.EAST);
        this.frame.addKeyListener(this);
        this.frame.setFocusable(true);
        turnorder.setEditable(false);
        turnorder.setFont(new Font("Arial", Font.BOLD, 18));
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
                    bt.addMouseListener(this);
                    board.add(bt);
                    buttons[i][j] = bt;

                } else if (this.game.getBoard()[i][j] instanceof Cover) {
                    ImageIcon icon = new ImageIcon("Cover.png");
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    icon = new ImageIcon(newimg);
                    JButton cover = new JButton(icon);
                    cover.addMouseListener(this);
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
                    champ.addMouseListener(this);
                    champ.setName("Champion");
                    champ.addActionListener(this);
                    buttons[i][j] = champ;
                    board.add(champ);
                }
            }
        }
        this.frame.add(board);
        turnorder.setText(this.game.getTurnOrder().toString());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            if(choosing){
                firstcoordinate=x;
                secondcoordinate=y;
                choosing=false;
            }
            Cover s = (Cover) game.getBoard()[x][y];
            JOptionPane.showMessageDialog(null, "Health = " + s.getCurrentHP(), null, JOptionPane.PLAIN_MESSAGE);
        }
        if (((JButton) e.getSource()).getName().equals("Champion")) {
            if(choosing){
                firstcoordinate=x;
                secondcoordinate=y;
                choosing=false;
            }
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
        if (((JButton) e.getSource()).getName().equals("null")){
            if(choosing){
                firstcoordinate=x;
                secondcoordinate=y;
                choosing=false;
            }
        }
        if (((JButton) e.getSource()).getName().equals("attack")){
            pressed=true;
            pressedmove=false;
        }
        if (((JButton) e.getSource()).getName().equals("move")){
            pressedmove=true;
            pressed=false;
        }
        if (((JButton) e.getSource()).getName().equals("endturn")){

            this.game.endTurn();
            turnorder.setText(this.game.getTurnOrder().toString());
            frame.repaint();
            frame.revalidate();
        }
        if (((JButton) e.getSource()).getName().equals("cast")){
            selections.removeAll();
            for(int i=0;i<game.getCurrentChampion().getAbilities().size();i++){
                JButton ability= new JButton(game.getCurrentChampion().getAbilities().get(i).getName());
                ability.addActionListener(this);
                ability.setName(game.getCurrentChampion().getAbilities().get(i).getCastArea()+"");
                selections.add(ability);
            }
            JButton back= new JButton("back");
            back.setName("back");
            back.addActionListener(this);
            selections.add(back);
            frame.remove(actions);

            frame.add(selections,BorderLayout.EAST);
            frame.repaint();
            frame.revalidate();
        }
        if(((JButton) e.getSource()).getName().equals("back")){
            frame.remove(selections);

            frame.add(actions,BorderLayout.EAST);
            frame.repaint();
            frame.revalidate();
        }
        if(((JButton) e.getSource()).getName().equals("SINGLETARGET")){
           choosing=true;
           String name= ((JButton) e.getSource()).getText();
           for(int i=0;i<game.getCurrentChampion().getAbilities().size();i++){
               if(game.getCurrentChampion().getAbilities().get(i).getName().equals(name)){
                   temp=game.getCurrentChampion().getAbilities().get(i);
               }
           }

        }
        if(((JButton) e.getSource()).getName().equals("DIRECTIONAL")){

        }
        if(((JButton) e.getSource()).getName().equals("SURROUND")||((JButton) e.getSource()).getName().equals("TEAMTARGET")||((JButton) e.getSource()).getName().equals("SELFTARGET")){

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar()=='s' &&pressed){

            try {
                this.game.attack(Direction.UP);

            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
                    board.removeAll();


            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    if(game.getBoard()[i][j]!=null){
                        board.add(buttons[i][j]);}
                    else{
                        board.remove(buttons[i][j]);
                        JButton pl=new JButton();
                        pl.addActionListener(this);
                        pl.addMouseListener(this);
                        pl.setName("null");
                        board.add(pl);
                        buttons[i][j]=pl;
                    }
                }
            }
        }
        if (e.getKeyChar()=='w' &&pressed){
            try {
                this.game.attack(Direction.DOWN);

            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            board.removeAll();


            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    if(game.getBoard()[i][j]!=null){
                        board.add(buttons[i][j]);}
                    else{
                        JButton pl=new JButton();
                        pl.addActionListener(this);
                        pl.addMouseListener(this);
                        pl.setName("null");
                        board.add(pl);
                        buttons[i][j]=pl;
                    }
                }
            }
        }
        if (e.getKeyChar()=='a' &&pressed){
            try {
                this.game.attack(Direction.LEFT);
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            board.removeAll();


            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    if(game.getBoard()[i][j]!=null){
                        board.add(buttons[i][j]);}
                    else{
                        JButton pl=new JButton();
                        pl.addActionListener(this);
                        pl.addMouseListener(this);
                        pl.setName("null");
                        board.add(pl);
                        buttons[i][j]=pl;
                    }
                }
            }
        }
        if (e.getKeyChar()=='d' &&pressed){
            try {
                this.game.attack(Direction.RIGHT);
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (ChampionDisarmedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            board.removeAll();


            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    if(game.getBoard()[i][j]!=null){
                        board.add(buttons[i][j]);}
                    else{
                        JButton pl=new JButton();
                        pl.addActionListener(this);
                        pl.addMouseListener(this);
                        pl.setName("null");
                        board.add(pl);
                        buttons[i][j]=pl;
                    }
                }
            }
        }
        if (e.getKeyChar()=='s' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            JButton temp;
            Point test=this.game.getCurrentChampion().getLocation();
            temp=buttons[test.x][test.y];
            try {
                this.game.move(Direction.UP);
                System.out.println("LOL");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(board, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
             catch (UnallowedMovementException ex) {
                 JOptionPane.showMessageDialog(board, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                 return;
            }
            board.removeAll();
            buttons[test.x+1][test.y]=temp;
            buttons[test.x][test.y]=new JButton();
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='w' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            JButton temp;
            Point test=this.game.getCurrentChampion().getLocation();
            temp=buttons[test.x][test.y];
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

            board.removeAll();
            buttons[test.x-1][test.y]=temp;
            buttons[test.x][test.y]=new JButton();
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }
            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='a' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            JButton temp;
            Point test=this.game.getCurrentChampion().getLocation();
            temp=buttons[test.x][test.y];
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

            board.removeAll();
            buttons[test.x][test.y-1]=temp;
            buttons[test.x][test.y]=new JButton();
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }

            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        if (e.getKeyChar()=='d' &&pressedmove){
            System.out.println(this.game.getCurrentChampion().getLocation());
            JButton temp;
            Point test=this.game.getCurrentChampion().getLocation();
            temp=buttons[test.x][test.y];
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

            board.removeAll();
            buttons[test.x][test.y+1]=temp;
            buttons[test.x][test.y]=new JButton();
            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    board.add(buttons[i][j]);
                }
            }

            System.out.println(this.game.getCurrentChampion().getLocation());
        }
        game.checkGameOver();
        frame.repaint();
        frame.revalidate();
        board.repaint();
        board.revalidate();
        info.repaint();
        info.revalidate();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressed=false;
        pressedmove=false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {


    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(choosing){
            System.out.println("test");
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
            try {
                game.castAbility(temp,x,y);
                System.out.println("sus");
            } catch (NotEnoughResourcesException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (AbilityUseException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (InvalidTargetException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            } catch (CloneNotSupportedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            board.removeAll();


            for(int i=0;i< buttons.length;i++){
                for(int j=0;j<buttons.length;j++){
                    if(game.getBoard()[i][j]!=null){
                        board.add(buttons[i][j]);}
                    else{
                        board.remove(buttons[i][j]);
                        JButton pl=new JButton();
                        pl.addActionListener(this);
                        pl.addMouseListener(this);
                        pl.setName("null");
                        board.add(pl);
                        buttons[i][j]=pl;
                    }
                }
            }
            frame.repaint();
            frame.revalidate();
            choosing=false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
