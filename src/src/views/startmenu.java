package views;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class startmenu implements ActionListener {
    JFrame x;
    public startmenu(){
         x = new JFrame();
        x.setLayout(null);
        JButton VSPlayer = new JButton("VSPlayer");
        JButton VSCOMP = new JButton("VSCOMP");
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

        x.add(VSPlayer);
        x.add(VSCOMP);
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
