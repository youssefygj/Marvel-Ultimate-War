package model.world;

import model.effects.EffectType;
import model.effects.Embrace;

import java.io.IOException;
import java.util.ArrayList;

public class Hero extends Champion {

    public Hero(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActionsPerTurn, speed, attackRange, attackDamage);
    }

    @Override


    public void useLeaderAbility(ArrayList<Champion> targets) throws IOException {
        Embrace temp = new Embrace(2);
        for (int i = 0; i < targets.size(); i++) {
            temp.apply(targets.get(i));
            for (int j = 0; j < targets.get(i).getAppliedEffects().size(); j++)
                if (targets.get(i).getAppliedEffects().get(j).getType() == EffectType.DEBUFF)
                    targets.get(i).getAppliedEffects().get(j).remove(targets.get(i));
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
    }
}
