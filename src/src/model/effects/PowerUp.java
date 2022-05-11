package model.effects;

import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;

import java.io.IOException;

public class PowerUp extends Effect{
    public PowerUp(int duration){
        super("PowerUp",duration,EffectType.BUFF);
    }

    @Override
    public void apply(Champion c) throws IOException {
        c.getAppliedEffects().add(this);
        for (int i = 0; i < c.getAbilities().size(); i++) {
            if (c.getAbilities().get(i) instanceof DamagingAbility) {
                double dam = ((DamagingAbility) (c.getAbilities().get(i))).getDamageAmount() * 1.2;
                ((DamagingAbility) c.getAbilities().get(i)).setDamageAmount((int) dam);
            } else if (c.getAbilities().get(i) instanceof HealingAbility) {
                double hel = ((HealingAbility) (c.getAbilities().get(i))).getHealAmount() * 1.2;
                ((HealingAbility) c.getAbilities().get(i)).setHealAmount((int) hel);
            }

        }}

    @Override
    public void remove(Champion c) throws IOException {
            c.getAppliedEffects().remove(this);
            for (int i = 0; i < c.getAbilities().size(); i++) {
                if (c.getAbilities().get(i) instanceof DamagingAbility) {
                    double dam = ((DamagingAbility) (c.getAbilities().get(i))).getDamageAmount() / 1.2;
                    ((DamagingAbility) c.getAbilities().get(i)).setDamageAmount((int) dam);
                } else if (c.getAbilities().get(i) instanceof HealingAbility) {
                    double hel = ((HealingAbility) (c.getAbilities().get(i))).getHealAmount() / 1.2;
                    ((HealingAbility) c.getAbilities().get(i)).setHealAmount((int) hel);
                }
            }
    }}

