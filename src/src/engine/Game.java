package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        prepareChampionTurns();
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

    public static boolean check2(Champion c, Champion k) {

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
            throw new NotEnoughResourcesException("NO!");
        }
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
    }

    public boolean isSecondLeaderAbilityUsed() {
        return secondLeaderAbilityUsed;
    }

    public boolean check(Object c, int o) {

        if (c instanceof Champion) {
            if (((Champion) c).getCurrentHP() - o > 0) {
                ((Champion) c).setCurrentHP(((Champion) c).getCurrentHP() - o);
                return true;
            } else {
                ((Champion) c).setCondition(Condition.KNOCKEDOUT);
                ((Champion) c).setCurrentHP(0);
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

    public boolean extra(Object c, int o) {
        if (((Champion) c).getCurrentHP() - 0.5 * o > 0) {
            ((Champion) c).setCurrentHP(((Champion) c).getCurrentHP() - (int) (0.5 * o));
            return true;
        } else {
            ((Champion) c).setCurrentHP(0);
            ((Champion) c).setCondition(Condition.KNOCKEDOUT);
            return false;
        }
    }

    public void attack(Direction d) throws AbilityUseException, ChampionDisarmedException, NotEnoughResourcesException, IOException {
        Point r = getCurrentChampion().getLocation();
        int ar = getCurrentChampion().getAttackRange();
        int o = getCurrentChampion().getAttackDamage();
        if (getCurrentChampion().getCurrentActionPoints() < 2) {
            throw new NotEnoughResourcesException("not enough");
        }
        for (int i = getCurrentChampion().getAppliedEffects().size() - 1; i >= 0; i--) {
            Effect g = getCurrentChampion().getAppliedEffects().get(i);
            if (g.getName().equals("Disarm")) {
                throw new ChampionDisarmedException("Champion is diarmed");

            }

            if (g instanceof Stun) {
                throw new AbilityUseException("Champion is Inactive rn");
            }


        }  getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 2);
        if (d == Direction.UP) {
            int z = r.x + ar;
            for (int i = r.x; i <= z; i++) {
                if (i > 4) {
                    break;
                }
                Object c = board[i][r.y];

                if (c != null) {
                    if (c instanceof Champion) {//check cases of shield or dodge
                        for (int j = ((Champion) c).getAppliedEffects().size() - 1; i >= 0; i--) {
                            Effect e = ((Champion) c).getAppliedEffects().get(i);
                            if (e instanceof Shield) {
                                ((Shield) getCurrentChampion().getAppliedEffects().get(i)).remove(getCurrentChampion());
                                return;
                            } else if (e instanceof Dodge) {
                                int q = (int) (Math.random() * 2);
                                if (q == 1) {//extra damage or normal damage and replace with null if no hp remauled after damage
                                    if (check2(getCurrentChampion(), (Champion) (c))) {
                                        if (!check(c, o)) {
                                            c = null;
                                        }
                                        if (!extra(c, o)) {
                                            c = null;
                                        }
                                        return;
                                    } else {
                                        if (!check(c, o)) {
                                            c = null;
                                            return;
                                        }
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                        //so the c(obj  on the booard with cast range) does not have neither dodge nor shiled s apply on it wheter normal or speciall attack
                        //special attack
                        if (check2(getCurrentChampion(), (Champion) (c))) {

                            if (!check(c, o)) {
                                c = null;
                            }
                            if (!extra(c, o)) {
                                c = null;
                            }
                            //normal attack
                        } else {
                            if (!check(c, o)) {
                                c = null;
                            }


                        }}
  //cover apply normal attack
                    else{
                            if (!check(c, o)) ;
                            {
                                c = null;
                            }
                        }

                }

            }} else if (d == Direction.DOWN) {

                int z = r.x - ar;
                for (int i = r.x; i >= z; i--) {
                    if (i < 0) {
                        break;
                    }
                    Object c = board[i][r.y];

                    if (c != null) {
                        if (c instanceof Champion) {//check cases of shield or dodge
                            for (int j = ((Champion) c).getAppliedEffects().size() - 1; i >= 0; i--) {
                                Effect e = ((Champion) c).getAppliedEffects().get(i);
                                if (e instanceof Shield) {
                                    ((Shield) getCurrentChampion().getAppliedEffects().get(i)).remove(getCurrentChampion());
                                    return;
                                } else if (e instanceof Dodge) {
                                    int q = (int) (Math.random() * 2);
                                    if (q == 1) {//extra damage or normal damage and replace with null if no hp remauled after damage
                                        if (check2(getCurrentChampion(), (Champion) (c))) {
                                            if (!check(c, o)) {
                                                c = null;
                                            }
                                            if (!extra(c, o)) {
                                                c = null;
                                            }
                                            return;
                                        } else {
                                            if (!check(c, o)) {
                                                c = null;
                                                return;
                                            }
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                            //so the c(obj  on the booard with cast range) does not have neither dodge nor shiled s apply on it wheter normal or speciall attack
                            //special attack
                            if (check2(getCurrentChampion(), (Champion) (c))) {

                                if (!check(c, o)) {
                                    c = null;
                                }
                                if (!extra(c, o)) {
                                    c = null;
                                }
                                //normal attack
                            } else {
                                if (!check(c, o)) {
                                    c = null;
                                }


                            }}
                        //cover apply normal attack
                        else{
                            if (!check(c, o)) ;
                            {
                                c = null;
                            }
                        }

                    }

                }
            } else if (d == Direction.RIGHT) {
                int z = r.y + ar;

                for (int i = r.y; i <= z; i++) {
                    if (i > 4) {
                        break;
                    }
                    Object c = board[r.x][i];

                    if (c != null) {
                        if (c instanceof Champion) {//check cases of shield or dodge
                            for (int j = ((Champion) c).getAppliedEffects().size() - 1; i >= 0; i--) {
                                Effect e = ((Champion) c).getAppliedEffects().get(i);
                                if (e instanceof Shield) {
                                    ((Shield) getCurrentChampion().getAppliedEffects().get(i)).remove(getCurrentChampion());
                                    return;
                                } else if (e instanceof Dodge) {
                                    int q = (int) (Math.random() * 2);
                                    if (q == 1) {//extra damage or normal damage and replace with null if no hp remauled after damage
                                        if (check2(getCurrentChampion(), (Champion) (c))) {
                                            if (!check(c, o)) {
                                                c = null;
                                            }
                                            if (!extra(c, o)) {
                                                c = null;
                                            }
                                            return;
                                        } else {
                                            if (!check(c, o)) {
                                                c = null;
                                                return;
                                            }
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                            //so the c(obj  on the booard with cast range) does not have neither dodge nor shiled s apply on it wheter normal or speciall attack
                            //special attack
                            if (check2(getCurrentChampion(), (Champion) (c))) {

                                if (!check(c, o)) {
                                    c = null;
                                }
                                if (!extra(c, o)) {
                                    c = null;
                                }
                                //normal attack
                            } else {
                                if (!check(c, o)) {
                                    c = null;
                                }


                            }}
                        //cover apply normal attack
                        else{
                            if (!check(c, o)) ;
                            {
                                c = null;
                            }
                        }

                    }

                }
            } else {
                int z = r.y - ar;

                for (int i = r.y; i >= z; i--) {
                    if (i < 0) {
                        break;
                    }
                    Object c = board[r.x][i];
                    if (c != null) {
                        if (c instanceof Champion) {//check cases of shield or dodge
                            for (int j = ((Champion) c).getAppliedEffects().size() - 1; i >= 0; i--) {
                                Effect e = ((Champion) c).getAppliedEffects().get(i);
                                if (e instanceof Shield) {
                                    ((Shield) getCurrentChampion().getAppliedEffects().get(i)).remove(getCurrentChampion());
                                    return;
                                } else if (e instanceof Dodge) {
                                    int q = (int) (Math.random() * 2);
                                    if (q == 1) {//extra damage or normal damage and replace with null if no hp remauled after damage
                                        if (check2(getCurrentChampion(), (Champion) (c))) {
                                            if (!check(c, o)) {
                                                c = null;
                                            }
                                            if (!extra(c, o)) {
                                                c = null;
                                            }
                                            return;
                                        } else {
                                            if (!check(c, o)) {
                                                c = null;
                                                return;
                                            }
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                            //so the c(obj  on the booard with cast range) does not have neither dodge nor shiled s apply on it wheter normal or speciall attack
                            //special attack
                            if (check2(getCurrentChampion(), (Champion) (c))) {

                                if (!check(c, o)) {
                                    c = null;
                                }
                                if (!extra(c, o)) {
                                    c = null;
                                }
                                //normal attack
                            } else {
                                if (!check(c, o)) {
                                    c = null;
                                }


                            }}
                        //cover apply normal attack
                        else{
                            if (!check(c, o)) ;
                            {
                                c = null;
                            }
                        }

                    }

                }}

        }


    public void castAbility(Ability a) throws
            NotEnoughResourcesException, InvalidTargetException, IOException, AbilityUseException {
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
                for (int i = z.y - 1; i <= z.y + 1; i++) {
                    for (int j = z.x - 1; j <= z.x + 1; j++) {
                        if (i < 0 || i > 4 || j < 0 || j > 4) {
                            continue;
                        }
                        if (z != new Point(j, i))
                            if (board[j][i] instanceof Champion) {
                                if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                    ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP() + ((HealingAbility) a).getHealAmount());
                                } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                    ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP() + ((HealingAbility) a).getHealAmount());
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
                            secondPlayer.getTeam().get(i).setCurrentHP(secondPlayer.getTeam().get(i).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                            if (secondPlayer.getTeam().get(i).getCurrentHP() == 0) {
                                secondPlayer.getTeam().get(i).setCondition(Condition.KNOCKEDOUT);
                                Point z = secondPlayer.getTeam().get(i).getLocation();
                                board[z.x][z.y] = null;
                            }
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
                            firstPlayer.getTeam().get(i).setCurrentHP(firstPlayer.getTeam().get(i).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                            if (secondPlayer.getTeam().get(i).getCurrentHP() == 0) {
                                secondPlayer.getTeam().get(i).setCondition(Condition.KNOCKEDOUT);
                                Point z = secondPlayer.getTeam().get(i).getLocation();
                                board[z.x][z.y] = null;
                            }
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

                        if (z != new Point(j, i))
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
                                        ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                                        if (((Champion) board[j][i]).getCurrentHP() == 0) {
                                            ((Champion) board[j][i]).setCondition(Condition.KNOCKEDOUT);
                                            Point u = ((Champion) board[j][i]).getLocation();
                                            board[u.x][u.y] = null;
                                        }
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
                                        ((Champion) board[j][i]).setCurrentHP(((Champion) board[j][i]).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                                        if (((Champion) board[j][i]).getCurrentHP() == 0) {
                                            ((Champion) board[j][i]).setCondition(Condition.KNOCKEDOUT);
                                            Point u = ((Champion) board[j][i]).getLocation();
                                            board[u.x][u.y] = null;
                                        }
                                    }
                                }
                            } else if (board[j][i] == null) {
                                continue;
                            } else {
                                ((Cover) board[j][i]).setCurrentHP(((Cover) board[j][i]).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                                if ((int) ((Cover) getBoard()[j][i]).getCurrentHP() == 0)
                                    getBoard()[j][i] = null;

                            }

                    }
                }
            }
        }
        if (a instanceof CrowdControlAbility) {
            if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                throw new InvalidTargetException("NO");
            } else if (a.getCastArea() == AreaOfEffect.SELFTARGET && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                ((CrowdControlAbility) a).getEffect().apply(getCurrentChampion());
            }
            if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                            ((CrowdControlAbility) a).getEffect().apply(secondPlayer.getTeam().get(i));
                        }
                    } else {
                        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                            ((CrowdControlAbility) a).getEffect().apply(firstPlayer.getTeam().get(i));
                        }
                    }
                }
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
                    if (firstPlayer.getTeam().contains(getCurrentChampion())) {
                        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
                            ((CrowdControlAbility) a).getEffect().apply(firstPlayer.getTeam().get(i));
                        }
                    } else {
                        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
                            ((CrowdControlAbility) a).getEffect().apply(secondPlayer.getTeam().get(i));
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
                            if (z != new Point(j, i))
                                if (board[j][i] instanceof Champion) {
                                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[j][i]);
                                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[j][i]);
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
                            if (z != new Point(j, i))
                                if (board[j][i] instanceof Champion) {
                                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[j][i])) {
                                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[j][i]);
                                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[j][i])) {
                                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[j][i]);
                                    } else if (board[j][i] == null) {
                                        continue;
                                    }
                                }
                        }

                    }
                }
            }
        }
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
        a.setCurrentCooldown(a.getBaseCooldown());
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
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
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
    }

    ////////////////////////////END
    public void castAbility(Ability a, int x, int y) throws
            NotEnoughResourcesException, InvalidTargetException, UnallowedMovementException, IOException, AbilityUseException {

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
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());

        if (Math.abs(x - getCurrentChampion().getLocation().x) + Math.abs(y - getCurrentChampion().getLocation().y) > a.getCastRange()) {
            throw new AbilityUseException("NO");
        }
        if (a.getCurrentCooldown() > 0) {
            throw new AbilityUseException("NO");
        }
        if (a instanceof HealingAbility) {
            if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                ((Champion) board[x][y]).setCurrentHP(((Champion) (board[x][y])).getCurrentHP() + ((HealingAbility) a).getHealAmount());
            } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                ((Champion) board[x][y]).setCurrentHP(((Champion) (board[x][y])).getCurrentHP() + ((HealingAbility) a).getHealAmount());
            } else {
                throw new InvalidTargetException("NO");
            }
        }
        if (a instanceof DamagingAbility) {
            if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                ((Champion) board[x][y]).setCurrentHP(((Champion) (board[x][y])).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                if (((Champion) board[x][y]).getCurrentHP() == 0) {
                    ((Champion) board[x][y]).setCondition(Condition.KNOCKEDOUT);
                    board[x][y] = null;
                }
            } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                ((Champion) board[x][y]).setCurrentHP(((Champion) (board[x][y])).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                if (((Champion) board[x][y]).getCurrentHP() == 0) {
                    ((Champion) board[x][y]).setCondition(Condition.KNOCKEDOUT);
                    board[x][y] = null;
                }

            } else if (board[x][y] instanceof Cover) {
                ((Cover) board[x][y]).setCurrentHP(((Cover) (board[x][y])).getCurrentHP() - ((DamagingAbility) a).getDamageAmount());
                if (((Cover) board[x][y]).getCurrentHP() == 0) {
                    board[x][y] = null;
                }
            }
            if (a instanceof CrowdControlAbility) {
                if (board[x][y] instanceof Cover) {
                    throw new InvalidTargetException();
                }
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {

                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[x][y]);
                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[x][y]);
                    } else {
                        throw new InvalidTargetException();
                    }
                }
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    if (firstPlayer.getTeam().contains(getCurrentChampion()) && secondPlayer.getTeam().contains(board[x][y])) {
                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[x][y]);
                    } else if (secondPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(board[x][y])) {
                        ((CrowdControlAbility) a).getEffect().apply((Champion) board[x][y]);
                    } else {
                        throw new InvalidTargetException();
                    }
                }
            }
        }
        a.setCurrentCooldown(a.getBaseCooldown());
    }

    public ArrayList checkl(Champion x, Player y, int i) {
        ArrayList<Champion> a = new ArrayList();

        if (x instanceof Hero) {
            a = y.getTeam();
        } else if (x instanceof Villain) {
            if (i == 1) {
                a = secondPlayer.getTeam();
            } else {
                a = firstPlayer.getTeam();
            }
        } else {
            for (int k = 0; k < getFirstPlayer().getTeam().size(); k++) {
                if (!getFirstPlayer().getTeam().get(k).getName().equals(firstPlayer.getLeader().getName()))
                    a.add(getFirstPlayer().getTeam().get(k));
            }
            for (int k = 0; k < getSecondPlayer().getTeam().size(); k++) {
                if (!getFirstPlayer().getTeam().get(k).getName().equals(secondPlayer.getLeader().getName()))
                    a.add(getFirstPlayer().getTeam().get(k));
            }
        }
        return a;
    }

    public void useLeaderAbility() throws LeaderAbilityAlreadyUsedException, IOException {
        Champion x = getCurrentChampion();
        boolean which = getFirstPlayer().getTeam().contains(x) ? true : false;
        if (which) {
            if (x.getName().equals((getFirstPlayer().getLeader()).getName())) {
                x.useLeaderAbility(checkl(x, getFirstPlayer(), 1));

            } else if (x.getName().equals((getSecondPlayer().getLeader()).getName()))
                x.useLeaderAbility(checkl(x, getSecondPlayer(), 2));
        }
        if (isFirstLeaderAbilityUsed()) {
            throw new LeaderAbilityAlreadyUsedException("u already used leader ability");
        }
        if (isSecondLeaderAbilityUsed()) {
            throw new LeaderAbilityAlreadyUsedException("u already used leader ability");

        }
    }

    public void endTurn() throws IOException {
        turnOrder.remove();
        if (turnOrder.size() == 0) {
            prepareChampionTurns();
        }
        while (getCurrentChampion().getCondition() == Condition.INACTIVE) {
            turnOrder.remove();
        }
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getMaxActionPointsPerTurn());
        for (int i = 0; i < getCurrentChampion().getAppliedEffects().size(); i++) {
            getCurrentChampion().getAppliedEffects().get(i).setDuration(getCurrentChampion().getAppliedEffects().get(i).getDuration() - 1);
            if (getCurrentChampion().getAppliedEffects().get(i).getDuration() == 0)
                getCurrentChampion().getAppliedEffects().get(i).remove(getCurrentChampion());
        }
        for (int i = 0; i < getCurrentChampion().getAbilities().size(); i++) {
            getCurrentChampion().getAbilities().get(i).setCurrentCooldown(getCurrentChampion().getAbilities().get(i).getCurrentCooldown() - 1);
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



