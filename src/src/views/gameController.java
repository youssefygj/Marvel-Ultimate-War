package views;

import engine.Game;

import javax.swing.*;

public class gameController {
    Game game;
    JFrame frame;
    JPanel removal;
    public gameController(Game game, JFrame frame,JPanel removal){
        this.game=game;
        this.frame=frame;
        this.frame.remove(removal);
        this.frame.revalidate();
        this.frame.repaint();

    }
}
