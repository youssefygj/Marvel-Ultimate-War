package model.world;

public class Hero extends Champion {

    public Hero(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActionsPerTurn, speed, attackRange, attackDamage);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
