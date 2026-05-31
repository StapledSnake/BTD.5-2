package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.InputAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MyGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    // Dynamic Procedural textures
    private Texture landTexture;
    private Texture pathTexture;
    private Texture waterTexture;
    private Texture monkeyTexture;

    private Tile[][] grid;
    private ArrayList<Tower> towers;
    private ArrayList<Enemy> enemies;
    private ArrayList<Vector2> pathWaypoints;

    private final int COLS = 16;
    private final int ROWS = 10;
    private final int TILE_SIZE = 50;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        towers = new ArrayList<>();
        enemies = new ArrayList<>();
        pathWaypoints = new ArrayList<>();

        // Generate color textures directly in RAM
        landTexture = createColoredTexture(Color.FOREST);
        pathTexture = createColoredTexture(Color.TAN);
        waterTexture = createColoredTexture(Color.ROYAL);
        monkeyTexture = createColoredTexture(Color.BROWN);

        setupProceduralMap();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float worldY = Gdx.graphics.getHeight() - screenY;
                placeTower(screenX, (int) worldY);
                return true;
            }
        });
    }

    private void setupProceduralMap() {
        grid = new Tile[COLS][ROWS];
        
        // Initialize everything as Land first
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                grid[i][j] = new Tile(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, Tile.TileType.LAND);
            }
        }

        // 1. Generate Random Connected Path (Top-Left to Bottom-Right)
        ArrayList<GridPoint> pathCoords = generatePath();
        for (GridPoint p : pathCoords) {
            grid[p.x][p.y].setType(Tile.TileType.PATH);
            // Center waypoint coordinates for the enemy
            pathWaypoints.add(new Vector2(p.x * TILE_SIZE + TILE_SIZE / 2f, p.y * TILE_SIZE + TILE_SIZE / 2f));
        }

        generateLake();
    }

    private ArrayList<GridPoint> generatePath() {
        ArrayList<GridPoint> path = new ArrayList<>();
        GridPoint current = new GridPoint(0, ROWS - 1); 
        GridPoint target = new GridPoint(COLS - 1, 0);  
        path.add(current);

        Random rand = new Random();

        while (current.x != target.x || current.y != target.y) {
            boolean moveRight = rand.nextBoolean();
            
            if (moveRight && current.x < target.x) {
                current = new GridPoint(current.x + 1, current.y);
            } else if (current.y > target.y) {
                current = new GridPoint(current.x, current.y - 1);
            } else if (current.x < target.x) {
                current = new GridPoint(current.x + 1, current.y);
            }
            path.add(current);
        }
        return path;
    }

    private void generateLake() {
        Random rand = new Random();
        int attempts = 0;
        
        while (attempts < 100) {
            int startX = rand.nextInt(COLS);
            int startY = rand.nextInt(ROWS);
            
            if (grid[startX][startY].getType() == Tile.TileType.LAND) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int nx = startX + i;
                        int ny = startY + j;
                        if (nx >= 0 && nx < COLS && ny >= 0 && ny < ROWS) {
                            if (grid[nx][ny].getType() == Tile.TileType.LAND) {
                                grid[nx][ny].setType(Tile.TileType.WATER);
                            }
                        }
                    }
                }
                break;
            }
            attempts++;
        }
    }

    private void placeTower(int x, int y) {
        int gridX = x / TILE_SIZE;
        int gridY = y / TILE_SIZE;

        if (gridX >= 0 && gridX < COLS && gridY >= 0 && gridY < ROWS) {
            Tile tile = grid[gridX][gridY];
            if (tile.canPlaceTower()) {
                DartMonkey monkey = new DartMonkey(tile.getX(), tile.getY(), monkeyTexture);
                towers.add(monkey);
                tile.setTower(monkey);
            }
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update logic
        for (Tower t : towers) t.update(delta);
        for (Enemy o : enemies) o.update(delta);

        // Press 'S' to spawn an enemy
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            enemies.add(new Enemy(pathWaypoints));
        }

        // --- Step 1: Render Map Background & Grid ---
        batch.begin();
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Texture currentTexture = landTexture;
                if (grid[i][j].getType() == Tile.TileType.PATH) currentTexture = pathTexture;
                if (grid[i][j].getType() == Tile.TileType.WATER) currentTexture = waterTexture;
                
                batch.draw(currentTexture, grid[i][j].getX(), grid[i][j].getY(), TILE_SIZE, TILE_SIZE);
            }
        }
        
        // --- Step 2: Render Towers & Enemies ---
        for (Tower t : towers) t.render(batch);
        batch.end();

        // Render vector shapes (Enemies/Projectiles) over the sprites
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Enemy e : enemies) {
            if (!e.isReachedEnd()) {
                shapeRenderer.circle(e.getPosition().x, e.getPosition().y, 12);
            }
        }
        shapeRenderer.end();
    }

    // Helper method to build a 1x1 color placeholder texture programmatically
    private Texture createColoredTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); 
        return texture;
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        landTexture.dispose();
        pathTexture.dispose();
        waterTexture.dispose();
        monkeyTexture.dispose();
    }

    // Tiny tuple wrapper for coordinate building
    private static class GridPoint {
        int x, y;
        GridPoint(int x, int y) { this.x = x; this.y = y; }
    }
}
