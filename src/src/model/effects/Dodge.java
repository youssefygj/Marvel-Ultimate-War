package model.effects;

import model.world.Champion;

import java.io.IOException;

public class Dodge extends Effect{
    public Dodge(int duration){
        super("Dodge",duration,EffectType.BUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {
        c.getAppliedEffects().add(this);
        c.setSpeed((int) (c.getSpeed() + c.getSpeed() * 0.05));
    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        c.setSpeed((int) (c.getSpeed() / 1.05));
    }
}
