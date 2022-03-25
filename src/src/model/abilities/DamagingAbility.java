package model.abilities;
import java.io.IOException;
public class DamagingAbility extends Ability {
    private int damageAmount;

    public DamagingAbility(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required, int damageAmount) throws IOException {
        super(name, cost, baseCoolDown, castRange, area, required);
        this.damageAmount = damageAmount;
    }

    public int getDamageAmount() {

        return this.damageAmount;

    }

    public void setDamageAmount(int damageAmount) {
        this.damageAmount = damageAmount;

    }
}