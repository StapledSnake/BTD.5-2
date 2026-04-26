import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

public class DartMonkey extends Tower {

    public DartMonkey(float x, float y) {
        super(x, y);
        this.range = 100f;
        this.fireRate = 0.8f; // Slightly faster than 1 shot per second
    }

    @Override
    public void shoot() {
        // Logic for spawning a projectile would go here
        System.out.println("Dart Monkey at " + position + " threw a dart!");
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
