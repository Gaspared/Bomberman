package game;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap;
import java.util.EnumMap;


public class BombermanComponent extends JComponent implements FloorListener
{
    // Constants are static by definition.
    private final static int SQUARE_SIZE = 40;
    private final static int CHARACTER_ADJUSTMENT_FOR_PAINT = 15;
    private final static int SQUARE_MIDDLE = SQUARE_SIZE/2;
    private final static int BOMB_ADJUSTMENT_1 =5;
    private final static int BOMB_ADJUSTMENT_2 =10;
    // Defining painting parameters
    private final static int PAINT_PARAMETER_13 = 13;
    private final static int PAINT_PARAMETER_15 = 15;
    private final static int PAINT_PARAMETER_17 = 17;
    private final static int PAINT_PARAMETER_18 = 18;
    private final static int PAINT_PARAMETER_19 = 19;
    private final static int PAINT_PARAMETER_20 = 20;
    private final static int PAINT_PARAMETER_24 = 24;
    private final Floor floor;
    private final AbstractMap<FloorTile, Color> colorMap;

    public BombermanComponent(Floor floor) {
	this.floor = floor;

	colorMap = new EnumMap<>(FloorTile.class);
	colorMap.put(FloorTile.FLOOR, Color.GREEN);
	colorMap.put(FloorTile.UNBREAKABLEBLOCK, Color.BLACK);
	colorMap.put(FloorTile.BREAKABLEBLOCK, Color.RED);
    }

    // This method is static since each square has the same size.
    public static int getSquareSize() {
	return SQUARE_SIZE;
    }

    // This method is static since each square has the same size.
    public static int getSquareMiddle() {
	return SQUARE_MIDDLE;
    }

    public Dimension getPreferredSize() {
	super.getPreferredSize();
	return new Dimension(this.floor.getWidth() * SQUARE_SIZE, this.floor.getHeight() * SQUARE_SIZE);
    }

    public void floorChanged() {
	repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;

	for (int rowIndex = 0; rowIndex < floor.getHeight(); rowIndex++) {
	    for (int colIndex = 0; colIndex < floor.getWidth(); colIndex++) {
		g2d.setColor(colorMap.get(this.floor.getFloorTile(rowIndex, colIndex)));
		if(floor.getFloorTile(rowIndex, colIndex)==FloorTile.BREAKABLEBLOCK){
		    paintBreakableBlock(rowIndex, colIndex, g2d);
		}
		else if(floor.getFloorTile(rowIndex, colIndex)==FloorTile.UNBREAKABLEBLOCK){
		    paintUnbreakableBlock(rowIndex, colIndex, g2d);
		}
		else{
		    paintFloor(rowIndex, colIndex, g2d);
		}
	    }
	}
	// Paint player:
	paintPlayer(floor.getPlayer(), g2d);

	//Paint enemies
	for (Enemy e: floor.getEnemyList()) {
	    paintEnemy(e, g2d);
	}

	//Paint powerups
	for (AbstractPowerup p: floor.getPowerupList()) {
	    if (p.getName().equals("BombCounter")) {
		g2d.setColor(Color.BLACK);
	    } else if (p.getName().equals("BombRadius")) {
		g2d.setColor(Color.RED);
	    }
	    g2d.fillOval(p.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT, p.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT, p.getPowerupSize(), p.getPowerupSize());
	}

	//Paint bombs
	for (Bomb b: floor.getBombList()) {
	    g2d.setColor(Color.RED);
	    int bombX = floor.squareToPixel(b.getColIndex());
	    int bombY = floor.squareToPixel(b.getRowIndex());
	    g2d.fillOval(bombX + BOMB_ADJUSTMENT_1, bombY + BOMB_ADJUSTMENT_1, Bomb.getBOMBSIZE(), Bomb.getBOMBSIZE());
	    g2d.setColor(Color.ORANGE);
	    g2d.fillOval(bombX + BOMB_ADJUSTMENT_2, bombY + BOMB_ADJUSTMENT_1, BOMB_ADJUSTMENT_1, BOMB_ADJUSTMENT_2);
	}

	//Paint explosions
	g2d.setColor(Color.ORANGE);
	for (Explosion tup: floor.getExplosionCoords()) {
	    g2d.fillOval(floor.squareToPixel(tup.getColIndex()) + BOMB_ADJUSTMENT_1, floor.squareToPixel(tup.getRowIndex()) +
										     BOMB_ADJUSTMENT_1, Bomb.getBOMBSIZE(), Bomb.getBOMBSIZE());
	}
    }

    private void paintBreakableBlock(int rowIndex, int colIndex, Graphics g2d){
	g2d.setColor(Color.lightGray);
	g2d.fillRect(colIndex * SQUARE_SIZE, rowIndex * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
	g2d.setColor(Color.BLUE);
	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+10, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+10);
	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE);
	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10);
	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_SIZE, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_SIZE);

	g2d.drawLine(colIndex* SQUARE_SIZE+10, rowIndex*SQUARE_SIZE+1, colIndex*SQUARE_SIZE+10, rowIndex*SQUARE_SIZE+10);
	g2d.drawLine(colIndex* SQUARE_SIZE+SQUARE_MIDDLE+10, rowIndex*SQUARE_SIZE+1, colIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, rowIndex*SQUARE_SIZE+10);

	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+10, colIndex*SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE);
	g2d.drawLine(colIndex* SQUARE_SIZE+SQUARE_MIDDLE+1, rowIndex*SQUARE_SIZE+10, colIndex*SQUARE_SIZE+SQUARE_MIDDLE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE);

	g2d.drawLine(colIndex* SQUARE_SIZE+10, rowIndex*SQUARE_SIZE+1+SQUARE_MIDDLE, colIndex*SQUARE_SIZE+10, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10);
	g2d.drawLine(colIndex* SQUARE_SIZE+SQUARE_MIDDLE+10, rowIndex*SQUARE_SIZE+1+SQUARE_MIDDLE, colIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10);

	g2d.drawLine(colIndex* SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, colIndex*SQUARE_SIZE+1, rowIndex*SQUARE_SIZE+SQUARE_SIZE);
	g2d.drawLine(colIndex* SQUARE_SIZE+SQUARE_MIDDLE+1, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, colIndex*SQUARE_SIZE+SQUARE_MIDDLE+1, rowIndex*SQUARE_SIZE+SQUARE_SIZE);
    }

    private void paintUnbreakableBlock(int rowIndex, int colIndex, Graphics g2d){
	g2d.fillRect(colIndex * SQUARE_SIZE, rowIndex * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
	g2d.setColor(Color.DARK_GRAY);
	g2d.drawLine(colIndex* SQUARE_SIZE, rowIndex*SQUARE_SIZE, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE);
	g2d.drawLine(colIndex* SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_SIZE, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_SIZE);
	g2d.drawLine(colIndex* SQUARE_SIZE, rowIndex*SQUARE_SIZE, colIndex*SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_SIZE);
	g2d.drawLine(colIndex* SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE, colIndex*SQUARE_SIZE+SQUARE_SIZE, rowIndex*SQUARE_SIZE+SQUARE_SIZE);
    }

    private void paintFloor(int rowIndex, int colIndex, Graphics g2d){
	g2d.setColor(Color.white);
	g2d.fillRect(colIndex * SQUARE_SIZE, rowIndex * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
	g2d.setColor(Color.CYAN);
	g2d.drawLine(colIndex* SQUARE_SIZE+5, rowIndex*SQUARE_SIZE+10, colIndex * SQUARE_SIZE + 10, rowIndex * SQUARE_SIZE + 5);
	g2d.drawLine(colIndex* SQUARE_SIZE+5, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE, colIndex * SQUARE_SIZE + SQUARE_MIDDLE, rowIndex * SQUARE_SIZE + 5);
	g2d.drawLine(colIndex* SQUARE_SIZE+5, rowIndex*SQUARE_SIZE+SQUARE_MIDDLE+10, colIndex * SQUARE_SIZE + SQUARE_MIDDLE + 10, rowIndex * SQUARE_SIZE + 5);
    }

    private void paintEnemy(Enemy e, Graphics g2d){
	// Paint body
	g2d.setColor(Color.orange);
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT, e.getSize(), e.getSize());
	// Paint brows
	g2d.setColor(Color.BLACK);
	// Paint eyes
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+4, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+9, 7, 7);
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_19, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+9, 7, 7);
	// Paint mouth
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+5, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_20, PAINT_PARAMETER_20, 2);
	// Fill eyes
	g2d.setColor(Color.RED);
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+5, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, 5, 5);
	g2d.fillOval(e.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_20, e.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, 5, 5);

    }

    private void paintPlayer(Player player, Graphics g2d){
	// Paint hat
	g2d.setColor(Color.BLUE);
	g2d.fillOval(player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_15, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT-2, PAINT_PARAMETER_15, PAINT_PARAMETER_15);
	// Paint body
	g2d.setColor(Color.LIGHT_GRAY);
	g2d.fillOval(player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT, player.getSize(), player.getSize());
	// Paint face
	g2d.setColor(Color.PINK);
	g2d.fillOval(player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+3, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+3, player.getSize()-6, player.getSize()-6);
	// Paint eyes
	g2d.setColor(Color.BLACK);
	g2d.drawLine(player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_18);
	g2d.drawLine(player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_20, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+10, player.getX()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_20, player.getY()-CHARACTER_ADJUSTMENT_FOR_PAINT+PAINT_PARAMETER_18);
    }
}
