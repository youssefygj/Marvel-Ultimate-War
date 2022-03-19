package model.world;

import java.awt.*;

public class Cover {
    private int currentHP;
    private Point location;

    public Cover(int x, int y) {
        this.currentHP = (int) ((Math.random() * 900) + 100);
        this.location.x = x;
        this.location.y = y;

    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int currentHP) {
        if (currentHP >= 0) {
            this.currentHP = currentHP;
        }
    }

    public Point getLocation() {
        return this.location;
    }

}