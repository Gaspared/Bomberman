package game;

/**
 * This Interface class is excluded to one method, floorChanged which is implemented in BombermanComponent
 * where the method repaints the game.
 */
public interface FloorListener
{
    void floorChanged();
}
