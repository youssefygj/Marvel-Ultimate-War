package engine;

import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;


public class Game<c> {
    private static final int BOARDHEIGHT = 5;
    private static final int BOARDWIDTH = 5;
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
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

    public static void loadAbilities(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String currentLine = br.readLine();
        while (currentLine != null) {
            String[] r = currentLine.split(",");

            availableAbilities.add(new Ability(r[1], Integer.parseInt(r[2]), Integer.parseInt(r[4]), Integer.parseInt(r[3]), AreaOfEffect.valueOf(r[5]), Integer.parseInt(r[6])));
            if (r[0].equals("CC")) {
                if (r[7].equals("Shield"))
                    Shield e = new Shield(Integer.parseInt(r[8]));

                s = new CrowdControlAbility(r[0], r[1], Integer.parseInt(r[2]), Integer.parseInt(r[4]), Integer.parseInt(r[3]), AreaOfEffect.valueOf(r[5]), Integer.parseInt(r[6]));
            }
            currentLine = br.readLine();
        }
        br.close();
    }

    public static void loadChampions(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String currentLine = br.readLine();
        while (currentLine != null) {
            String[] r = currentLine.split(",");
            if (r[0].equals("A")) {
                AntiHero newChamp = new AntiHero(r[1], Integer.parseInt(r[2]), Integer.parseInt(r[3]), Integer.parseInt(r[4]),
                        Integer.parseInt(r[5]), Integer.parseInt(r[6]), Integer.parseInt(r[7]));
                Ability newAbility = new Ability()
            } else if (r[0].equals("H")) {
                Hero newChamp = new Hero(r[1], Integer.parseInt(r[2]), Integer.parseInt(r[3]), Integer.parseInt(r[4]),
                        Integer.parseInt(r[5]), Integer.parseInt(r[6]), Integer.parseInt(r[7]));
            } else {
                Villain newChamp = new Villain(r[1], Integer.parseInt(r[2]), Integer.parseInt(r[3]), Integer.parseInt(r[4]),
                        Integer.parseInt(r[5]), Integer.parseInt(r[6]), Integer.parseInt(r[7]));
            }
            currentLine = br.readLine();
        }
        br.close();
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
        }
    }


}

