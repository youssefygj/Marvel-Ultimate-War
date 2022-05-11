package model.effects;

import model.world.Champion;

import java.io.IOException;

public class Silence extends Effect{
    public Silence(int duration){
        super("Silence",duration,EffectType.DEBUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {
        c.getAppliedEffects().add(this);
        c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 2);
        c.setCurrentActionPoints(c.getCurrentActionPoints() + 2);
    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 2);
        c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);

    }
}
