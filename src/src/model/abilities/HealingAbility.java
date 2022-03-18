package model.abilities;

public class HealingAbility extends Ability {
    private int healAmount;
    public HealingAbility(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required,int healAmount)
    {super(name,cost,baseCoolDown,castRange,area,required);
        this.healAmount=healAmount;
    }
    public int getHealAmount() {

        return this.healAmount;

    }
    public void setHealAmount(int damageAmount)
    {
        this.healAmount=damageAmount;

    }

}
