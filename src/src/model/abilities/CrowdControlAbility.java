package model.abilities;

import model.effects.Effect;

public class CrowdControlAbility extends Ability {

   private Effect effect;
    public CrowdControlAbility(Effect effect, String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required, int healAmount){
        super(name,cost,baseCoolDown,castRange,area,required);
        this.effect=effect;

    }
    public Effect getEffect() {

        return this.effect;

    }
}
