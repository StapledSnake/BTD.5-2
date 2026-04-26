import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import com.badlogic.gdx.InputAdapter;

public class MyGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Tile[][] grid;
    private ArrayList<Tower> towers;
    private ArrayList<Enemy> enemies;
    private ArrayList<Vector2> pathWaypoints;

    private final int TILE_SIZE = 50;

    @Override
    public void create() {
        batch = new SpriteBatch();
        towers = new ArrayList<>();
        enemies = new ArrayList<>();
        pathWaypoints = new ArrayList<>();

        setupGrid();

        // Setup Input Handling
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert Y because LibGDX (0,0) is bottom-left, but screen (0,0) is top-left
                float worldY = Gdx.graphics.getHeight() - screenY;
                placeTower((int)screenX, (int)worldY);
                return true;
            }
        });
    }

    private void setupGrid() {
        grid = new Tile[16][10];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 10; j++) {
                Tile.TileType type = (j == 4) ? Tile.TileType.PATH : Tile.TileType.LAND;
                if (i > 12 && j > 7) type = Tile.TileType.WATER;

                grid[i][j] = new Tile(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, type);

                // If it's a path, add it to our waypoint list for enemies
                if (type == Tile.TileType.PATH) {
                    pathWaypoints.add(new Vector2(i * TILE_SIZE + 25, j * TILE_SIZE + 25));
                }
            }
        }
    }

    private void placeTower(int x, int y) {
        int gridX = x / TILE_SIZE;
        int gridY = y / TILE_SIZE;

        if (gridX < 16 && gridY < 10) {
            Tile tile = grid[gridX][gridY];
            if (tile.canPlaceTower()) {
                DartMonkey monkey = new DartMonkey(tile.getX(), tile.getY());
                towers.add(monkey);
                tile.setTower(monkey);
            }
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update Logic
        for (Tower t : towers) t.update(delta);
        for (Enemy o : enemies) o.update(delta);

        // Spawning logic (Example: spawn an enemy every few seconds)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            enemies.add(new Enemy(pathWaypoints));
        }

        batch.begin();
        // Here you would loop through towers and enemies and call their render methods
        batch.end();
    }
}
