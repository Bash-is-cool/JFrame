package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, spacePressed;
    long lastEnterPressed;
    long enterCooldown = 500;
    // DEBUG
    boolean checkDrawTime = false;
    public boolean hitBox = false;
    public boolean click = false;
    public boolean muted = true;
    public boolean shotKeyPressed;
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

        // OPTION STATE
        else if(gp.gameState == gp.optionState) {
            optionState(code);
        }

        // GAME OVER STATE
        else if(gp.gameState == gp.gameOverState) {
            gameOverState(code);
        }

        // TRADE STATE
        else if(gp.gameState == gp.tradeState) {
            tradeState(code);
        }

        // MAP STATE
        else if (gp.gameState == gp.mapState) {
            mapState(code);
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
                        gp.saveLoad.load();
                        gp.gameState = gp.playState;
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
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
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

        if(code == KeyEvent.VK_E) {
            gp.gameState = gp.characterState;
        }

        if (code == KeyEvent.VK_F) {
            shotKeyPressed = true;
        }

        if(code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.optionState;
        }

        if(code == KeyEvent.VK_M) {
            gp.gameState = gp.mapState;
        }

        if(code == KeyEvent.VK_X) {
            gp.map.miniMapOn = !gp.map.miniMapOn;
        }

        if(code == KeyEvent.VK_SPACE) {
            spacePressed = true;
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
            switch(gp.currentMap) {
                case 0:
                    gp.tileM.loadMap("/maps/worldV3.txt", 0);
                    break;
                case 1:
                    gp.tileM.loadMap("/maps/interior01.txt", 1);
                    break;
            }
        }

        if(code == KeyEvent.VK_H) {
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
        }

        if(code == KeyEvent.VK_1) {
            gp.assetSetter.setObject();
            gp.assetSetter.setMonster();
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
        if(code == KeyEvent.VK_E) {
            gp.gameState = gp.playState;
        }

        if(code == KeyEvent.VK_ENTER) {
            gp.player.selectItem();
            gp.playSE(10);
        }
        gp.ui.saveSlotPosition();
        playerInventory(code);
    }

    public void playerInventory(int code) {
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if(gp.ui.playerSlotRow != 0) {
                gp.ui.playerSlotRow--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if(gp.ui.playerSlotCol != 0) {
                gp.ui.playerSlotCol--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if(gp.ui.playerSlotRow != 3) {
                gp.ui.playerSlotRow++;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if(gp.ui.playerSlotCol != 4) {
                gp.ui.playerSlotCol++;
                gp.playSE(9);
            }
        }
    }

    public void npcInventory(int code) {
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            if(gp.ui.npcSlotRow != 0) {
                gp.ui.npcSlotRow--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if(gp.ui.npcSlotCol != 0) {
                gp.ui.npcSlotCol--;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            if(gp.ui.npcSlotRow != 3) {
                gp.ui.npcSlotRow++;
                gp.playSE(9);
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if(gp.ui.npcSlotCol != 4) {
                gp.ui.npcSlotCol++;
                gp.playSE(9);
            }
        }
    }

    public void optionState(int code) {
        long time = System.currentTimeMillis();
        if(code == KeyEvent.VK_ESCAPE) {
            gp.gameState = gp.playState;
        }

        if(code == KeyEvent.VK_ENTER && checkEnter(time)) {
            enterPressed = true;
        }

        int maxCommandNum = switch (gp.ui.subState) {
            case 0 -> 5;
            case 3 -> 1;
            default -> 0;
        };

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            gp.playSE(9);

            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = maxCommandNum;
            }
        }

        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            gp.playSE(9);

            if (gp.ui.commandNum > maxCommandNum) {
                gp.ui.commandNum = 0;
            }
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            if(gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.music.volumeScale > 0) {
                    gp.music.volumeScale--;
                    gp.music.checkVolume();
                    gp.playSE(10);
                }
                if (gp.ui.commandNum == 2 && gp.se.volumeScale > 0) {
                    gp.se.volumeScale--;
                    gp.playSE(10);
                }
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            if(gp.ui.subState == 0) {
                if(gp.ui.commandNum == 1 && gp.music.volumeScale < 5) {
                    gp.music.volumeScale++;
                    gp.music.checkVolume();
                    gp.playSE(10);
                }

                if (gp.ui.commandNum == 2 && gp.se.volumeScale < 5) {
                    gp.se.volumeScale++;
                    gp.playSE(10);
                }
            }
        }
    }

    public void gameOverState(int code) {
        long time = System.currentTimeMillis();
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.ui.commandNum--;
            if(gp.ui.commandNum < 0) {
                gp.ui.commandNum = 1;
            }
            gp.playSE(9);
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.ui.commandNum++;
            if(gp.ui.commandNum > 1) {
                gp.ui.commandNum = 0;
            }
            gp.playSE(9);
        }

        if(code == KeyEvent.VK_ENTER && checkEnter(time)) {
            if(gp.ui.commandNum == 0) {
                gp.gameState = gp.playState;
                gp.resetGame(false);
            } else if(gp.ui.commandNum == 1) {
                gp.ui.titleScreenState = 0;
                gp.stopMusic();
                gp.gameState = gp.titleState;
                gp.resetGame(true);
            }
        }
    }

    public void tradeState(int code) {
        if(code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }

        if(gp.ui.subState == 0) {
            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if(gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2;
                }
                gp.playSE(9);
            }

            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if(gp.ui.commandNum > 2) {
                    gp.ui.commandNum = 0;
                }
                gp.playSE(9);
            }
        }

        if(gp.ui.subState == 1) {
            npcInventory(code);
            if(code == KeyEvent.VK_ESCAPE) {
                gp.ui.subState = 0;
            }
        }

        if(gp.ui.subState == 2) {
            playerInventory(code);
            if(code == KeyEvent.VK_ESCAPE) {
                gp.ui.subState = 0;
            }
        }
    }

    public void mapState(int code) {
        if(code == KeyEvent.VK_M) {
            gp.gameState = gp.playState;
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

        if (code == KeyEvent.VK_F) {
            shotKeyPressed = false;
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }

        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
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
