package game;

/**
 * This class is the basis for powerups, and the contents are shared between the different types of powerups that extends
 * AbstractPowerup. The classes that extend this class is BombCounterPU and BombRadiusPU. The constructor needs an x and y
 * coordinate that will be the placement for the powerup.
 */
public class AbstractPowerup
{
    // Constants are static by definition.
    private final static int POWERUP_SIZE = 30;
    private final int x;
    private final int y;
    private String name = null;

    public AbstractPowerup(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void addToPlayer(Player player) {
    }

    public int getPowerupSize() {
	return POWERUP_SIZE;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public String getName() {
	return name;
    }
}
