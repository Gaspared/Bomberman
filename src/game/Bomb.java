package game;

public class Bomb
{
    // Constants are static by definition.
    private final static int BOMBSIZE = 30;
    private final static int STARTCOUNTDOWN = 100;
    private int timeToExplosion = STARTCOUNTDOWN;
    private final int rowIndex;
    private final int colIndex;
    private int explosionRadius;
    private boolean playerLeft;

    public Bomb(final int rowIndex, final int colIndex, int explosionRadius) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.explosionRadius = explosionRadius;
        playerLeft = false;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    // This method is static since every bomb has the same size.
    public static int getBOMBSIZE() {
        return BOMBSIZE;
    }

    public int getTimeToExplosion() {
        return timeToExplosion;
    }

    public void setTimeToExplosion(final int timeToExplosion) {
        this.timeToExplosion = timeToExplosion;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }

    public boolean isPlayerLeft() {
        return playerLeft;
    }

    public void setPlayerLeft(final boolean playerLeft) {
        this.playerLeft = playerLeft;
    }
}