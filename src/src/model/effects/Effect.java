package model.effects;

public class Effect {
    private String name;
    private int duration;
    private EffectType type;

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int new_Duration) {
        duration = new_Duration;
    }

    public EffectType getType() {
        return type;
    }

    public Effect(String name, int duration, EffectType type) {
        this.name = name;
        this.duration = duration;
        this.type = type;
    }

    public Effect() {

    }
}
