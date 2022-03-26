package model.abilities;

import java.io.IOException;

public class Ability {
    private String name;
    private int manaCost;
    private int baseCooldown;
    private int currentCooldown;
    private int castRange;
    private int requiredActionPoints;
    private AreaOfEffect castArea;

    public Ability(String name, int cost, int baseCoolDown, int castRange, AreaOfEffect area, int required) throws IOException {

        this.name = name;
        this.manaCost = cost;
        this.baseCooldown = baseCoolDown;
        this.castRange = castRange;
        this.castArea = area;
        this.requiredActionPoints = required;

    }

    public Ability() {

    }

    public String getName() {
        return this.name;

    }

    public int getManaCost() {
        return this.manaCost;

    }

    public int getBaseCooldown() {
        return this.baseCooldown;

    }

    public int getCurrentCooldown() {
        return this.currentCooldown;

    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;

    }

    public int getCastRange() {
        return this.castRange;

    }

    public int getRequiredActionPoints() {
        return this.requiredActionPoints;

    }

    public AreaOfEffect getCastArea() {
        return this.castArea;

    }
}
