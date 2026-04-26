public class Tile {
    public enum TileType { LAND, PATH, WATER }

    private TileType type;
    private Tower towerOnTile;
    private float x, y, size;

    public Tile(float x, float y, float size, TileType type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.type = type;
    }

    public boolean canPlaceTower() {
        return type == TileType.LAND && towerOnTile == null;
    }

    public void setTower(Tower tower) { this.towerOnTile = tower; }
    public TileType getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
}