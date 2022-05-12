package model.abilities;

import model.effects.Effect;
import model.world.Champion;
import model.world.Damageable;

import java.io.IOException;
import java.util.ArrayList;

public class CrowdControlAbility extends Ability {

    private Effect effect;

    public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required, Effect effect) throws IOException {
        super(name, cost, baseCoolDown, castRange, area, required);
        this.effect = effect;
    }

    public Effect getEffect() {

        return this.effect;

    }

    public void execute(ArrayList<Damageable> targets) throws CloneNotSupportedException, IOException {

        Effect c = (Effect)effect.clone();

        for (int i = 0; i < targets.size(); i++)
        {
            c.apply((Champion)targets.get(i));

            ((Champion) targets.get(i)).getAppliedEffects().add(c);
    }

}}
