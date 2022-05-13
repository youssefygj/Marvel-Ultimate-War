package model.world;

import model.effects.Stun;

import java.io.IOException;
import java.util.ArrayList;

public class Villain extends Champion {

    public Villain(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActionsPerTurn, speed, attackRange, attackDamage);
    }

    @Override
    public void useLeaderAbility(ArrayList<Champion> targets) throws IOException {

        for (int i = 0; i < targets.size(); i++) {
            Champion c=targets.get(i);
            if(c.getCurrentHP()<c.getMaxHP()*0.3){
                c.setCondition(Condition.KNOCKEDOUT);
                c.setCurrentActionPoints(0);
            }
        }
    }


    @Override
    public int compareTo(Object o) {
        int x= ((Champion) o).getSpeed();
        if (this.getSpeed() >x) {
            return -1;
        } else if (this.getSpeed() < x) {
            return 1;
        } else {
            Champion z=  ((Champion)o);
            return (this.getName().compareTo(z.getName()));
        }
    }}
