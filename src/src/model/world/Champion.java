package model.world;

import model.abilities.Ability;
import model.effects.Effect;

import java.awt.*;
import java.util.ArrayList;

public class Champion {
    private String name;
    private int maxHP;
    private int currentHP;
    private int mana;
    private int maxActionPointsPerTurn;
    private int currentActionPoints;
    private int attackRange;
    private int attackDamage;
    private int speed;
    private ArrayList<Ability> abilities = new ArrayList<Ability>();
    private ArrayList<Effect> appliedEffects = new ArrayList<Effect>();
    private Condition condition;
    private Point location;

    public Champion() {

    }

    public Champion(String name, int maxHP, int mana, int maxActionsPerTurn, int speed, int attackRange, int attackDamage) {
        //Condition must be set to ACTIVE still
        this.name = name;
        this.maxHP = maxHP;

        this.currentHP = maxHP;
        this.mana = mana;
        this.currentActionPoints = maxActionsPerTurn;
        this.maxActionPointsPerTurn = maxActionsPerTurn;
        this.speed = speed;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        condition = Condition.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        if (currentHP < 0) {
            this.currentHP = 0;
        } else if (currentHP > this.maxHP) {
            this.currentHP = maxHP;
        } else
            this.currentHP = currentHP;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        if (mana < 0)
            mana = 0;
        this.mana = mana;
    }

    public int getMaxActionPointsPerTurn() {
        return maxActionPointsPerTurn;
    }

    public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
        if (maxActionPointsPerTurn < 0)
            maxActionPointsPerTurn = 0;
        this.maxActionPointsPerTurn = maxActionPointsPerTurn;
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public void setCurrentActionPoints(int currentActionPoints) {
        if (currentActionPoints > maxActionPointsPerTurn)
            currentActionPoints = maxActionPointsPerTurn;
        else if (currentActionPoints < 0)
            this.currentActionPoints = 0;
        else
            this.currentActionPoints = currentActionPoints;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        if (attackDamage < 0)
            attackDamage = 0;
        this.attackDamage = attackDamage;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed < 0)
            speed = 0;
        this.speed = speed;
    }

    public ArrayList<Ability> getAbilities() {
        return abilities;
    }

    public ArrayList<Effect> getAppliedEffects() {
        return appliedEffects;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
