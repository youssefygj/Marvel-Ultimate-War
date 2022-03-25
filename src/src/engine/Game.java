package engine;

import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;


public class Game<c> {
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    private static final int BOARDHEIGHT=5;
    private static final int BOARDWIDTH = 5;
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;
    private Object[][] board;
    private PriorityQueue turnOrder;

    public Game(Player first, Player second) {
        this.firstPlayer = first;
        this.secondPlayer = second;
        board = new Object[BOARDWIDTH][BOARDHEIGHT];
        placeChampions();
        placeCovers();
    }



    private void placeChampions() {
        ArrayList<Champion> firstTeam = firstPlayer.getTeam();
        ArrayList<Champion> secondTeam = secondPlayer.getTeam();
        for (int i = 1; i <= 3; i++) {                             //places the 3 champions of each player on the board
            board[4][i] = firstTeam.get(i);                        //player1 on the bottom, player2 on the top
            board[0][i] = secondTeam.get(i);                       //no champions on edges.
        }
    }


    private void placeCovers() {
        Random Rand = new Random();
        for (int i = 0; i < 5; i++) {
            int x = Rand.nextInt(5);      //places 5 covers on the board at random cell
            int y = Rand.nextInt(5);      //excluding edges and already occupied cells

            while (x == y || (x != 4 && y == 0) || (y != 0 && x == 4) || (board[x][y] != null)) {
                x = Rand.nextInt(5);
                y = Rand.nextInt(5);
            }
        }}

//int c=0;
//    do{
//
//        int x = (int) (Math.random() * 4 + 1);
//        int y = (int) (Math.random() * 4 + 1);
//        if ((board[x][y] == null) && (x != 0 && y != 0) && (x != 0 && y != 4) && (x != 4 && y != 4)) {
//            c=c+1;
//            board[x][y] = new Cover(x, y);
//        }
//    }while (c!=4);


            public static void loadAbilities (String filePath) throws IOException{
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                String currentLine = br.readLine();
              while(currentLine!=null){

                  String[] r=currentLine.split(",");
                  availableAbilities.add(new Ability(r[1],r[2],r[4],r[3],r[5],r[6]));
                  currentLine = br.readLine();
                  br.close();
              }}



            }

