package model.world;

import java.awt.Point;
import java.util.ArrayList;


import model.abilities.Ability;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Effect;

@SuppressWarnings("rawtypes")
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

    public String toString() {
        String r = "";
        if(this instanceof Hero)
            r += "Name: " + name + "\n"+ "Type: Hero\n" + "Health = " + currentHP +  "\n"+  "Mana = " + mana + "\n"+"Action points ="+this.currentActionPoints+"\n"
                + "Points per turn = " + maxActionPointsPerTurn + "\n"+ "Speed = " + speed + "\n"+ "Range = " + attackRange + "\n"+  "Damage = " + attackDamage + "\n"+ "\n"+ "\n";
        else if(this instanceof AntiHero)
            r += "Name: " + name + "\n"+ "Type: Anti Hero\n" + "Health = " + currentHP  +  "\n"+  "Mana = " + mana + "\n"+"Action points ="+this.currentActionPoints+"\n"
                    + "Points per turn = " + maxActionPointsPerTurn + "\n"+ "Speed = " + speed + "\n"+ "Range = " + attackRange + "\n"+  "Damage = " + attackDamage + "\n"+ "\n"+ "\n";
        else
            r += "Name: " + name + "\n"+ "Type: Villain\n" + "Health = " + currentHP +  "\n"+  "Mana = " + mana + "\n"+"Action points ="+this.currentActionPoints+"\n"
                    + "Points per turn = " + maxActionPointsPerTurn + "\n"+ "Speed = " + speed + "\n"+ "Range = " + attackRange + "\n"+  "Damage = " + attackDamage + "\n"+ "\n"+ "\n";

        for (int i = 0; i < this.getAbilities().size(); i++) {
            r += "Ability name: " + this.getAbilities().get(i).getName() + "\n"+ "          Cast area: " + this.getAbilities().get(i).getCastArea()
                    +"\n"+  "          Cool down = " + this.getAbilities().get(i).getBaseCooldown() + "\n" +
                    "          Range = " + this.getAbilities().get(i).getCastRange() +"\n"+  "          Mana cost = " + this.getAbilities().get(i).getManaCost()
                    + "\n"+
                    "          Required Action Points = " + this.getAbilities().get(i).getRequiredActionPoints()+"\n";
            if(this.getAbilities().get(i) instanceof DamagingAbility){
                r+="          Type = Damaging Ability"+"\n"+"          Damage Amount = " +((DamagingAbility) this.getAbilities().get(i)).getDamageAmount()+"\n"+"\n"+"\n";
            }
            else if(this.getAbilities().get(i) instanceof HealingAbility){
                r+="          Type = Healing Ability"+"\n"+"          Heal Amount = " +((HealingAbility) this.getAbilities().get(i)).getHealAmount()+"\n"+"\n"+"\n";
            }
            else{
                r+="          Type = Crowd Control Ability"+"\n"+"           Effect = " +((CrowdControlAbility)this.getAbilities().get(i)).getEffect().getName() +"\n"+"\n"+"\n";
            }
        }

        return r;
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

        if (hp <= 0) {
            currentHP = 0;
            condition = Condition.KNOCKEDOUT;

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

    public int compareTo(Object o) {
        Champion c = (Champion) o;
        if (speed == c.speed)
            return name.compareTo(c.name);
        return -1 * (speed - c.speed);
    }

    public abstract void useLeaderAbility(ArrayList<Champion> targets);
}
