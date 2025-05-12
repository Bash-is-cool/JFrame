package main;

import entity.Entity;
import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48*48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 786 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity[] obj = new Entity[10];
    public Entity[] npc = new Entity[10];
    public Entity[] monster = new Entity[20];
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS
    int FPS = 60;

    // CURSOR
    Cursor cursor, pointer;

    //  NEW GAME
    public final int newGameWidth = 210;
    public final int newGameHeight = 40;
    public final int newGameX = screenWidth / 2 - newGameWidth / 2 + 2;
    public final int newGameY = (int) (tileSize * 7.5) + 11;

    // LOAD GAME
    public final int loadGameWidth = 210;
    public final int loadGameHeight = 40;
    public final int loadGameX = screenWidth / 2 - loadGameWidth / 2 + 2;
    public final int loadGameY = (int) (tileSize * 8.5) + 11;

    // QUIT
    public final int quitWidth = 210;
    public final int quitHeight = 40;
    public final int quitX = screenWidth / 2 - quitWidth / 2 + 2;
    public final int quitY = (int) (tileSize * 9.5) + 11;

    // FIGHTER
    public final int fighterWidth = 210;
    public final int fighterHeight = 40;
    public final int fighterX = screenWidth / 2 - fighterWidth / 2 + 2;
    public final int fighterY = tileSize * 4 + 13;

    // THIEF
    public final int thiefWidth = 210;
    public final int thiefHeight = 40;
    public final int thiefX = screenWidth / 2 - thiefWidth / 2 + 2;
    public final int thiefY = tileSize * 5 + 13;

    // SORCERER
    public final int sorcererWidth = 210;
    public final int sorcererHeight = 40;
    public final int sorcererX = screenWidth / 2 - sorcererWidth / 2 + 2;
    public final int sorcererY = tileSize * 6 + 13;

    // BACK
    public final int backWidth = 210;
    public final int backHeight = 40;
    public final int backX = screenWidth / 2 - backWidth / 2 + 2;
    public final int backY = tileSize * 9 + 13;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);

        // CURSOR
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image defaultImg = toolkit.getImage("res/cursor/point.png");
        Image hoverImg = toolkit.getImage("res/cursor/click.png");

        cursor = toolkit.createCustomCursor(defaultImg, new Point(0, 0), "Default");
        pointer = toolkit.createCustomCursor(hoverImg, new Point(0, 0), "Hover");
        setCursor(cursor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameState == titleState) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    if (ui.titleScreenState == 0) {
                        if (mouseX >= newGameX && mouseX <= newGameX + newGameWidth && mouseY >= newGameY && mouseY <= newGameY + newGameHeight) {
                            ui.titleScreenState = 1;
                        } else if (mouseX >= loadGameX && mouseX <= loadGameX + loadGameWidth && mouseY >= loadGameY && mouseY <= loadGameY + loadGameHeight) {
                            // Load Game Logic
                        } else if (mouseX >= quitX && mouseX <= quitX + quitWidth && mouseY >= quitY && mouseY <= quitY + quitHeight) {
                            System.exit(0);
                        }
                    } else if (ui.titleScreenState == 1) {
                        if (mouseX >= fighterX && mouseX <= fighterX + fighterWidth && mouseY >= fighterY && mouseY <= fighterY + fighterHeight) {
                            playMusic(0);
                            gameState = playState;
                        } else if (mouseX >= thiefX && mouseX <= thiefX + thiefWidth && mouseY >= thiefY && mouseY <= thiefY + thiefHeight) {
                            playMusic(0);
                            gameState = playState;
                        } else if (mouseX >= sorcererX && mouseX <= sorcererX + sorcererWidth && mouseY >= sorcererY && mouseY <= sorcererY + sorcererHeight) {
                            playMusic(0);
                            gameState = playState;
                        } else if (mouseX >= backX && mouseX <= backX + backWidth && mouseY >= backY && mouseY <= backY + backHeight) {
                            ui.titleScreenState = 0;
                            ui.commandNum = 0;
                        }
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == titleState) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    if (ui.titleScreenState == 0) {
                        if (mouseX >= newGameX && mouseX <= newGameX + newGameWidth && mouseY >= newGameY && mouseY <= newGameY + newGameHeight) {
                            setCursor(pointer);
                            ui.commandNum = 0;
                        } else if (mouseX >= loadGameX && mouseX <= loadGameX + loadGameWidth && mouseY >= loadGameY && mouseY <= loadGameY + loadGameHeight) {
                            setCursor(pointer);
                            ui.commandNum = 1;
                        } else if (mouseX >= quitX && mouseX <= quitX + quitWidth && mouseY >= quitY && mouseY <= quitY + quitHeight) {
                            setCursor(pointer);
                            ui.commandNum = 2;
                        } else {
                            setCursor(cursor);
                        }
                    } else if (ui.titleScreenState == 1) {
                        if (mouseX >= fighterX && mouseX <= fighterX + fighterWidth && mouseY >= fighterY && mouseY <= fighterY + fighterHeight) {
                            setCursor(pointer);
                            ui.commandNum = 0;
                        } else if (mouseX >= thiefX && mouseX <= thiefX + thiefWidth && mouseY >= thiefY && mouseY <= thiefY + thiefHeight) {
                            setCursor(pointer);
                            ui.commandNum = 1;
                        } else if (mouseX >= sorcererX && mouseX <= sorcererX + sorcererWidth && mouseY >= sorcererY && mouseY <= sorcererY + sorcererHeight) {
                            setCursor(pointer);
                            ui.commandNum = 2;
                        } else if (mouseX >= backX && mouseX <= backX + backWidth && mouseY >= backY && mouseY <= backY + backHeight) {
                            setCursor(pointer);
                            ui.commandNum = 3;
                        } else {
                            setCursor(cursor);
                        }
                    }
                }
            }
        });
    }

    public void setupGame() {
        assetSetter.setObject();
        assetSetter.setNPC();
        assetSetter.setMonster();
        gameState = titleState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /*@Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(gameThread != null) {
            long currentTime = System.nanoTime();
            //long currentTime2 = System.currentTimeMillis();

            System.out.println(currentTime);

            update();
            repaint();
            double remainingTime = nextDrawTime - System.nanoTime();
            remainingTime = remainingTime / 1000000;

            if(remainingTime < 0) {
                remainingTime = 0;
            }

            try {
                Thread.sleep((long)remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
            }
        }
    }*/

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
                repaint();
                delta--;
            }

            /*if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }*/
        }
    }

    public void update() {
        if(gameState == playState) {
            player.update();
            setCursor(cursor);

            // NPC
            for (Entity entity : npc) {
                if (entity != null) {
                    entity.update();
                }
            }

            for(int i = 0; i < monster.length; i++) {
                if(monster[i] != null) {
                    if(monster[i].alive && !monster[i].dying)
                        monster[i].update();
                    if(!monster[i].alive)
                        monster[i] = null;
                }
            }
        }

        if(gameState == pauseState) {

        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // DEBUG
        long drawStart = 0;
        if(keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        // TITLE SCREEN
        if(gameState == titleState) {
            ui.draw(g2);
        } else {
            // TILE
            tileM.draw(g2);

            // ADD ENTITIES TO THE LIST
            entityList.add(player);

            for (Entity entity : npc) {
                if (entity != null) {
                    entityList.add(entity);
                }
            }

            for (Entity entity : obj) {
                if (entity != null) {
                    entityList.add(entity);
                }
            }

            for (Entity entity : monster) {
                if (entity != null) {
                    entityList.add(entity);
                }
            }

            // SORT
            entityList.sort(Comparator.comparingInt(e -> e.worldY));

            // DRAW ENTITIES
            for (Entity entity : entityList) {
                entity.draw(g2);
            }
            // EMPTY ENTITY LIST
            entityList.clear();

            // UI
            ui.draw(g2);
        }

        if(keyH.checkDrawTime) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("Draw Time: " + passed);
        }

        g2.dispose();
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
}
