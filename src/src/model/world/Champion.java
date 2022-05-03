package model.world;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;


import engine.Player;
import model.abilities.Ability;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.Stun;

public abstract class Champion implements Damageable, Comparable {
    private String name;
    private int maxHP;
    private int currentHP;
    private int mana;
    private int maxActionPointsPerTurn;
    private int currentActionPoints;
    private int attackRange;
    private int attackDamage;
    private int speed;
    private ArrayList<Ability> abilities;
    private ArrayList<Effect> appliedEffects;
    private Condition condition;
    private Point location;

    public Champion(String name, int maxHP, int mana, int actions, int speed, int attackRange, int attackDamage) {
        this.name = name;
        this.maxHP = maxHP;
        this.mana = mana;
        this.currentHP = this.maxHP;
        this.maxActionPointsPerTurn = actions;
        this.speed = speed;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        this.condition = Condition.ACTIVE;
        this.abilities = new ArrayList<Ability>();
        this.appliedEffects = new ArrayList<Effect>();
        this.currentActionPoints = maxActionPointsPerTurn;
    }

    public int compareTo(Champion c) {
        if (this.getSpeed() > c.getSpeed()) {
            return 1;
        } else if (this.getSpeed() < c.getSpeed()) {
            return -1;
        } else {
            if (this.getName().compareTo(c.getName()) > 1) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public int getMaxHP() {
        return maxHP;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHP() {

        return currentHP;
    }

    public void setCurrentHP(int hp) {

        if (hp < 0) {
            currentHP = 0;

        } else if (hp > maxHP)
            currentHP = maxHP;
        else
            currentHP = hp;

    }

    public ArrayList<Effect> getAppliedEffects() {
        return appliedEffects;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int currentSpeed) {
        if (currentSpeed < 0)
            this.speed = 0;
        else
            this.speed = currentSpeed;
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

    public void setLocation(Point currentLocation) {
        this.location = currentLocation;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public ArrayList<Ability> getAbilities() {
        return abilities;
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public void setCurrentActionPoints(int currentActionPoints) {
        if (currentActionPoints > maxActionPointsPerTurn)
            currentActionPoints = maxActionPointsPerTurn;
        else if (currentActionPoints < 0)
            currentActionPoints = 0;
        this.currentActionPoints = currentActionPoints;
    }

    public int getMaxActionPointsPerTurn() {
        return maxActionPointsPerTurn;
    }

    public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
        this.maxActionPointsPerTurn = maxActionPointsPerTurn;
    }

    //Start of milestone 2

    public void useLeaderAbility(ArrayList<Champion> targets) throws IOException {
        if (this instanceof Hero) {
            for (int i = 0; i < targets.size(); i++) {
                for (int j = 0; j < targets.get(i).getAppliedEffects().size(); j++) {
                    if (targets.get(i).getAppliedEffects().get(j).getType() == EffectType.DEBUFF) ;
                    {
                        targets.get(i).getAppliedEffects().remove(j);
                    }
                }
                Embrace temp = new Embrace(2);
                temp.apply(targets.get(i));
            }
        } else if (this instanceof Villain) {
            for (int i = 0; i < targets.size(); i++) {
                if (targets.get(i).getCurrentHP() < ((targets.get(i).getMaxHP() * 30) / 100))
                    targets.get(i).setCondition(Condition.KNOCKEDOUT);
            }

        } else if (this instanceof AntiHero) {
            Stun x = new Stun(2);
            for (int i = 0; i < targets.size();i++){
            x.apply(targets.get(i));
            }
        }
    }


}
