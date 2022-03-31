package model.effects;

public class Effect {
    private String name;
    private int duration;
    private EffectType type;

    public Effect(String name, int duration, EffectType type) {
        this.name = name;
        this.duration = duration;
        this.type = type;

    }

    public Effect() {

    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int new_Duration) {
        if (new_Duration < 0)
            new_Duration = 0;
        duration = new_Duration;
    }

    public EffectType getType() {
        return type;
    }
}
