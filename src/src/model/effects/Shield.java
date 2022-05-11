package model.effects;

import model.world.Champion;

import java.io.IOException;

public class Shield extends Effect{
    public Shield(int duration){
        super("Shield",duration,EffectType.BUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {
        c.getAppliedEffects().add(this);
        c.setSpeed((int) (c.getSpeed() * 1.02));

    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        c.setSpeed((int) (c.getSpeed() / 1.02));

    }
}
