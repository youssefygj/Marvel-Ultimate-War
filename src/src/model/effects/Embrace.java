package model.effects;

import model.world.Champion;

import java.io.IOException;

public class Embrace extends Effect{
    public Embrace(int duration){
        super("Embrace",duration,EffectType.BUFF);
    }
    public  void apply(Champion c) throws IOException{
        c.getAppliedEffects().add(this);
        int twentyPercent_HP = (c.getMaxHP() * 20) / 100;
        c.setCurrentHP(c.getCurrentHP() + twentyPercent_HP);
        int twentyPercent_Mana = (c.getMana() * 20) / 100;
        c.setMana(c.getMana() + twentyPercent_Mana);
        c.setSpeed((int) (c.getSpeed() * 1.2));
        c.setAttackDamage((int) (c.getAttackDamage() * 1.2));

    }

    @Override
    public void remove(Champion c) throws IOException {
        c.getAppliedEffects().remove(this);
        c.setSpeed((int) (c.getSpeed() / 1.2));
        c.setAttackDamage((int) (c.getAttackDamage() / 1.2));
    }
}
