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
        System.out.println(game.getFirstPlayer().getLeader());
        System.out.println(game.getSecondPlayer().getLeader());
        this.frame.remove(removal);
        this.frame.revalidate();
        this.frame.repaint();

    }
}
