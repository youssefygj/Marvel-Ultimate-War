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

        if (c.getCondition()== Condition.ACTIVE) {
            c.setCondition(Condition.ROOTED);
        }
    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        for (int i = 0; i < c.getAppliedEffects().size(); i++) {
            if (c.getAppliedEffects().get(i) instanceof Root) {
                c.setCondition(Condition.ROOTED);
                return;
            }if (c.getAppliedEffects().get(i) instanceof Stun) {
                c.setCondition(Condition.INACTIVE);
                return;
            }

        }

        c.setCondition(Condition.ACTIVE);
}}
