package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    long lastEnterPressed;
    long enterCooldown = 500;
    // DEBUG
    boolean checkDrawTime = false;
    public boolean hitBox = false;
    public boolean click = false;
    Graphics2D g2;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if(gp.gameState == gp.titleState) {
            titleState(code);
        }

        // PLAY STATE
        else if(gp.gameState == gp.playState) {
            playState(code);
        }
        // PAUSE STATE
        else if(gp.gameState == gp.pauseState) {
            pauseState(code);
        }

        // DIALOGUE STATE
        else if(gp.gameState == gp.dialogueState) {
            dialogueState(code);
        }

        // CHARACTER STATE
        else if(gp.gameState == gp.characterState) {
            characterState(code);
        }
    }

    public void titleState(int code) {
        long time = System.currentTimeMillis();
        if(gp.ui.titleScreenState == 0) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;

                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2;
                }
            }

            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;

                if (gp.ui.commandNum > 2) {
                    gp.ui.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER && checkEnter(time)) {
                switch (gp.ui.commandNum) {
                    case 0:
                        gp.ui.titleScreenState = 1;
                        break;
                    case 1:
                        break;
                    case 2:
                        System.exit(0);
                }
            }

            if(code == KeyEvent.VK_B) {
                gp.keyH.click = !gp.keyH.click;
                gp.ui.draw(g2);
            }
        } else if(gp.ui.titleScreenState == 1) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;

                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 3;
                }
            }

            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;

                if (gp.ui.commandNum > 3) {
                    gp.ui.commandNum = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER && checkEnter(time)) {
                switch (gp.ui.commandNum) {
                    case 0:
                        gp.playMusic(0);
                        gp.gameState = gp.playState;
                        break;
                    case 1:
                        gp.playMusic(0);
                        gp.gameState = gp.playState;
                        break;
                    case 2:
                        gp.playMusic(0);
                        gp.gameState = gp.playState;
                        break;
                    case 3:
                        gp.ui.titleScreenState = 0;
                        gp.ui.commandNum = 0;
                        break;
                }
            }
        }

        if(code == KeyEvent.VK_B) {
            gp.keyH.click = !gp.keyH.click;
            gp.ui.draw(g2);
        }
    }

    public void playState(int code) {
        long time = System.currentTimeMillis();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            upPressed = true;
        }

        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            downPressed = true;
        }

        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = true;
        }

        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = true;
        }

        if (code == KeyEvent.VK_P) {
            gp.gameState = gp.pauseState;
        }

        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.characterState;
        }

        // DEBUG
        if (code == KeyEvent.VK_T) {
            checkDrawTime = !checkDrawTime;
        }

        if (code == KeyEvent.VK_B) {
            hitBox = !hitBox;
        }

        if(code == KeyEvent.VK_ENTER && checkEnter(time)) {
            enterPressed = true;
        }

        if(code == KeyEvent.VK_R) {
            gp.tileM.loadMap("/maps/worldV2.txt");
        }
    }

    public void pauseState(int code) {
        if (code == KeyEvent.VK_P) {
            gp.gameState = gp.playState;
        }
    }

    public void dialogueState(int code) {
        long time = System.currentTimeMillis();
        if(code == KeyEvent.VK_ENTER && checkEnter(time)) {
            gp.gameState = gp.playState;
        }
    }

    public void characterState(int code) {
        if(code == KeyEvent.VK_C) {
            gp.gameState = gp.playState;
        }

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if(gp.ui.slotRow != 0) {
                gp.ui.slotRow--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if(gp.ui.slotCol != 0) {
                gp.ui.slotCol--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if(gp.ui.slotRow != 3) {
                gp.ui.slotRow++;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_DOWN) {
            if(gp.ui.slotCol != 4) {
                gp.ui.slotCol++;
                gp.playSE(9);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            upPressed = false;
        }

        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            downPressed = false;
        }

        if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = false;
        }
    }

    public boolean checkEnter(long time) {
        if(time - lastEnterPressed >= enterCooldown) {
            lastEnterPressed = time;
            return true;
        }
        return false;
    }
}
