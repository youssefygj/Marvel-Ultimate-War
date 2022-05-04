package model.effects;


import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;
import model.world.Condition;

import java.io.IOException;

public abstract class Effect implements Cloneable {
    private String name;
    private EffectType type;
    private int duration;

    public Effect(String name, int duration, EffectType type) {
        this.name = name;
        this.type = type;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public EffectType getType() {
        return type;
    }

    public void apply(Champion c) throws IOException {
        if (this.name == "Disarm") {
            c.getAppliedEffects().add(this);
            DamagingAbility punch = new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50);
            c.getAbilities().add(punch);
        } else if (this.name == "Dodge") {
            c.getAppliedEffects().add(this);
            c.setSpeed((int) (c.getSpeed() + c.getSpeed() * 0.05));
        } else if (this.name == "Embrace") {
            c.getAppliedEffects().add(this);
            int twentyPercent_HP = (c.getMaxHP() * 20) / 100;
            c.setCurrentHP(c.getCurrentHP() + twentyPercent_HP);
            int twentyPercent_Mana = (c.getMana() * 20) / 100;
            c.setMana(c.getMana() + twentyPercent_Mana);
            c.setSpeed((int) (c.getSpeed() * 1.2));
            c.setAttackDamage((int) (c.getAttackDamage() * 1.2));
        } else if (this.name == "PowerUp") {
            c.getAppliedEffects().add(this);
            for (int i = 0; i < c.getAbilities().size(); i++) {
                if (c.getAbilities().get(i) instanceof DamagingAbility) {
                    double dam = ((DamagingAbility) (c.getAbilities().get(i))).getDamageAmount() * 1.2;
                    ((DamagingAbility) c.getAbilities().get(i)).setDamageAmount((int) dam);
                } else if (c.getAbilities().get(i) instanceof HealingAbility) {
                    double hel = ((HealingAbility) (c.getAbilities().get(i))).getHealAmount() * 1.2;
                    ((HealingAbility) c.getAbilities().get(i)).setHealAmount((int) hel);
                }

            }
        } else if (this.name == "Root") {
            c.getAppliedEffects().add(this);
        } else if (this.name == "Shield") {
            c.getAppliedEffects().add(this);
            c.setSpeed((int) (c.getSpeed() * 1.02));
        } else if (this.name == "Shock") {
            c.getAppliedEffects().add(this);
            c.setSpeed((int) (c.getSpeed() * 0.9));
            c.setAttackDamage((int) (c.getAttackDamage() * 0.9));
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
            c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
        } else if (this.name == "Silence") {
            c.getAppliedEffects().add(this);
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 2);
            c.setCurrentActionPoints(c.getCurrentActionPoints() + 2);
        } else if (this.name == "SpeedUp") {
            c.getAppliedEffects().add(this);
            c.setSpeed((int) (c.getSpeed() * 1.15));
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
            c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
        } else if (this.name == "Stun") {
            c.getAppliedEffects().add(this);
            c.setCondition(Condition.INACTIVE);
        }
    }

    public void remove(Champion c) throws IOException {
        if (c.getAppliedEffects().size() == 0)
            return;
        if (this.name == "Disarm") {
            c.getAppliedEffects().remove(this);
            DamagingAbility punch = new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50);
            c.getAbilities().remove(punch);
        } else if (this.name == "Dodge") {
            c.getAppliedEffects().remove(this);
            c.setSpeed((int) (c.getSpeed() / 1.05));
        } else if (this.name == "Embrace") {
            c.getAppliedEffects().remove(this);
            c.setSpeed((int) (c.getSpeed() / 1.2));
            c.setAttackDamage((int) (c.getAttackDamage() / 1.2));
        } else if (this.name == "PowerUp") {
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
        } else if (this.name == "Root") {
            c.getAppliedEffects().remove(this);
        } else if (this.name == "Shield") {
            c.getAppliedEffects().remove(this);
            c.setSpeed((int) (c.getSpeed() / 1.02));
        } else if (this.name == "Shock") {
            c.getAppliedEffects().remove(this);
            c.setSpeed((int) (c.getSpeed() / 0.9));
            c.setAttackDamage((int) (c.getAttackDamage() / 0.9));
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
            c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
        } else if (this.name == "Silence") {
            c.getAppliedEffects().remove(this);
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 2);
            c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
        } else if (this.name == "SpeedUp") {
            c.getAppliedEffects().remove(this);
            c.setSpeed((int) (c.getSpeed() / 1.15));
            c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
            c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
        } else if (this.name == "Stun") {
            c.getAppliedEffects().remove(this);
            c.setCondition(Condition.ACTIVE);
        }
    }


}
