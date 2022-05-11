package model.effects;

import model.world.Champion;
import model.world.Condition;

import java.io.IOException;

public class Root extends Effect{
    public Root(int duration){
        super("Root",duration,EffectType.DEBUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {
        c.getAppliedEffects().add(this);
        if (c.getCondition()== Condition.ACTIVE) {
            c.setCondition(Condition.ROOTED);
        }
    }

    @Override
    public void remove(Champion c) throws IOException {
        if(c.getCondition()==Condition.ROOTED){
            c.setCondition(Condition.ACTIVE);
        }else{
            if(c.getCondition()==Condition.INACTIVE){
                c.setCondition(Condition.INACTIVE);
            }
        }
        c.getAppliedEffects().remove(this);
    }
}
