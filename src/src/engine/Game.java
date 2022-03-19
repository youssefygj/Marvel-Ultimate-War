package engine;

import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.ArrayList;

public class Game {
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    private static int BOARDHEIGHT;
    private static int BOARDWIDTH;
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;
    private Object[][] board;
    private PriorityQueue turnOrder;

    public Game(Player first, Player second) {
        this.firstPlayer = first;
        this.secondPlayer = second;
        board = new Object[5][5];
        placeChampions();
        placeCovers();
    }

    public static void loadAbilities(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
    }

    public static void loadChampions(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
    }

    private void placeChampions() {
        ArrayList<Champion> firstTeam = firstPlayer.getTeam();
        ArrayList<Champion> secondTeam = secondPlayer.getTeam();
        for (int i = 1; i <= 3; i++) {                             //places the 3 champions of each player on the board
            board[4][i] = firstTeam.get(i);                        //player1 on the bottom, player2 on the top
            board[0][i] = secondTeam.get(i);                       //no champions on edges.
        }
    }

    //    random.nextInt(max - min + 1) + min
    private void placeCovers() {
        Random Rand = new Random();
        for (int i = 0; i < 5; i++) {
            int x = Rand.nextInt(5);      //places 5 covers on the board at random cell
            int y = Rand.nextInt(5);      //excluding edges and already occupied cells

            while (x == y || (x != 4 && y == 0) || (y != 0 && x == 4) || (board[x][y] != null)) {
                x = Rand.nextInt(5);
                y = Rand.nextInt(5);
            }

            Cover coverToBePlaced = new Cover(x, y);
            board[x][y] = coverToBePlaced;
        }
    }
}
