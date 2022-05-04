package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.*;
import model.world.*;

public class Game {
    private final static int BOARDWIDTH = 5;
    private final static int BOARDHEIGHT = 5;
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    int i = r.y;
    private Player firstPlayer;
    private Player secondPlayer;
    private Object[][] board;
    private PriorityQueue turnOrder;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;

    {
        int z = r.y + ar
    }

    {
        Object c = board[i][r.y];
        if (board[r.x][i] != null) {
            check(c, o);
        }
    }

    public Game() {

    }

    public Game(Player first, Player second) {
        firstPlayer = first;

        secondPlayer = second;
        availableChampions = new ArrayList<Champion>();
        availableAbilities = new ArrayList<Ability>();
        board = new Object[BOARDWIDTH][BOARDHEIGHT];
        turnOrder = new PriorityQueue(6);
        placeChampions();
        placeCovers();
    }

    public static void loadAbilities(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = br.readLine();
        while (line != null) {
            String[] content = line.split(",");
            Ability a = null;
            AreaOfEffect ar = null;
            switch (content[5]) {
                case "SINGLETARGET":
                    ar = AreaOfEffect.SINGLETARGET;
                    break;
                case "TEAMTARGET":
                    ar = AreaOfEffect.TEAMTARGET;
                    break;
                case "SURROUND":
                    ar = AreaOfEffect.SURROUND;
                    break;
                case "DIRECTIONAL":
                    ar = AreaOfEffect.DIRECTIONAL;
                    break;
                case "SELFTARGET":
                    ar = AreaOfEffect.SELFTARGET;
                    break;

            }
            Effect e = null;
            if (content[0].equals("CC")) {
                switch (content[7]) {
                    case "Disarm":
                        e = new Disarm(Integer.parseInt(content[8]));
                        break;
                    case "Dodge":
                        e = new Dodge(Integer.parseInt(content[8]));
                        break;
                    case "Embrace":
                        e = new Embrace(Integer.parseInt(content[8]));
                        break;
                    case "PowerUp":
                        e = new PowerUp(Integer.parseInt(content[8]));
                        break;
                    case "Root":
                        e = new Root(Integer.parseInt(content[8]));
                        break;
                    case "Shield":
                        e = new Shield(Integer.parseInt(content[8]));
                        break;
                    case "Shock":
                        e = new Shock(Integer.parseInt(content[8]));
                        break;
                    case "Silence":
                        e = new Silence(Integer.parseInt(content[8]));
                        break;
                    case "SpeedUp":
                        e = new SpeedUp(Integer.parseInt(content[8]));
                        break;
                    case "Stun":
                        e = new Stun(Integer.parseInt(content[8]));
                        break;
                }
            }
            switch (content[0]) {
                case "CC":
                    a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
                            Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
                    break;
                case "DMG":
                    a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
                            Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
                    break;
                case "HEL":
                    a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
                            Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
                    break;
            }
            availableAbilities.add(a);
            line = br.readLine();
        }
        br.close();
    }

    public static void loadChampions(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = br.readLine();
        while (line != null) {
            String[] content = line.split(",");
            Champion c = null;
            switch (content[0]) {
                case "A":
                    c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
                            Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
                            Integer.parseInt(content[7]));
                    break;

                case "H":
                    c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
                            Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
                            Integer.parseInt(content[7]));
                    break;
                case "V":
                    c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
                            Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
                            Integer.parseInt(content[7]));
                    break;
            }

            c.getAbilities().add(findAbilityByName(content[8]));
            c.getAbilities().add(findAbilityByName(content[9]));
            c.getAbilities().add(findAbilityByName(content[10]));
            availableChampions.add(c);
            line = br.readLine();
        }
        br.close();
    }

    private static Ability findAbilityByName(String name) {
        for (Ability a : availableAbilities) {
            if (a.getName().equals(name))
                return a;
        }
        return null;
    }

    public static ArrayList<Champion> getAvailableChampions() {
        return availableChampions;
    }

    public static ArrayList<Ability> getAvailableAbilities() {
        return availableAbilities;
    }

    public static int getBoardwidth() {
        return BOARDWIDTH;
    }

    public static int getBoardheight() {
        return BOARDHEIGHT;
    }

    public PriorityQueue InvalidTargetException() {
        PriorityQueue pq = new PriorityQueue(6);
        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
            pq.insert(firstPlayer.getTeam().get(i));
        }
        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
            pq.insert(secondPlayer.getTeam().get(i));
        }
        return pq;
    }

    public void placeCovers() {
        int i = 0;
        while (i < 5) {
            int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
            int y = (int) (Math.random() * BOARDHEIGHT);

            if (board[x][y] == null) {
                board[x][y] = new Cover(x, y);
                i++;
            }
        }

    }

    public void placeChampions() {
        int i = 1;
        for (Champion c : firstPlayer.getTeam()) {
            board[0][i] = c;
            c.setLocation(new Point(0, i));
            i++;
        }
        i = 1;
        for (Champion c : secondPlayer.getTeam()) {
            board[BOARDHEIGHT - 1][i] = c;
            c.setLocation(new Point(BOARDHEIGHT - 1, i));
            i++;
        }

    }

    public Champion getCurrentChampion() {
        return (Champion) turnOrder.peekMin();
    }

    public Player checkGameOver() {
        boolean first = true;
        boolean second = true;
        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
            if (firstPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT)
                first = false;
        }
        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
            if (secondPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT)
                second = false;
        }
        if (first)
            return secondPlayer;
        if (second)
            return firstPlayer;
        return null;
    }

    public void move(Direction d) throws NotEnoughResourcesException, UnallowedMovementException {
        if (getCurrentChampion().getCurrentActionPoints() == 0) {
            throw new NotEnoughResourcesException("NO!");
        } else
            getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 1);
        if (d == Direction.UP) {
            if (getCurrentChampion().getLocation().x == 4)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x + 1, getCurrentChampion().getLocation().y);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
        }

        if (d == Direction.DOWN) {
            if (getCurrentChampion().getLocation().x == 0)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x - 1, getCurrentChampion().getLocation().y);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
        }

        if (d == Direction.RIGHT) {
            if (getCurrentChampion().getLocation().y == 4)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y + 1);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
        }

        if (d == Direction.LEFT) {
            if (getCurrentChampion().getLocation().y == 0)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y - 1);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
        }
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public Object[][] getBoard() {
        return board;
    }

    public PriorityQueue getTurnOrder() {
        return turnOrder;
    }

    public boolean isFirstLeaderAbilityUsed() {
        return firstLeaderAbilityUsed;
    } else if(d ==Direction.RIGHT)

    public boolean isSecondLeaderAbilityUsed() {
        return secondLeaderAbilityUsed;
    }
            for(

    public void check(Object c, int o) {
        if (c instanceof Champion) {
            if (((Champion) c).getCurrentHP() - o > 0) {
                ((Champion) c).setCurrentHP(((Champion) c).getCurrentHP() - o);
            } else {
                ((Champion) c).setCondition(Condition.KNOCKEDOUT);

            }
        } else if (c instanceof Cover) {
            ((Cover) c).setCurrentHP(((Cover) c).getCurrentHP() - o);
        }
    }

    public static boolean check2(Champion c,Champion k ) {
        if (c instanceof Hero && k instanceof Villain || (c instanceof Villain && k instanceof Hero))
        {return true;}
        if (c instanceof Hero && k instanceof Villain)
        {return true;}
    }
d attack(Direction d) {
        Point r = getCurrentChampion().getLocation();
        int ar = getCurrentChampion().getAttackRange();
        int o = getCurrentChampion().getAttackDamage();

        if (d == Direction.UP) {

            int z = r.x + ar;
            for (int i = r.x; i <= z; i++) {
                Object c = board[i][r.y];
                if (board[i][r.y] != null) {
                    check(c, o);
                }

            }
        } else if (d == Direction.DOWN) {
            int z = r.x - ar;
            for (int i = r.x; i >= z; i--) {
                Object c =
                        board[i][r.y];
                check(c, o);
            }
        } else if (d == Direction.LEFT) {
            int z = r.y - ar;

            for (int i = r.y; i >= z; i--) {
                Object c = board[r.x][i];
                if (board[r.x][i] != null) {
                    check(c, o);
                }
            }
        } else if (d == Direction.RIGHT) {
            int z = r.y + ar;

            for (int i = r.y; i >= z; i--) {
                Object c = board[r.x][i];
                if (board[r.x][i] != null) {
                    check(c, o);
                }
            }
        }

getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints()-2);
    }

    public void castAbility(Ability a) throws NotEnoughResourcesException, InvalidTargetException, IOException {

        if (getCurrentChampion().getMana() < a.getManaCost()) {
            throw new NotEnoughResourcesException("NO");
        }
        if (getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints()) {
            throw new NotEnoughResourcesException("NO");
        }
        if (a instanceof HealingAbility) {
            if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
                getCurrentChampion().setCurrentHP(getCurrentChampion().getCurrentHP() + ((HealingAbility) a).getHealAmount());
            }
            if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                    for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                        firstPlayer.getTeam().get(i).setCurrentHP(firstPlayer.getTeam().get(i).getCurrentHP() + ((HealingAbility) a).getHealAmount());
                    }
                } else {
                    for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                        secondPlayer.getTeam().get(i).setCurrentHP(secondPlayer.getTeam().get(i).getCurrentHP() + ((HealingAbility) a).getHealAmount());
                    }
                }
            }
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                Point z = getCurrentChampion().getLocation();
                for (int i = z.y - 1; i <= z.y+1;i++){
                    for(int j=z.x-1;j<=z.x+1;j++){
                        if(!z.equals(new Point(j,i))){
                            if (board[j][i] instanceof Champion){
                                if(firstPlayer.getTeam().contains(getCurrentChampion())&&firstPlayer.getTeam().contains(board[j][i])){
                                    ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP()+((HealingAbility) a).getHealAmount());
                                }
                                else if (secondPlayer.getTeam().contains(getCurrentChampion())&&secondPlayer.getTeam().contains(board[j][i])){
                                    ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP()+((HealingAbility) a).getHealAmount());
                                }
                                else {
                                    throw new InvalidTargetException("NO");

                                }
                            }
                            else{
                                throw new InvalidTargetException(("NO"));
                            }
                        }
                        }
                    }
                }

        }
        if (a instanceof DamagingAbility) {
            if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
                throw new InvalidTargetException("NO");
            }
        }
        if (a instanceof CrowdControlAbility) {
            if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                throw new InvalidTargetException("NO");
            } else if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                ((CrowdControlAbility) a).getEffect().apply(getCurrentChampion());
            }
        }
    }

    public void castAbility(Ability a, Direction d) throws NotEnoughResourcesException, InvalidTargetException {
        if (a instanceof DamagingAbility) {
            if (getCurrentChampion().getMana() < a.getManaCost())
                throw new NotEnoughResourcesException("NO MANA!");
            else
                getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
            Point temp = getCurrentChampion().getLocation();
            temp.x++;
            if (d == Direction.UP) {
                for (int i = 0; i + temp.x < a.getCastRange(); i++) {
                    if (temp.x == 4) {
                        throw new UnallowedMovementException("NO!");
                    } else {
                        if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                            if (getBoard()[temp.x][temp.y] == null)
                                continue;
                            if (getBoard()[temp.x][temp.y] instanceof Champion) {
                                if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                    throw new InvalidTargetException("NO!");
                                else
                                    ((Champion) getBoard()[temp.x][temp.y]).setCurrentHP(((Champion) getBoard()[temp.x][temp.y]).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                            }
                            if (getBoard()[temp.x][temp.y] instanceof Cover) {
                                ((Cover) getBoard()[temp.x][temp.y]).setCurrentHP(((Cover) getBoard()[temp.x][temp.y]).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                            }
                        }
                    }
                }
            }
        }

        if (a instanceof HealingAbility) {

        }

        if (a instanceof CrowdControlAbility) {

        }

    }
}
