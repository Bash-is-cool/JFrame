package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

    // DEBUG
    boolean checkDrawTime = false;
    public boolean hitBox = false;

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

                if (code == KeyEvent.VK_ENTER) {
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

                if (code == KeyEvent.VK_ENTER) {
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
        }

        // PLAY STATE
        else if(gp.gameState == gp.playState) {
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

            // DEBUG
            if (code == KeyEvent.VK_T) {
                if (!checkDrawTime) {
                    checkDrawTime = true;
                } else if (checkDrawTime) {
                    checkDrawTime = false;
                }
            }

            if (code == KeyEvent.VK_B) {
                if (!hitBox) {
                    hitBox = true;
                } else if (hitBox) {
                    hitBox = false;
                }
            }

            if(code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
        }

        // PAUSE STATE
        else if(gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_P) {
                gp.gameState = gp.playState;
            }
        }

        //DIALOGUE STATE
        else if(gp.gameState == gp.dialogueState) {
            if(code == KeyEvent.VK_ENTER) {
                gp.gameState = gp.playState;
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
}
