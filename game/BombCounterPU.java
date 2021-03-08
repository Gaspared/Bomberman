package game;

/**
 * This class extends AbstractPowerup and receives fundamental methods such as getters for its coordinates and size. This class
 * has an addToPlayer-method which adjusts the bombCount of the player.
 */
public class BombCounterPU extends AbstractPowerup
{

    public BombCounterPU(int rowIndex, int colIndex) {
	super(colIndex, rowIndex);
    }

    public void addToPlayer(Player player) {
	int currentBombCount = player.getBombCount();
	player.setBombCount(currentBombCount + 1);
    }

    public String getName() {
	final String name = "BombCounter";
	return name;
    }
}
