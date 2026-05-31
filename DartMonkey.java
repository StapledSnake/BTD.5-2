package com.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DartMonkey extends Tower {
    private Texture texture;

    public DartMonkey(float x, float y, Texture texture) {
        super(x, y);
        this.texture = texture;
        this.range = 100f;
        this.fireRate = 0.8f;
    }

    @Override
    public void shoot() {
        System.out.println("Dart Monkey at " + position + " threw a dart!");
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x + 10, position.y + 10, 30, 30);
    }
}
