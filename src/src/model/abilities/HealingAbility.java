package model.abilities;

import model.world.Champion;
import model.world.Damageable;

import java.io.IOException;
import java.util.ArrayList;

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
    public void execute(ArrayList<Damageable> targets){

        for(int i=0;i<targets.size();i++){
            if(targets.get(i) instanceof Champion){
            targets.get(i).setCurrentHP(targets.get(i).getCurrentHP()+this.getHealAmount());

        }}

}}
