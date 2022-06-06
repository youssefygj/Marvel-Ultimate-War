package views;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class startmenu implements ActionListener {
    JFrame x;
    public startmenu(){
         x = new JFrame();
        x.setLayout(null);
        ImageIcon icon = new ImageIcon( "buttonStock1h.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(155, 130, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        JButton VSPlayer = new JButton(icon);
        JButton VSCOMP = new JButton(icon);
        JLabel test = new JLabel("        PVP");
        test.setForeground(Color.GRAY);
        test.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel test2 = new JLabel("        PVE");
        test2.setForeground(Color.GRAY);
        test2.setFont(new Font("Arial", Font.BOLD, 18));
       test.setBounds(200,0,100,100);
        VSPlayer.add(test);
        VSCOMP.add(test2);
        VSPlayer.addActionListener(this);
        VSCOMP.addActionListener(this);
        VSPlayer.setBounds(400,400,150,50);
       VSPlayer.setName("player");
        VSCOMP.setName("comp");
        VSCOMP.setBounds(400,600,150,50);

        x.setExtendedState(JFrame.MAXIMIZED_BOTH);
        x.setUndecorated(false);
       x.setVisible(true);
        x.setDefaultCloseOperation(EXIT_ON_CLOSE);
        x.setVisible(true);
        JLabel background = new JLabel(new ImageIcon("backrgound.jpg"));
        background.setBounds(-8,-8,x.getWidth(),x.getHeight());
        background.setVisible(true);
        x.add(VSPlayer);
        x.add(VSCOMP);
        x.add(background);

        VSPlayer.setVisible(true);
        VSCOMP.setVisible(true);

        x.repaint();
        x.revalidate();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(((JButton)e.getSource()).getName().equals("player")){
            x.dispose();
            startController z= new startController();
        }

        else{
        x.dispose();
        VSCOMP test = new VSCOMP();
        }
    }
    public static void main(String[] args){
        startmenu x = new startmenu();
    }
}
