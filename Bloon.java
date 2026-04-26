import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Enemy {
    private Vector2 position;
    private float speed = 100f;
    private int currentWaypoint = 0;
    private ArrayList<Vector2> path;
    private boolean reachedEnd = false;

    public Enemy(ArrayList<Vector2> path) {
        this.path = path;
        this.position = new Vector2(path.get(0).x, path.get(0).y);
    }

    public void update(float delta) {
        if (currentWaypoint < path.size()) {
            Vector2 target = path.get(currentWaypoint);
            Vector2 direction = new Vector2(target).sub(position).nor();
            position.add(direction.scl(speed * delta));

            // Check if we are close enough to the waypoint to move to the next one
            if (position.dst(target) < 2f) {
                currentWaypoint++;
            }
        } else {
            reachedEnd = true;
        }
    }

    public Vector2 getPosition() { return position; }
    public boolean isReachedEnd() { return reachedEnd; }
}
