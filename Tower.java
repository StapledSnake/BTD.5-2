import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Tower {
    protected Vector2 position;
    protected float range;
    protected float fireRate;
    protected float timer;

    public Tower(float x, float y) {
        this.position = new Vector2(x, y);
        this.range = 150f;
        this.fireRate = 1.0f; // Seconds between shots
    }

    public void update(float delta) {
        timer += delta;
        if (timer >= fireRate) {
            shoot();
            timer = 0;
        }
    }

    public abstract void shoot();
    public abstract void render(SpriteBatch batch);
}