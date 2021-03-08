package game;

import game.AbstractCharacter.Move;
import java.util.*;


public class Floor {
    // Constants are static by definition.
    private final static double CHANCE_FOR_BREAKABLE_BLOCK = 0.4;
    private final static double CHANCE_FOR_RADIUS_POWERUP = 0.2;
    private final static double CHANCE_FOR_COUNTER_POWERUP = 0.8;
    private final FloorTile[][] tiles;
    private int width;
    private int height;
    private Collection<FloorListener> floorListeners = new ArrayList<>();
    private Player player = null;
    private Collection<Enemy> enemyList = new ArrayList<>();
    private List<Bomb> bombList= new ArrayList<>();
    private Collection<AbstractPowerup> powerupList = new ArrayList<>();
    private Collection<Bomb> explosionList= new ArrayList<>();
    private Collection<Explosion> explosionCoords= new ArrayList<>();
    private boolean isGameOver = false;

    public Floor(int width, int height, int nrOfEnemies) {
	this.width = width;
	this.height = height;
	this.tiles = new FloorTile[height][width];
	placeBreakable();
	placeUnbreakableAndGrass();
	spawnEnemies(nrOfEnemies);
    }

    public static int pixelToSquare(int pixelCoord){
	return ((pixelCoord + BombermanComponent.getSquareSize()-1) / BombermanComponent.getSquareSize())-1;
    }

    public FloorTile getFloorTile(int rowIndex, int colIndex) {
	return tiles[rowIndex][colIndex];
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public Player getPlayer() {
	return player;
    }

    public Collection<Enemy> getEnemyList() {
	return enemyList;
    }

    public Iterable<Bomb> getBombList() {
	return bombList;
    }

    public int getBombListSize() {
	return bombList.size();
    }

    public Iterable<AbstractPowerup> getPowerupList() {
	return powerupList;
    }

    public Iterable<Explosion> getExplosionCoords() {
	return explosionCoords;
    }

    public boolean getIsGameOver() {
	return isGameOver;
    }

    public void setIsGameOver(boolean value) {
	isGameOver = value;
    }

    public void addToBombList(Bomb bomb) {
	bombList.add(bomb);
    }

    public void createPlayer(BombermanComponent bombermanComponent, Floor floor){
	player = new Player(bombermanComponent, floor);
    }

    public int squareToPixel(int squareCoord){
	return squareCoord * BombermanComponent.getSquareSize();
    }

    public void moveEnemies() {
	if (enemyList.isEmpty()) {
	    isGameOver = true;
	}
	for (Enemy e: enemyList){
	    Move currentDirection = e.getCurrentDirection();

	    if (currentDirection == Move.DOWN) {
		e.move(Move.DOWN);
	    } else if (currentDirection == Move.UP) {
		e.move(Move.UP);
	    } else if (currentDirection == Move.LEFT) {
		e.move(Move.LEFT);
	    } else {
		e.move(Move.RIGHT);
	    }

	    if (collisionWithBlock(e)) {
		e.changeDirection();
	    }

	    if (collisionWithBombs(e)) {
		e.changeDirection();
	    }

	    if (collisionWithEnemies()) {
		isGameOver = true;
	    }
	}
    }

    public void addFloorListener(FloorListener bl) {
	floorListeners.add(bl);
    }

    public void notifyListeners() {
	for (FloorListener b : floorListeners) {
	    b.floorChanged();
	}
    }

    /**
     * This method creates a bomb if the given demands are satisfied.
     */
    public void bombCountdown(){
	Collection<Integer> bombIndexesToBeRemoved = new ArrayList<>();
	explosionList.clear();
	int index = 0;
	for (Bomb b: bombList) {
	    b.setTimeToExplosion(b.getTimeToExplosion() - 1);
	    if(b.getTimeToExplosion() == 0){
		bombIndexesToBeRemoved.add(index);
		explosionList.add(b);
	    }
	    index++;
	}
	for (int i: bombIndexesToBeRemoved){bombList.remove(i);}
    }

    public void explosionHandler(){
	Collection<Explosion> explosionsToBeRemoved = new ArrayList<>();
	for (Explosion e:explosionCoords) {
	    e.setDuration(e.getDuration()-1);
	    if(e.getDuration()==0){
		explosionsToBeRemoved.add(e);
	    }
	}
	for (Explosion e: explosionsToBeRemoved){explosionCoords.remove(e);}

	for (Bomb e: explosionList) {
	    int eRow = e.getRowIndex();
	    int eCol = e.getColIndex();
	    boolean northOpen = true;
	    boolean southOpen = true;
	    boolean westOpen = true;
	    boolean eastOpen = true;
	    explosionCoords.add(new Explosion(eRow, eCol));
	    for (int i = 1; i < e.getExplosionRadius()+1; i++) {
		if (eRow - i >= 0 && northOpen) {
		    northOpen = bombCoordinateCheck(eRow-i, eCol, northOpen);
		}
		if (eRow - i <= height && southOpen) {
		    southOpen = bombCoordinateCheck(eRow+i, eCol, southOpen);
		}
		if (eCol - i >= 0 && westOpen) {
		    westOpen = bombCoordinateCheck(eRow, eCol-i, westOpen);
		}
		if (eCol + i <= width && eastOpen) {
		    eastOpen = bombCoordinateCheck(eRow, eCol+i, eastOpen);
		}
	    }
	}
    }

    public void playerInExplosion(){
	for (Explosion tup:explosionCoords) {
	    if(collidingCircles(player, squareToPixel(tup.getColIndex()), squareToPixel(tup.getRowIndex()))){
		isGameOver = true;
	    }
	}
    }

    public void enemyInExplosion(){
	for (Explosion tup:explosionCoords) {
	    Collection<Enemy> enemiesToBeRemoved = new ArrayList<>();
	    for (Enemy e : enemyList) {
		if(collidingCircles(e, squareToPixel(tup.getColIndex()), squareToPixel(tup.getRowIndex()))){
		    enemiesToBeRemoved.add(e);
		}
	    }
	    for (Enemy e: enemiesToBeRemoved ) {
		enemyList.remove(e);
	    }
	}
    }

    public void characterInExplosion(){
	playerInExplosion();
	enemyInExplosion();
    }

    private void placeBreakable () {
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		double r = Math.random();
		if (r < CHANCE_FOR_BREAKABLE_BLOCK) {
		    tiles[i][j] = FloorTile.BREAKABLEBLOCK;
		}
	    }
	}
	clearSpawn();
    }

    private void clearSpawn () {
	tiles[1][1] = FloorTile.FLOOR;
	tiles[1][2] = FloorTile.FLOOR;
	tiles[2][1] = FloorTile.FLOOR;
    }

    private void spawnPowerup (int rowIndex, int colIndex) {
	double r = Math.random();
	if (r < CHANCE_FOR_RADIUS_POWERUP) {
	    powerupList.add(new BombRadiusPU(squareToPixel(rowIndex) + BombermanComponent.getSquareMiddle(), squareToPixel(colIndex) + BombermanComponent.getSquareMiddle()));
	} else if (r > CHANCE_FOR_COUNTER_POWERUP) {
	    powerupList.add(new BombCounterPU(squareToPixel(rowIndex) + BombermanComponent.getSquareMiddle(), squareToPixel(colIndex) + BombermanComponent.getSquareMiddle()));
	}
    }

    private void placeUnbreakableAndGrass () {
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		//Makes frame of unbreakable
		if ((i == 0) || (j == 0) || (i == height - 1) || (j == width - 1) || i % 2 == 0 && j % 2 == 0) {
		    tiles[i][j] = FloorTile.UNBREAKABLEBLOCK;
		    //Every-other unbreakable
		} else if (tiles[i][j] != FloorTile.BREAKABLEBLOCK) {
		    tiles[i][j] = FloorTile.FLOOR;
		}
	    }
	}
    }

    private void spawnEnemies (int nrOfEnemies) {
	for (int e = 0; e < nrOfEnemies; e++){
	    while(true) {
		int randRowIndex = 1 + (int) (Math.random() * (height - 2));
		int randColIndex = 1 + (int) (Math.random() * (width - 2));
		if(getFloorTile(randRowIndex, randColIndex) != FloorTile.FLOOR){
		    continue;
		}
		if(randRowIndex==1&&randColIndex==1||randRowIndex==1&&randColIndex==2||randRowIndex==2&&randColIndex==1){
		    continue;
		}
		if((randRowIndex % 2)==0){
		    enemyList.add(new Enemy(squareToPixel(randColIndex) + BombermanComponent.getSquareMiddle(), squareToPixel(randRowIndex) + BombermanComponent.getSquareMiddle(), true));
		}
		else{
		    enemyList.add(new Enemy(squareToPixel(randColIndex) + BombermanComponent.getSquareMiddle(), squareToPixel(randRowIndex) + BombermanComponent.getSquareMiddle(), false));
		}
		break;
	    }
	}
    }



    public boolean collisionWithEnemies(){
	for (Enemy enemy : enemyList) {
	    if(collidingCircles(player, enemy.getX()-BombermanComponent.getSquareMiddle(), enemy.getY()-BombermanComponent.getSquareMiddle())){
		return true;
	    }
	}
	return false;
    }

    public boolean collisionWithBombs(AbstractCharacter abstractCharacter) {
	boolean playerLeftBomb = true;

	for (Bomb bomb : bombList) {
	    if (abstractCharacter instanceof Player) {
		playerLeftBomb = bomb.isPlayerLeft();
	    }
	    if(playerLeftBomb && collidingCircles(abstractCharacter, squareToPixel(bomb.getColIndex()), squareToPixel(bomb.getRowIndex()))){
		return true;
	    }
	}
	return false;
    }


    public boolean collisionWithBlock(AbstractCharacter abstractCharacter){
	//Maybe create if statements to only check nearby squares
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		if(getFloorTile(i, j) != FloorTile.FLOOR){
		    boolean isIntersecting = squareCircleInstersect(i, j, abstractCharacter);
		    if (isIntersecting) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public void collisionWithPowerup() {
	for (AbstractPowerup powerup : powerupList) {
	    if(collidingCircles(player, powerup.getX()-BombermanComponent.getSquareMiddle(), powerup.getY()-BombermanComponent.getSquareMiddle())){
		powerup.addToPlayer(player);
		powerupList.remove(powerup);
		break;
	    }
	}
    }



    public boolean squareHasBomb(int rowIndex, int colIndex){
	for (Bomb b: bombList) {
	    if(b.getRowIndex()==rowIndex && b.getColIndex()==colIndex){
		return true;
	    }
	}
	return false;
    }


    public void checkIfPlayerLeftBomb(){
	for (Bomb bomb: bombList) {
	    if(!bomb.isPlayerLeft()){
		if(!collidingCircles(player, squareToPixel(bomb.getColIndex()), squareToPixel(bomb.getRowIndex()))){
		    bomb.setPlayerLeft(true);
		}
	    }
	}
    }

    private boolean bombCoordinateCheck(int eRow, int eCol, boolean open){
	if(tiles[eRow][eCol] != FloorTile.FLOOR){open = false;}
	if(tiles[eRow][eCol] == FloorTile.BREAKABLEBLOCK){
	    tiles[eRow][eCol] = FloorTile.FLOOR;
	    spawnPowerup(eRow, eCol);
	}
	if(tiles[eRow][eCol] != FloorTile.UNBREAKABLEBLOCK){explosionCoords.add(new Explosion(eRow, eCol));}
	return open;
    }

    private boolean collidingCircles(AbstractCharacter abstractCharacter, int x, int y){
	int a = abstractCharacter.getX() - x - BombermanComponent.getSquareMiddle();
	int b = abstractCharacter.getY() - y - BombermanComponent.getSquareMiddle();
	int a2 = a * a;
	int b2 = b * b;
	double c = Math.sqrt(a2 + b2);
	return(abstractCharacter.getSize() > c);
    }

    private boolean squareCircleInstersect(int row, int col, AbstractCharacter abstractCharacter) {
	//http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
	int characterX = abstractCharacter.getX();
	int characterY = abstractCharacter.getY();

	int circleRadius = abstractCharacter.getSize() / 2;
	int squareSize = BombermanComponent.getSquareSize();
	int squareCenterX = (col*squareSize)+(squareSize/2);
	int squareCenterY = (row*squareSize)+(squareSize/2);

	int circleDistanceX = Math.abs(characterX - squareCenterX);
	int circleDistanceY = Math.abs(characterY - squareCenterY);

	if (circleDistanceX > (squareSize/2 + circleRadius)) { return false; }
	if (circleDistanceY > (squareSize/2 + circleRadius)) { return false; }

	if (circleDistanceX <= (squareSize/2)) { return true; }
	if (circleDistanceY <= (squareSize/2)) { return true; }

	int cornerDistance = (circleDistanceX - squareSize/2)^2 +
							      (circleDistanceY - squareSize/2)^2;

	return (cornerDistance <= (circleRadius^2));
    }
}