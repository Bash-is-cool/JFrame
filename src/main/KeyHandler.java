package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // DEBUG
    boolean checkDrawTime = false;
    public boolean hitBox = false;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            upPressed = true;
        }

        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            downPressed = true;
        }

        if(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = true;
        }

        if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = true;
        }

        // DEBUG
        if(code == KeyEvent.VK_T) {
            if(!checkDrawTime) {
                checkDrawTime = true;
            } else if(checkDrawTime) {
                checkDrawTime = false;
            }
        }

        if(code == KeyEvent.VK_B) {
            if(!hitBox) {
                hitBox = true;
            } else if(hitBox) {
                hitBox = false;
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
