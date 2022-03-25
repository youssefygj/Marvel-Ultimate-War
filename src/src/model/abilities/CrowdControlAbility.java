package model.abilities;

import model.effects.Effect;

import java.io.IOException;

public class CrowdControlAbility extends Ability {

    private Effect effect;

    public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required,Effect effect) throws IOException {
        super(name, cost, baseCoolDown, castRange, area, required);
        this.effect = effect;
    }

    public Effect getEffect() {

        return this.effect;

    }
}
