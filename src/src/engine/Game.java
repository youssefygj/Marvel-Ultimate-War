package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;


import exceptions.*;
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

    private Player firstPlayer;
    private Player secondPlayer;
    private Object[][] board;
    private PriorityQueue turnOrder;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;


    public Game(Player first, Player second) {
        firstPlayer = first;
        turnOrder = new PriorityQueue(6);
        secondPlayer = second;
        availableChampions = new ArrayList<Champion>();
        availableAbilities = new ArrayList<Ability>();
        board = new Object[BOARDWIDTH][BOARDHEIGHT];
        placeChampions();
        placeCovers();
        prepareChampionTurns();
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

    public static boolean checkifextra(Champion c, Champion k) {

        if ((c instanceof Hero && k instanceof Hero) || (c instanceof Villain && k instanceof Villain) || (c instanceof AntiHero && k instanceof AntiHero)) {
            return false;
        } else {
            return true;
        }

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
        if (getCurrentChampion().getCondition() == Condition.ROOTED) {
            throw new UnallowedMovementException("NO!");
        }
        if (d == Direction.UP) {
            if (getCurrentChampion().getLocation().x == 4)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x + 1, getCurrentChampion().getLocation().y);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
            board[temp.x][temp.y] = getCurrentChampion();
            board[temp.x - 1][temp.y] = null;
        }

        if (d == Direction.DOWN) {
            if (getCurrentChampion().getLocation().x == 0)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x - 1, getCurrentChampion().getLocation().y);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
            board[temp.x][temp.y] = getCurrentChampion();
            board[temp.x + 1][temp.y] = null;
        }

        if (d == Direction.RIGHT) {
            if (getCurrentChampion().getLocation().y == 4)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y + 1);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
            board[temp.x][temp.y] = getCurrentChampion();
            board[temp.x][temp.y - 1] = null;
        }

        if (d == Direction.LEFT) {
            if (getCurrentChampion().getLocation().y == 0)
                throw new UnallowedMovementException("NO!");
            Point temp = new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y - 1);
            if (getBoard()[temp.x][temp.y] != null) {
                throw new UnallowedMovementException("NO!");
            }
            getCurrentChampion().setLocation(temp);
            board[temp.x][temp.y] = getCurrentChampion();
            board[temp.x][temp.y + 1] = null;
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
    }

    public boolean isSecondLeaderAbilityUsed() {
        return secondLeaderAbilityUsed;
    }

    public boolean normalattack(Object c, int o) {

        if (c instanceof Champion) {
            if (((Champion) c).getCurrentHP() - o > 0) {
                ((Champion) c).setCurrentHP(((Champion) c).getCurrentHP() - o);
                return true;
            } else {
                ((Champion) c).setCondition(Condition.KNOCKEDOUT);
                ((Champion) c).setCurrentHP(0);
                if (((Champion) (turnOrder.peekMin())).getName().equals(((Champion) c).getName())) ;
                {
                    turnOrder.remove();
                }
                return false;
            }
        } else {
            if (((Cover) c).getCurrentHP() - o > 0) {
                ((Cover) c).setCurrentHP(((Cover) c).getCurrentHP() - o);
                return true;
            } else {
                ((Cover) c).setCurrentHP(0);
                return false;
            }

        }

    }
////////////////END

    public boolean extradamage(Object c, int o) {
        if (((Champion) c).getCurrentHP() - 0.5 * o > 0) {
            ((Champion) c).setCurrentHP(((Champion) c).getCurrentHP() - (int) (1.5 * o));
            return true;
        } else {
            ((Champion) c).setCurrentHP(0);
            ((Champion) c).setCondition(Condition.KNOCKEDOUT);
            return false;
        }
    }

    public boolean checkSaD(Champion c) {
        boolean hasShield = false;
        boolean hasDodge = false;
        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i).getName().equals("Shield")) {
                hasShield = true;
            } else if (c.getAppliedEffects().get(i).getName().equals("Dodge")) {
                hasDodge = true;
            }
        }
        return hasShield && hasDodge;
    }

    public boolean checkdis(Champion c) {
        boolean hasDis = false;
        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i).getName().equals("Disarm")) {
                hasDis = true;
            }
        }
        return hasDis;
    }

    public boolean checkshield(Champion c) {
        boolean hasShield = false;

        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i).getName().equals("Shield")) {
                hasShield = true;
            }
        }
        return hasShield;
    }

    public boolean checkdodge(Champion c) {
        boolean hasDodge = false;

        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i).getName().equals("Dodge")) {
                hasDodge = true;
            }
        }
        return hasDodge;
    }

    public void die(Champion c) {
        if (c.getCurrentHP() == 0) {
            c.setCondition(Condition.KNOCKEDOUT);
            Point z = c.getLocation();
            board[z.x][z.y] = null;
            ArrayList<Champion> f = new ArrayList<Champion>();
            if(firstPlayer.getTeam().contains(c))
                firstPlayer.getTeam().remove(c);
            if(secondPlayer.getTeam().contains(c))
                secondPlayer.getTeam().remove(c);
            while (!turnOrder.isEmpty())
                f.add((Champion) turnOrder.remove());
            if(firstPlayer.getTeam().contains(c))
                firstPlayer.getTeam().remove(c);
            if(secondPlayer.getTeam().contains(c))
                secondPlayer.getTeam().remove(c);
            for (int i = 0; i < f.size(); i++) {
                if (!f.get(i).getName().equals(c.getName()))
                    turnOrder.insert(f.get(i));
            }
        }
    }

    public void die(Cover c) {
        if (c.getCurrentHP() == 0) {
            Point z = c.getLocation();
            board[z.x][z.y] = null;
        }
    }

    public void attack(Direction d) throws AbilityUseException, ChampionDisarmedException, NotEnoughResourcesException, IOException, InvalidTargetException {
        if (getCurrentChampion().getCurrentActionPoints() < 2) {
            throw new NotEnoughResourcesException();
        }
        if (checkdis(getCurrentChampion())) {
            throw new ChampionDisarmedException();
        }
        Point z = getCurrentChampion().getLocation();

        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 2);

        if (d == Direction.UP) {
            for (int i = z.x + 1; i < z.x + 1 + getCurrentChampion().getAttackRange(); i++) {
                if (i > 4) {
                    break;
                }

                if (board[i][z.y] != null) {
                    if (board[i][z.y] instanceof Champion) {

                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[i][z.y])) {
                            continue;
                        } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[i][z.y])) {
                            continue;
                        } else {

                            if (checkSaD((Champion) board[i][z.y])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    for (int j = 0; j < ((Champion) board[i][z.y]).getAppliedEffects().size(); j++) {
                                        if (((Champion) board[i][z.y]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                            ((Champion) board[i][z.y]).getAppliedEffects().get(j).remove(((Champion) board[i][z.y]));
                                            return;
                                        }
                                    }

                                } else {
                                    return;
                                }

                            } else if (checkshield((Champion) board[i][z.y])) {

                                for (int j = 0; j < ((Champion) board[i][z.y]).getAppliedEffects().size(); j++) {
                                    if (((Champion) board[i][z.y]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                        ((Champion) board[i][z.y]).getAppliedEffects().get(j).remove(((Champion) board[i][z.y]));
                                        return;
                                    }
                                }
                            } else if (checkdodge((Champion) board[i][z.y])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    if (checkifextra(getCurrentChampion(), (Champion) board[i][z.y])) {
                                        ((Champion) board[i][z.y]).setCurrentHP((((Champion) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                        die((Champion) board[i][z.y]);
                                        return;
                                    } else {
                                        ((Champion) board[i][z.y]).setCurrentHP((int) (((Champion) board[i][z.y]).getCurrentHP() - getCurrentChampion().getAttackDamage() * 1));
                                        die((Champion) board[i][z.y]);
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {

                                if (checkifextra(getCurrentChampion(), (Champion) board[i][z.y])) {
                                    ((Champion) board[i][z.y]).setCurrentHP((((Champion) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                    die((Champion) board[i][z.y]);
                                    return;
                                } else {
                                    ((Champion) board[i][z.y]).setCurrentHP((int) (((Champion) board[i][z.y]).getCurrentHP() - getCurrentChampion().getAttackDamage() * 1));
                                    die((Champion) board[i][z.y]);
                                    return;
                                }
                            }

                        }

                    } else {
                        ((Cover) board[i][z.y]).setCurrentHP((((Cover) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                        die((Cover) board[i][z.y]);
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
        if (d == Direction.DOWN) {
            for (int i = z.x - 1; i > z.x - 1 - getCurrentChampion().getAttackRange(); i--) {
                if (i > 0) {
                    break;
                }

                if (board[i][z.y] != null) {
                    if (board[i][z.y] instanceof Champion) {

                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[i][z.y])) {
                            continue;
                        } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[i][z.y])) {
                            continue;
                        } else {

                            if (checkSaD((Champion) board[i][z.y])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    for (int j = 0; j < ((Champion) board[i][z.y]).getAppliedEffects().size(); j++) {
                                        if (((Champion) board[i][z.y]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                            ((Champion) board[i][z.y]).getAppliedEffects().get(j).remove(((Champion) board[i][z.y]));
                                            return;
                                        }
                                    }

                                } else {
                                    return;
                                }

                            } else if (checkshield((Champion) board[i][z.y])) {

                                for (int j = 0; j < ((Champion) board[i][z.y]).getAppliedEffects().size(); j++) {
                                    if (((Champion) board[i][z.y]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                        ((Champion) board[i][z.y]).getAppliedEffects().get(j).remove(((Champion) board[i][z.y]));
                                        return;
                                    }
                                }
                            } else if (checkdodge((Champion) board[i][z.y])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    if (checkifextra(getCurrentChampion(), (Champion) board[i][z.y])) {
                                        ((Champion) board[i][z.y]).setCurrentHP((((Champion) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                        die((Champion) board[i][z.y]);
                                        return;
                                    } else {
                                        ((Champion) board[i][z.y]).setCurrentHP((int) (((Champion) board[i][z.y]).getCurrentHP() - getCurrentChampion().getAttackDamage() * 1));
                                        die((Champion) board[i][z.y]);
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {

                                if (checkifextra(getCurrentChampion(), (Champion) board[i][z.y])) {
                                    ((Champion) board[i][z.y]).setCurrentHP((((Champion) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                    die((Champion) board[i][z.y]);
                                    return;
                                } else {
                                    ((Champion) board[i][z.y]).setCurrentHP((int) (((Champion) board[i][z.y]).getCurrentHP() - getCurrentChampion().getAttackDamage() * 1));
                                    die((Champion) board[i][z.y]);
                                    return;
                                }
                            }
                        }

                    } else {
                        ((Cover) board[i][z.y]).setCurrentHP((((Cover) board[i][z.y]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                        die((Cover) board[i][z.y]);
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
        if (d == Direction.LEFT) {

            for (int i = z.y - 1; i > z.y - 1 - getCurrentChampion().getAttackRange(); i--) {
                if (i < 0) {
                    break;
                }
                if (board[z.x][i] != null) {
                    if (board[z.x][i] instanceof Champion) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[z.x][i])) {
                            continue;
                        } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[z.x][i])) {
                            continue;
                        } else {
                            if (checkSaD((Champion) board[z.x][i])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    for (int j = 0; j < ((Champion) board[z.x][i]).getAppliedEffects().size(); j++) {
                                        if (((Champion) board[z.x][i]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                            ((Champion) board[z.x][i]).getAppliedEffects().get(j).remove(((Champion) board[z.x][i]));
                                            return;
                                        }
                                    }

                                } else {
                                    return;
                                }
                            } else if (checkshield((Champion) board[z.x][i])) {
                                for (int j = 0; j < ((Champion) board[z.x][i]).getAppliedEffects().size(); j++) {
                                    if (((Champion) board[z.x][i]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                        ((Champion) board[z.x][i]).getAppliedEffects().get(j).remove(((Champion) board[z.x][i]));
                                        return;
                                    }
                                }
                            } else if (checkdodge((Champion) board[z.x][i])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    if (checkifextra(getCurrentChampion(), (Champion) board[z.x][i])) {
                                        ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                        die((Champion) board[z.x][i]);
                                    } else {
                                        ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                                        die((Champion) board[z.x][i]);
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                if (checkifextra(getCurrentChampion(), (Champion) board[z.x][i])) {
                                    ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                    die((Champion) board[z.x][i]);
                                    return;
                                } else {
                                    ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                                    die((Champion) board[z.x][i]);
                                    return;
                                }
                            }
                        }
                    } else {
                        ((Cover) board[z.x][i]).setCurrentHP((((Cover) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                        die((Cover) board[z.x][i]);
                        return;
                    }

                }
            }
        }
        if (d == Direction.RIGHT) {
            for (int i = z.y + 1; i < z.y + 1 + getCurrentChampion().getAttackRange(); i++) {

                if (i > 4) {
                    break;
                }

                if (board[z.x][i] != null) {

                    if (board[z.x][i] instanceof Champion) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[z.x][i])) {
                            continue;
                        } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[z.x][i])) {
                            continue;
                        } else {
                            if (checkSaD((Champion) board[z.x][i])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    for (int j = 0; j < ((Champion) board[z.x][i]).getAppliedEffects().size(); j++) {
                                        if (((Champion) board[z.x][i]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                            ((Champion) board[z.x][i]).getAppliedEffects().get(j).remove(((Champion) board[z.x][i]));
                                            return;
                                        }
                                    }

                                } else {
                                    return;
                                }
                            } else if (checkshield((Champion) board[z.x][i])) {
                                for (int j = 0; j < ((Champion) board[z.x][i]).getAppliedEffects().size(); j++) {
                                    if (((Champion) board[z.x][i]).getAppliedEffects().get(j).getName().equals(("Shield"))) {
                                        ((Champion) board[z.x][i]).getAppliedEffects().get(j).remove(((Champion) board[z.x][i]));
                                        return;
                                    }
                                }
                            } else if (checkdodge((Champion) board[z.x][i])) {
                                int x = (int) (Math.random() * 2);
                                if (x == 1) {
                                    if (checkifextra(getCurrentChampion(), (Champion) board[z.x][i])) {
                                        ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                        die((Champion) board[z.x][i]);
                                        return;
                                    } else {
                                        ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                                        die((Champion) board[z.x][i]);
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                if (checkifextra(getCurrentChampion(), (Champion) board[z.x][i])) {
                                    ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1.5)));
                                    die((Champion) board[z.x][i]);
                                    return;
                                } else {
                                    ((Champion) board[z.x][i]).setCurrentHP((((Champion) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                                    die((Champion) board[z.x][i]);
                                    return;
                                }
                            }
                        }
                    } else {
                        ((Cover) board[z.x][i]).setCurrentHP((((Cover) board[z.x][i]).getCurrentHP() - (int) (getCurrentChampion().getAttackDamage() * 1)));
                        die((Cover) board[z.x][i]);
                        return;
                    }

                } else {
                    continue;
                }
            }
        }

    }


    public void castAbility(Ability a) throws
            NotEnoughResourcesException, InvalidTargetException, IOException, AbilityUseException, CloneNotSupportedException {
        if (this == null) {
            return;
        }
        if (getCurrentChampion() == null) {
            return;
        }
        if (a.getCurrentCooldown() > 0) {
            throw new AbilityUseException("NO");
        }
        for (int i = 0; i < getCurrentChampion().getAppliedEffects().size(); i++) {
            if (getCurrentChampion().getAppliedEffects().get(i).getName().equals("Silence")) {
                throw new AbilityUseException("NO");
            }
        }
        if (getCurrentChampion().getMana() < a.getManaCost()) {
            throw new NotEnoughResourcesException("NO");
        }
        if (getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints()) {
            throw new NotEnoughResourcesException("NO");
        }

        ArrayList targets = new ArrayList();
        if (a instanceof HealingAbility) {

            if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
                targets.add(getCurrentChampion());
            }
            if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                    for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                        Point fj = firstPlayer.getTeam().get(i).getLocation();
                        Point z = getCurrentChampion().getLocation();
                        int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                        if (kf<=a.getCastRange())
                            targets.add(firstPlayer.getTeam().get(i));
                    }
                } else {
                    for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                        Point fj = secondPlayer.getTeam().get(i).getLocation();
                        Point z = getCurrentChampion().getLocation();
                        int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                        if (kf<=a.getCastRange())
                            targets.add(secondPlayer.getTeam().get(i));
                    }
                }
            }
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                Point z = getCurrentChampion().getLocation();
                for (int i = z.y - 1; i <= z.y + 1; i++) {
                    for (int j = z.x - 1; j <= z.x + 1; j++) {
                        if (i < 0 || i > 4 || j < 0 || j > 4) {
                            continue;
                        }
                        if (!z.equals(new Point(j, i)))
                            if (board[j][i] instanceof Champion) {
                                if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                    targets.add((Champion) board[j][i]);
                                } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                    targets.add((Champion) board[j][i]);
                                } else if (board[j][i] == null) {
                                    continue;
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
            if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                    for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                        int c = 0;
                        for (int j = 0; j < secondPlayer.getTeam().get(i).getAppliedEffects().size(); j++) {
                            if (secondPlayer.getTeam().get(i).getAppliedEffects().get(j).getName().equals("Shield")) {
                                secondPlayer.getTeam().get(i).getAppliedEffects().get(j).remove(secondPlayer.getTeam().get(i));
                                c++;
                            }
                        }
                        if (c == 0) {
                            Point fj = secondPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                            targets.add(secondPlayer.getTeam().get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                        int c = 0;
                        for (int j = 0; j < firstPlayer.getTeam().get(i).getAppliedEffects().size(); j++) {
                            if (firstPlayer.getTeam().get(i).getAppliedEffects().get(j).getName().equals("Shield")) {
                                firstPlayer.getTeam().get(i).getAppliedEffects().get(j).remove(firstPlayer.getTeam().get(i));
                                c++;
                            }
                        }
                        if (c == 0) {
                            Point fj = firstPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                                targets.add(firstPlayer.getTeam().get(i));
                        }
                    }
                }
            }
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                Point z = getCurrentChampion().getLocation();
                for (int i = z.y - 1; i <= z.y + 1; i++) {
                    for (int j = z.x - 1; j <= z.x + 1; j++) {
                        if (i < 0 || i > 4 || j < 0 || j > 4) {
                            continue;
                        }

                        if (!z.equals(new Point(j, i)))
                            if (board[j][i] instanceof Champion) {
                                if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                    int c = 0;
                                    for (int k = 0; k < ((Champion) board[j][i]).getAppliedEffects().size(); k++) {
                                        if (((Champion) board[j][i]).getAppliedEffects().get(k).getName().equals("Shield")) {
                                            ((Champion) board[j][i]).getAppliedEffects().get(k).remove(((Champion) board[j][i]));
                                            c++;
                                        }
                                    }
                                    if (c == 0) {
                                        targets.add((Champion) board[j][i]);
                                    }
                                } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                    int c = 0;
                                    for (int k = 0; k < ((Champion) board[j][i]).getAppliedEffects().size(); k++) {
                                        if (((Champion) board[j][i]).getAppliedEffects().get(k).getName().equals("Shield")) {
                                            ((Champion) board[j][i]).getAppliedEffects().get(k).remove(((Champion) board[j][i]));
                                            c++;
                                        }
                                    }
                                    if (c == 0) {
                                        targets.add((Champion) board[j][i]);
                                    }
                                }
                            } else if (board[j][i] == null) {
                                continue;
                            } else {

                                    targets.add((Cover) board[j][i]);
                            }

                    }
                }
            }
        }
        if (a instanceof CrowdControlAbility) {
            if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                throw new InvalidTargetException("NO");
            } else if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                targets.add(getCurrentChampion());
            }
            if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                            Point fj = secondPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                                targets.add(secondPlayer.getTeam().get(i));
                        }
                    } else {
                        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                            Point fj = firstPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                                targets.add(firstPlayer.getTeam().get(i));
                        }
                    }
                }
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                    if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                            Point fj = firstPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                                targets.add(firstPlayer.getTeam().get(i));
                        }
                    } else {
                        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                            Point fj = secondPlayer.getTeam().get(i).getLocation();
                            Point z = getCurrentChampion().getLocation();
                            int kf=Math.abs(fj.x-z.x)+Math.abs(fj.y-z.y);
                            if (kf<=a.getCastRange())
                                targets.add(secondPlayer.getTeam().get(i));
                        }
                    }
                }

            }
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    Point z = getCurrentChampion().getLocation();
                    for (int i = z.y - 1; i <= z.y + 1; i++) {
                        for (int j = z.x - 1; j <= z.x + 1; j++) {
                            if (i < 0 || i > 4 || j < 0 || j > 4) {
                                continue;
                            }
                            if (!z.equals(new Point(j, i)))
                                if (board[j][i] instanceof Champion) {
                                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                        targets.add((Champion) board[j][i]);
                                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                        targets.add((Champion) board[j][i]);
                                    } else if (board[j][i] == null) {
                                        continue;
                                    }
                                }
                        }

                    }
                }

                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                    Point z = getCurrentChampion().getLocation();
                    for (int i = z.y - 1; i <= z.y + 1; i++) {
                        for (int j = z.x - 1; j <= z.x + 1; j++) {
                            if (i < 0 || i > 4 || j < 0 || j > 4) {
                                continue;
                            }
                            if (!z.equals(new Point(j, i)))
                                if (board[j][i] instanceof Champion) {
                                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                        targets.add((Champion) board[j][i]);
                                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                        targets.add((Champion) board[j][i]);
                                    } else if (board[j][i] == null) {
                                        continue;
                                    }
                                }
                        }
                    }
                }
            }
        }
        a.setCurrentCooldown(a.getBaseCooldown());
        a.execute(targets);
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) instanceof Champion)
                die((Champion) targets.get(i));
            if (targets.get(i) instanceof Cover)
                die((Cover) targets.get(i));
        }
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
    }


    public void castAbility(Ability a, Direction d) throws
            NotEnoughResourcesException, InvalidTargetException, UnallowedMovementException, IOException, AbilityUseException, CloneNotSupportedException {

        if (getCurrentChampion().getMana() < a.getManaCost())
            throw new NotEnoughResourcesException("NO MANA!");
        else if (getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints())
            throw new NotEnoughResourcesException("NO ACTION POINTS");
        else if (a.getCurrentCooldown() > 0) {
            throw new AbilityUseException("ON COOLDOWN");
        }
        for (int i = 0; i < getCurrentChampion().getAppliedEffects().size(); i++) {
            if (getCurrentChampion().getAppliedEffects().get(i).getName().equals("Silence")) {
                throw new AbilityUseException("SILENCED");
            }
        }

        a.setCurrentCooldown(a.getBaseCooldown());

        ArrayList targets = new ArrayList();
        Point temp = getCurrentChampion().getLocation();
        if (a instanceof DamagingAbility) {
            if (d == Direction.UP) {
                temp.x++;
                for (int i = 0; i < a.getCastRange(); i++, temp.x++) {
                    if (temp.x >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (getBoard()[temp.x][temp.y] instanceof Champion && checkshield((Champion) getBoard()[temp.x][temp.y])) {
                        for (int j = 0; j < ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().size(); j++) {
                            if (((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).getName().equals("Shield")) {
                                ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).remove(((Champion) (getBoard()[temp.x][temp.y])));
                                return;
                            }
                        }
                    }
                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (getBoard()[temp.x][temp.y] instanceof Cover)
                        targets.add(getBoard()[temp.x][temp.y]);

                }
            }

            if (d == Direction.DOWN) {
                temp.x--;
                for (int i = 0; i < a.getCastRange(); i++, temp.x--) {
                    if (temp.x < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (getBoard()[temp.x][temp.y] instanceof Champion && checkshield((Champion) getBoard()[temp.x][temp.y])) {
                        for (int j = 0; j < ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().size(); j++) {
                            if (((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).getName().equals("Shield")) {
                                ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).remove(((Champion) (getBoard()[temp.x][temp.y])));
                                return;
                            }

                        }

                    }
                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (getBoard()[temp.x][temp.y] instanceof Cover)
                        targets.add(getBoard()[temp.x][temp.y]);
                }
            }

            if (d == Direction.RIGHT) {
                temp.y++;
                for (int i = 0; i < a.getCastRange(); i++, temp.y++) {
                    if (temp.y >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (getBoard()[temp.x][temp.y] instanceof Champion && checkshield((Champion) getBoard()[temp.x][temp.y])) {
                        for (int j = 0; j < ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().size(); j++) {
                            if (((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).getName().equals("Shield")) {
                                ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).remove(((Champion) (getBoard()[temp.x][temp.y])));
                                return;
                            }
                        }

                    }
                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (getBoard()[temp.x][temp.y] instanceof Cover)
                        targets.add(getBoard()[temp.x][temp.y]);

                }
            }

            if (d == Direction.LEFT) {
                temp.y--;
                for (int i = 0; i < a.getCastRange(); i++, temp.y--) {
                    if (temp.y < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (getBoard()[temp.x][temp.y] instanceof Champion && checkshield((Champion) getBoard()[temp.x][temp.y])) {
                        for (int j = 0; j < ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().size(); j++) {
                            if (((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).getName().equals("Shield")) {
                                ((Champion) (getBoard()[temp.x][temp.y])).getAppliedEffects().get(j).remove(((Champion) (getBoard()[temp.x][temp.y])));
                                return;
                            }

                        }
                    }
                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (getBoard()[temp.x][temp.y] instanceof Cover)
                        targets.add(getBoard()[temp.x][temp.y]);

                }
            }
        }

        if (a instanceof HealingAbility) {
            if (d == Direction.UP) {
                temp.x++;
                for (int i = 0; i < a.getCastRange(); i++, temp.x++) {
                    if (temp.x >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;

                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                }
            }
            if (d == Direction.DOWN) {
                temp.x--;
                for (int i = 0; i < a.getCastRange(); i++, temp.x--) {
                    if (temp.x < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;

                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);
                }
            }
            if (d == Direction.RIGHT) {
                temp.y++;
                for (int i = 0; i < a.getCastRange(); i++, temp.y++) {
                    if (temp.y >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;

                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);
                }
            }
            if (d == Direction.LEFT) {
                temp.y--;
                for (int i = 0; i < a.getCastRange(); i++, temp.y--) {
                    if (temp.y < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;

                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);

                    if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                        if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                            targets.add(getBoard()[temp.x][temp.y]);
                }
            }
        }
        if (a instanceof CrowdControlAbility) {
            if (d == Direction.UP) {
                temp.x++;
                for (int i = 0; i < a.getCastRange(); i++, temp.x++) {
                    if (temp.x >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                }
            }
            if (d == Direction.DOWN) {
                temp.x--;
                for (int i = 0; i < a.getCastRange(); i++, temp.x--) {
                    if (temp.x < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                }
            }
            if (d == Direction.RIGHT) {
                temp.y++;
                for (int i = 0; i < a.getCastRange(); i++, temp.y++) {
                    if (temp.y >= 5)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                }
            }
            if (d == Direction.LEFT) {
                temp.y--;
                for (int i = 0; i < a.getCastRange(); i++, temp.y--) {
                    if (temp.y < 0)
                        break;
                    if (getBoard()[temp.x][temp.y] == null)
                        continue;
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                    if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                        if (firstPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (secondPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);

                        if (secondPlayer.getTeam().contains(getCurrentChampion()) && getBoard()[temp.x][temp.y] instanceof Champion)
                            if (firstPlayer.getTeam().contains(getBoard()[temp.x][temp.y]))
                                targets.add(getBoard()[temp.x][temp.y]);
                    }
                }
            }
        }
        a.execute(targets);
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) instanceof Champion)
                die((Champion) targets.get(i));
            if (targets.get(i) instanceof Cover)
                die((Cover) targets.get(i));
        }
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
    }

    ////////////////////////////END
    public void castAbility(Ability a, int x, int y) throws
            NotEnoughResourcesException, InvalidTargetException, UnallowedMovementException, IOException, AbilityUseException, CloneNotSupportedException {

            if (a.getCurrentCooldown() > 0) {
                throw new AbilityUseException("NO");
            }



        if (getCurrentChampion().getMana() < a.getManaCost()) {
            throw new NotEnoughResourcesException("No");
        }

        if (getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints()) {
            throw new NotEnoughResourcesException("No");
        }

        for (int i = 0; i < getCurrentChampion().getAppliedEffects().size(); i++) {
            if (getCurrentChampion().getAppliedEffects().get(i).getName().equals("Silence")) {
                throw new AbilityUseException("NO");
            }
        }
        if(board[x][y]==null){
            throw new InvalidTargetException("NO");
        }

        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());

        if (Math.abs(x - getCurrentChampion().getLocation().x) + Math.abs(y - getCurrentChampion().getLocation().y) > a.getCastRange()) {
            throw new AbilityUseException("NO");
        }
        if (a.getCurrentCooldown() > 0) {
            throw new AbilityUseException("NO");
        }
        ArrayList target = new ArrayList();
        if (a instanceof HealingAbility) {
            if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                target.add((Champion) board[x][y]);
            } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                target.add((Champion) board[x][y]);
            } else if (board[x][y] instanceof Cover){
                throw new InvalidTargetException("NO");
            }
        }
        if (a instanceof DamagingAbility) {
            if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                target.add((Champion) board[x][y]);
            } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                target.add((Champion) board[x][y]);
            } else if (board[x][y] instanceof Cover) {
                target.add((Cover) board[x][y]);
            }
            else{
                throw new InvalidTargetException("NO");
            }
        }
        if (a instanceof CrowdControlAbility) {
            if (board[x][y] instanceof Cover) {
                throw new InvalidTargetException();
            }
            if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {

                if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                    target.add((Champion) board[x][y]);
                } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                    target.add((Champion) board[x][y]);
                } else {
                    throw new InvalidTargetException();
                }
            }
            if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                    target.add((Champion) board[x][y]);
                } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                    target.add((Champion) board[x][y]);
                } else {
                    throw new InvalidTargetException();
                }
            }
        }
        a.setCurrentCooldown(a.getBaseCooldown());
        a.execute(target);
        for (int i = 0; i < target.size(); i++) {
            if (target.get(i) instanceof Champion)
                die((Champion) target.get(i));
            if (target.get(i) instanceof Cover)
                die((Cover) target.get(i));
        }
    }

    public ArrayList checkl(Champion x, Player y, int i) {
        ArrayList<Champion> a = new ArrayList();

        if (x instanceof Hero) {
            return y.getTeam();
        } else if (x instanceof Villain) {
            if (i == 1) {
                return secondPlayer.getTeam();
            } else {
                return firstPlayer.getTeam();
            }
        } else {

            for (int k = 0; k < getFirstPlayer().getTeam().size(); k++) {
                if (!getFirstPlayer().getTeam().get(k).getName().equals(firstPlayer.getLeader().getName()))

                    a.add(getFirstPlayer().getTeam().get(k));
            }
            for (int k = 0; k < getSecondPlayer().getTeam().size(); k++) {
                if (!getSecondPlayer().getTeam().get(k).getName().equals(secondPlayer.getLeader().getName()))
                    a.add(getSecondPlayer().getTeam().get(k));
            }

        }
        return a;

    }

    public void useLeaderAbility() throws LeaderAbilityAlreadyUsedException, IOException, LeaderNotCurrentException, CloneNotSupportedException {

        if (isFirstLeaderAbilityUsed()) {
            throw new LeaderAbilityAlreadyUsedException("u already used leader ability");
        }
        if (isSecondLeaderAbilityUsed()) {
            throw new LeaderAbilityAlreadyUsedException("u already used leader ability");

        }
        if ((!getCurrentChampion().getName().equals((getFirstPlayer().getLeader()).getName())) && ((!getCurrentChampion().getName().equals((getSecondPlayer().getLeader()).getName()))))
            throw new LeaderNotCurrentException();
        boolean which = getFirstPlayer().getTeam().contains(getCurrentChampion()) ? true : false;
        if (which) {

            getCurrentChampion().useLeaderAbility(checkl(getCurrentChampion(), getFirstPlayer(), 1));
            firstLeaderAbilityUsed = true;
        } else {
            getCurrentChampion().useLeaderAbility(checkl(getCurrentChampion(), getSecondPlayer(), 2));
            secondLeaderAbilityUsed = true;
        }
    }


    public void endTurn() throws IOException {
        turnOrder.remove();
        if (turnOrder.isEmpty())
            prepareChampionTurns();
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getMaxActionPointsPerTurn());

        for (int i = 0; i < getCurrentChampion().getAppliedEffects().size(); i++) {
            getCurrentChampion().getAppliedEffects().get(i).setDuration(getCurrentChampion().getAppliedEffects().get(i).getDuration() - 1);
            if (getCurrentChampion().getAppliedEffects().get(i).getDuration() == 0)
                getCurrentChampion().getAppliedEffects().get(i).remove(getCurrentChampion());
        }

        for (int i = 0; i < getCurrentChampion().getAbilities().size(); i++) {
            getCurrentChampion().getAbilities().get(i).setCurrentCooldown(getCurrentChampion().getAbilities().get(i).getCurrentCooldown() - 1);
        }

        while (getCurrentChampion().getCondition().equals(Condition.INACTIVE) ) {
            if(!turnOrder.isEmpty())
                turnOrder.remove();
            else{
                prepareChampionTurns();
            }
        }
    }

    public void prepareChampionTurns() {
        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
            if (firstPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT) {
                turnOrder.insert(firstPlayer.getTeam().get(i));
            }
        }
        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
            if (secondPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT) {
                turnOrder.insert(secondPlayer.getTeam().get(i));
            }
        }
    }


}



