package views;

import javax.swing.*;
import java.awt.*;

public class playerView extends JFrame {
    private JPanel champions = new JPanel();

    public JTextArea getAttributes() {
        return attributes;
    }

    private JTextArea attributes = new JTextArea();

    public playerView() {
        this.setBounds(500, 500, 1000, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        champions.setPreferredSize(new Dimension(1000, 400));
        champions.setLayout(new GridLayout(5, 3));
        attributes.setText("nig");
        this.add(champions, BorderLayout.NORTH);
        this.add(attributes, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    public static void main(String[] args) {
        playerView sus = new playerView();
    }

    public JPanel getChampions() {
        return champions;
    }
}
