package views;

import engine.Game;
import engine.Player;
import engine.gameListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class startController implements ActionListener {
    JTextField field1;
    JTextField field2;
    JFrame z;

    public startController() {
        z = new JFrame();
        JPanel x = new JPanel();
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
        test.addActionListener(this);
        z.add(test, BorderLayout.SOUTH);
        x.revalidate();
        x.repaint();
    }

    public static void main(String[] args) {
        startController s = new startController();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String firstname = field1.getText();
        String secondname = field2.getText();
        Player first = new Player(firstname);
        Player second = new Player(secondname);
        try {
            secondController s = new secondController(first,second);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        z.dispose();
    }
}
