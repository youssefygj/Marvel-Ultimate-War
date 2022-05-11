package model.world;

import java.io.IOException;
import java.util.ArrayList;

public class Villain extends Champion {

    public Villain(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActionsPerTurn, speed, attackRange, attackDamage);
    }

    @Override
    public void useLeaderAbility(ArrayList<Champion> targets) throws IOException {

            for (int i = 0; i < targets.size(); i++) {
                if (targets.get(i).getCurrentHP() < ((targets.get(i).getMaxHP() * 30) / 100))
                    targets.get(i).setCondition(Condition.KNOCKEDOUT);
            }
        }


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
