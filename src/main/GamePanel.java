package main;

import ai.PathFinder;
import data.SaveLoad;
import entity.Entity;
import entity.Player;
import environment.EnvironmentManager;
import interactiveTile.InteractiveTile;
import tile.Map;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48*48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 786 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    // FOR FULL SCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = true;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Config config = new Config(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);
    public PathFinder pFinder = new PathFinder(this);
    public EnvironmentManager eManager = new EnvironmentManager(this);
    Map map = new Map(this);
    SaveLoad saveLoad = new SaveLoad(this);
    public EntityGenerator eGenerator = new EntityGenerator(this);
    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity[][] obj = new Entity[maxMap][30];
    public Entity[][] npc = new Entity[maxMap][10];
    public Entity[][] monster = new Entity[maxMap][20];
    public Entity[][] projectile = new Entity[maxMap][20];
    public InteractiveTile[][] iTile = new InteractiveTile[maxMap][50];
    ArrayList<Entity> entityList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
    public final int sleepState = 9;
    public final int mapState = 10;

    // AREA
    public int currentArea;
    public int nextArea;
    public final int outside = 50;
    public final int indoor = 51;
    public final int dungeon = 52;

    // FPS
    int FPS = 60;

    // CURSOR
    Cursor pointer, clicker, clicker2;
    int mouseClickX, mouseClickY;
    public int selectedSlotX = -1;
    public int selectedSlotY = -1;
    public int selectedX = -1;
    public int selectedY = -1;

    //  NEW GAME
    public final Rectangle newGame = new Rectangle(screenWidth / 2 - 210 / 2 + 2, (int) (tileSize * 7.5) + 11, 210, 40);

    // LOAD GAME
    public final Rectangle loadGame = new Rectangle(screenWidth / 2 - 210 / 2 + 2, (int) (tileSize * 8.5) + 11, 210, 40);

    // QUIT
    public final Rectangle quitGame = new Rectangle(screenWidth / 2 - 210 / 2 + 2, (int) (tileSize * 9.5) + 11, 210, 40);

    // FIGHTER
    public final Rectangle fighter = new Rectangle(screenWidth / 2 - 210 / 2 + 2, tileSize * 4 + 13, 210, 40);

    // THIEF
    public final Rectangle thief = new Rectangle(screenWidth / 2 - 210 / 2 + 2, tileSize * 5 + 13, 210, 40);

    // SORCERER
    public final Rectangle sorcerer = new Rectangle(screenWidth / 2 - 210 / 2 + 2, tileSize * 6 + 13, 210, 40);

    // BACK
    public final Rectangle back = new Rectangle(screenWidth / 2 - 210 / 2 + 2, tileSize * 9 + 13, 210, 40);

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
    }

    public void setupGame() {
        assetSetter.setObject();
        assetSetter.setNPC();
        assetSetter.setMonster();
        assetSetter.setInteractiveTile();
        eManager.setUp();
        gameState = titleState;
        currentArea = outside;

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();

        if(fullScreenOn) {
            setFullScreen();
        }
    }

    public void resetGame(boolean restart) {
        currentArea = outside;
        player.setDefaultPositions();
        player.restoreStatus();
        player.restCounters();
        assetSetter.setNPC();
        assetSetter.setMonster();

        if(restart) {
            player.setDefault();
            assetSetter.setObject();
            assetSetter.setInteractiveTile();
            eManager.lighting.resetDay();
        }
    }

    public void setFullScreen() {
        // GET LOCAL SCREEN
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        // GET FULL SCREEN WIDTH AND HEIGHT
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                drawToTempScreen();
                drawToScreen();
                delta--;
            }
        }
    }

    public void update() {
        if(gameState == playState) {
            player.update();
            setCursor(pointer);

            // NPC
            for(int i = 0; i < npc[1].length; i++) {
                if(npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            for(int i = 0; i < monster[1].length; i++) {
                if(monster[currentMap][i] != null) {
                    if(monster[currentMap][i].alive && !monster[currentMap][i].dying) {
                        monster[currentMap][i].update();
                    }

                    if(!monster[currentMap][i].alive) {
                        monster[currentMap][i].checkDrop();
                        monster[currentMap][i] = null;
                    }
                }
            }

            for(int i = 0; i < projectile[1].length; i++) {
                if(projectile[currentMap][i] != null) {
                    if(projectile[currentMap][i].alive)
                        projectile[currentMap][i].update();
                    if(!projectile[currentMap][i].alive)
                        projectile[currentMap][i] = null;
                }
            }

            for(int i = 0; i < particleList.size(); i++) {
                if(particleList.get(i) != null) {
                    if(particleList.get(i).alive)
                        particleList.get(i).update();
                    if(!particleList.get(i).alive)
                        particleList.remove(i);
                }
            }
        }

        for (int i = 0; i < iTile[1].length; i++) {
            if (iTile[currentMap][i] != null) {
                iTile[currentMap][i].update();
            }
        }
        eManager.update();
    }

    public void drawToTempScreen() {
        // DEBUG
        long drawStart = 0;
        if(keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        // TITLE SCREEN
        if(gameState == titleState) {
            ui.draw(g2);
        } 
        // MAP SCREEN
        else if (gameState == mapState) {
            map.drawFullMapScreen(g2);
        }
        // OTHERS
        else {
            // TILE
            tileM.draw(g2);

            // INTERACTIVE TILES
            for (int i = 0; i < iTile[1].length; i++) {
                if (iTile[currentMap][i] != null) {
                    iTile[currentMap][i].draw(g2);
                }
            }

            // ADD ENTITIES TO THE LIST
            entityList.add(player);

            for(int i = 0; i < npc[1].length; i++) {
                if(npc[currentMap][i] != null) {
                    entityList.add(npc[currentMap][i]);
                }
            }

            for (int i = 0; i < obj[1].length; i++) {
                if (obj[currentMap][i] != null) {
                    entityList.add(obj[currentMap][i]);
                }
            }

            for (int i = 0; i < monster[1].length; i++) {
                if (monster[currentMap][i] != null) {
                    entityList.add(monster[currentMap][i]);
                }
            }

            for (int i = 0; i < projectile[1].length; i++) {
                if (projectile[currentMap][i] != null) {
                    entityList.add(projectile[currentMap][i]);
                }
            }

            for (Entity value : particleList) {
                if (value != null) {
                    entityList.add(value);
                }
            }

            // SORT
            entityList.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    // result returns : (x=y : 0, x>y : >0, x<y : <0)
                    return Integer.compare(e1.worldY, e2.worldY);
                }
            });

            // DRAW ENTITIES
            for (Entity entity : entityList) {
                entity.draw(g2);
            }

            // EMPTY ENTITY LIST
            entityList.clear();

            // ENVIROMENT
            eManager.draw(g2);

            // MINI MAP
            map.drawMiniMap(g2);

            // UI
            ui.draw(g2);
        }

        if(keyH.checkDrawTime) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.white);
            int x = 10;
            int y = 400;
            int lineHeight = 20;

            g2.drawString("WorldX: " + player.worldX, x, y); y += lineHeight;
            g2.drawString("WorldY: " + player.worldY, x, y); y += lineHeight;
            g2.drawString("Col: " + (player.worldX + player.solidArea.x) / tileSize, x, y); y += lineHeight;
            g2.drawString("Row: " + (player.worldY + player.solidArea.y) / tileSize, x, y); y += lineHeight;
            g2.drawString("Draw Time: " + passed, x, y); y += lineHeight;
            g2.drawString("OP: " + keyH.op, x, y);
        }
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public int scaleWidth(int num) {
        if(fullScreenOn) {
            return (num * screenWidth / screenWidth2);
        }
        else return num;
    }

    public int scaleHeight(int num) {
        if(fullScreenOn) {
            return (num * screenHeight / screenHeight2);
        }
        else return num;
    }

    public void changeArea() {
        if(nextArea != currentArea) {
            stopMusic();

            if(nextArea == outside) {
                playMusic(0);
            }

            if(nextArea == indoor) {
                playMusic(19);
            }

            if(nextArea == dungeon) {
                playMusic(20);
            }
            assetSetter.setNPC();
        }
        currentArea = nextArea;
        assetSetter.setMonster();
    }
}
