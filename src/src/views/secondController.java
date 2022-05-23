package views;

import engine.Game;
import engine.Player;
import model.world.Champion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class secondController implements ActionListener {
    private Game game;
    private playerView playerView;
    private ArrayList<JButton> buttons = new ArrayList<>();

    secondController(Player first, Player second) throws IOException {
        game = new Game(first, second);
        game.loadChampions("D:\\Marvel-Ultimate-War\\src\\Champions.csv");
        game.loadAbilities("D:\\Marvel-Ultimate-War\\src\\Abilities.csv");
        playerView = new playerView();
        for (int i = 0; i < game.getAvailableChampions().size(); i++) {
            JButton b = new JButton(game.getAvailableChampions().get(i).getName());
            b.addActionListener(this);
            b.setVisible(true);
            b.setFont(new Font("Arial", Font.BOLD, 18));
            buttons.add(b);
            playerView.getChampions().add(b);
        }
        playerView.revalidate();
        playerView.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton a = (JButton) e.getSource();
        int i = buttons.indexOf(a);
        Champion current = game.getAvailableChampions().get(i);
        System.out.println(i);
        System.out.println(game.getAvailableChampions());
//        playerView.getAttributes().setText(current.toString());
    }
}
