package model.effects;

import model.world.Champion;
import model.world.Condition;

import java.io.IOException;

public class Stun extends Effect {
    public Stun(int duration) {
        super("Stun", duration, EffectType.DEBUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {

        c.setCondition(Condition.INACTIVE);
    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i).getName().equals("Root")) {
                c.setCondition(Condition.ROOTED);
                return;
            }if (c.getAppliedEffects().get(i).getName().equals("Stun")) {
                c.setCondition(Condition.INACTIVE);
                return;
            }

        }

        c.setCondition(Condition.ACTIVE);
    }

}
