package game;

/**
 * This class extends AbstractPowerup and receives fundamental methods such as getters for its coordinates and size. This class
 * has an addToPlayer-method which adjusts the bombRadius of the player.
 */
public class BombRadiusPU extends AbstractPowerup
{

    public BombRadiusPU(int rowIndex, int colIndex) {
	super(colIndex, rowIndex);
    }

    public void addToPlayer(Player player) {
	int currentExplosionRadius = player.getExplosionRadius();
	player.setExplosionRadius(currentExplosionRadius + 1);
    }

    public String getName() {
	final String name = "BombRadius";
	return name;
    }
}
