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
                if (targets.get(i).getCurrentHP() <((int)(targets.get(i).getMaxHP()*0.3))){
                    (targets.get(i)).setCondition(Condition.KNOCKEDOUT);}
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
