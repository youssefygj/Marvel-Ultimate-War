package model.world;

import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.effects.Effect;
import model.effects.Stun;

import java.io.IOException;
import java.util.ArrayList;

public class AntiHero extends Champion {

    public AntiHero(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActionsPerTurn, speed, attackRange, attackDamage);

    }


    public void useLeaderAbility(ArrayList<Champion> targets) throws IOException, CloneNotSupportedException {
        Stun x = new Stun(2);
        CrowdControlAbility y = new CrowdControlAbility("test",1,1,1, AreaOfEffect.DIRECTIONAL,1,x);
        ArrayList<Damageable> targ= new ArrayList<Damageable>();
        for(int i=0;i<targets.size();i++){
            targ.add((Damageable) targets.get(i));
        }
        y.execute(targ);

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
