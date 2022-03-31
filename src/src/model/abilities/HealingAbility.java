package model.abilities;

import java.io.IOException;

public class HealingAbility extends Ability {
    private int healAmount;

    public HealingAbility(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required, int healAmount) throws IOException {
        super(name, cost, baseCoolDown, castRange, area, required);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {

        return this.healAmount;

    }

    public void setHealAmount(int damageAmount) {
        if (damageAmount < 0)
            damageAmount = 0;
        this.healAmount = damageAmount;

    }

}
