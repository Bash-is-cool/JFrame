package main;

import entity.Entity;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_Mana_Crystal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font maruMonica, purisaB;
    BufferedImage heart_full, heart_half, heart_blank, crystal_full, crystal_blank, coin;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialogue;
    public int commandNum = 0;
    public int titleScreenState = 0; // 0: the first screen
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;
    public int prevSlotCol = 0;
    public int prevSlotRow = 0;
    int subState = 0;
    int counter;
    public Entity npc;



     public UI(GamePanel gp) {
         this.gp = gp;

         try {
             InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
             maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
             is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
             purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
         } catch (FontFormatException | IOException e) {
             e.printStackTrace();
         }

         // CREATE HUD OBJECT
         Entity heart = new OBJ_Heart(gp);
         heart_full = heart.image;
         heart_half = heart.image2;
         heart_blank = heart.image3;
         Entity crystal = new OBJ_Mana_Crystal(gp);
         crystal_full = crystal.image;
         crystal_blank = crystal.image2;
         Entity bronzeCoin = new OBJ_Coin_Bronze(gp);
         coin = bronzeCoin.down1;

     }

     public void addMessage(String text) {
         message.add(text);
         messageCounter.add(0);
     }

     public void draw(Graphics2D g2) {
         this.g2 = g2;

         g2.setFont(maruMonica);
         g2.setColor(Color.white);

         // PLAY STATE
         if(gp.gameState == gp.playState) {
             drawPlayerLife();
             drawMessage();
         }

         // PAUSE STATE
         if(gp.gameState == gp.pauseState) {
             drawPlayerLife();
             drawPauseScreen();
         }

         // DIALOGUE STATE
         if(gp.gameState == gp.dialogueState) {
             drawDialogueScreen();
         }

         if(gp.gameState == gp.titleState) {
             drawTitleScreen();
             if (gp.keyH.click && gp.ui.titleScreenState == 0) {
                 g2.setColor(Color.white);
                 g2.drawRect(gp.newGame.x, gp.newGame.y, gp.newGame.width, gp.newGame.height);
                 g2.drawRect(gp.loadGame.x, gp.loadGame.y, gp.loadGame.width, gp.loadGame.height);
                 g2.drawRect(gp.quitGame.x, gp.quitGame.y, gp.quitGame.width, gp.quitGame.height);
             } else if (gp.keyH.click && gp.ui.titleScreenState == 1) {
                 g2.drawRect(gp.thief.x, gp.thief.y, gp.thief.width, gp.thief.height);
                 g2.drawRect(gp.fighter.x, gp.fighter.y, gp.fighter.width, gp.fighter.height);
                 g2.drawRect(gp.sorcerer.x, gp.sorcerer.y, gp.sorcerer.width, gp.sorcerer.height);
                 g2.drawRect(gp.back.x, gp.back.y, gp.back.width, gp.back.height);
             }
         }

         // CHARACTER STATE
         if(gp.gameState == gp.characterState) {
             drawCharacterScreen();
             drawInventory(gp.player, true);
         }

         // OPTIONS STATE
         if(gp.gameState == gp.optionState) {
             drawOptionScreen();
         }

         // GAME OVER STATE
         if(gp.gameState == gp.gameOverState) {
             drawGameOverScreen();
         }

         // TRANSITION STATE
         if(gp.gameState == gp.transitionState) {
             drawTransition();
         }

         // TRADE STATE
         if(gp.gameState == gp.tradeState) {
             drawTradeScreen();
         }
     }

     public void drawGameOverScreen() {
         g2.setColor(new Color(0, 0, 0, 150));
         g2.fillRoundRect(0, 0, gp.screenWidth, gp.screenHeight, 10, 10);

         int x, y;
         String text;
         g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

         text = "Game Over";
         // SHADOW
         g2.setColor(Color.black);
         x = getXForCenteredText(text);
         y = gp.tileSize * 4;
         g2.drawString(text, x, y);

         // MAIN
         g2.setColor(Color.white);
         g2.drawString(text, x - 4, y -4);

         // Retry
         g2.setFont(g2.getFont().deriveFont(50F));
         text = "Retry";
         x = getXForCenteredText(text);
         y += gp.tileSize * 4;
         g2.drawString(text, x, y);
         if(commandNum == 0) {
             g2.drawString(">", x - 40, y);
         }

         // Back to Title Screen
         text = "Quit";
         x = getXForCenteredText(text);
         y += 55;
         g2.drawString(text, x, y);
         if(commandNum == 1) {
             g2.drawString(">", x - 40, y);
         }
     }

     public void drawPlayerLife() {
         int x = gp.tileSize / 2;
         int y = gp.tileSize / 2;
         int i = 0;

         // DRAW BLANK HEART
         while(i < gp.player.maxLife / 2) {
             g2.drawImage(heart_blank, x, y, null);
             i++;
             x += gp.tileSize;
         }

         // RESET
         x = gp.tileSize / 2;
         y = gp.tileSize / 2;
         i = 0;

         // DRAW CURRENT LIFE
         while(i < gp.player.life) {
             g2.drawImage(heart_half, x, y, null);
             i++;
             if(i < gp.player.life) {
                 g2.drawImage(heart_full, x, y, null);
             }
             i++;
             x += gp.tileSize;
         }

         // DRAW MAX MANA
         x = (gp.tileSize / 2) - 5;
         y = (int) (gp.tileSize * 1.5);
         i = 0;
         while(i < gp.player.maxMana) {
             g2.drawImage(crystal_blank, x, y, null);
             i++;
             x += 35;
         }

         // DRAW MANA
         x = (gp.tileSize / 2) - 5;
         y = (int) (gp.tileSize * 1.5);         i = 0;
         while (i < gp.player.mana) {
             g2.drawImage(crystal_full, x, y, null);
             i++;
             x += 35;
         }
     }

     public void drawMessage() {
         int messageX = gp.tileSize;
         int messageY = gp.tileSize * 4;
         g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));

         for(int i = 0; i < message.size(); i++) {
             if(message.get(i) != null) {
                 g2.setColor(Color.black);
                 g2.drawString(message.get(i), messageX + 2, messageY + 2);
                 g2.setColor(Color.white);
                 g2.drawString(message.get(i), messageX, messageY);

                 int counter = messageCounter.get(i) + 1;
                 messageCounter.set(i, counter);
                 messageY += 50;

                 if(messageCounter.get(i) > 180) {
                     message.remove(i);
                     messageCounter.remove(i);
                 }
             }
         }
     }

     public void drawPauseScreen() {
         g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80));
         String text = "PAUSE";
         int x = getXForCenteredText(text);
         int y = gp.screenHeight / 2;

         g2.drawString(text, x, y);
     }

     public void drawCharacterScreen() {
         // CREATE FRAME
         final int frameX = gp.tileSize * 2;
         final int frameY = gp.tileSize;
         final int frameWidth = gp.tileSize * 5;
         final int frameHeight = gp.tileSize * 10;
         drawSubWindow(frameX, frameY, frameWidth, frameHeight);

         // TEXT
         g2.setColor(Color.white);
         g2.setFont(g2.getFont().deriveFont(32F));

         int textX = frameX + 20;
         int textY = frameY + gp.tileSize;
         final int lineHeight = 35;

         // NAMES
         g2.drawString("Level", textX, textY);
         textY += lineHeight;
         g2.drawString("Life", textX, textY);
         textY += lineHeight;
         g2.drawString("Mana", textX, textY);
         textY += lineHeight;
         g2.drawString("Strength", textX, textY);
         textY += lineHeight;
         g2.drawString("Dexterity", textX, textY);
         textY += lineHeight;
         g2.drawString("Attack", textX, textY);
         textY += lineHeight;
         g2.drawString("Defense", textX, textY);
         textY += lineHeight;
         g2.drawString("Exp", textX, textY);
         textY += lineHeight;
         g2.drawString("Next Level", textX, textY);
         textY += lineHeight;
         g2.drawString("Coin", textX, textY);
         textY += lineHeight + 10;
         g2.drawString("Weapon", textX, textY);
         textY += lineHeight + 15;
         g2.drawString("Shield", textX, textY);

         // VALUES
         int tailX = (frameX + frameWidth) - 30;
         // Reset textY
         textY = frameY + gp.tileSize;
         String value;
         value = String.valueOf(gp.player.level);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = gp.player.life + "/" + gp.player.maxLife;
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = gp.player.mana + "/" + gp.player.maxMana;
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.strength);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.dexterity);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.attack);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.defense);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.exp);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.nextLevelExp);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         value = String.valueOf(gp.player.coin);
         textX = getXForAlignToRightText(value, tailX);
         g2.drawString(value, textX, textY);
         textY += lineHeight;

         g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY - 24, null);
         textY += gp.tileSize;
         g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY - 24, null);
     }

    public void saveSlotPosition() {
        prevSlotCol = playerSlotCol;
        prevSlotRow = playerSlotRow;
    }

    public void restoreSlotPosition() {
        playerSlotCol = prevSlotCol;
        playerSlotRow = prevSlotRow;
    }

     public void drawInventory(Entity entity, boolean cursor) {
         int frameX, frameY, frameWidth, frameHeight, slotCol, slotRow;

         if(entity == gp.player) {
             frameX = gp.tileSize * 12;
             frameY = gp.tileSize;
             frameWidth = gp.tileSize * 6;
             frameHeight = gp.tileSize * 5;
             slotCol = playerSlotCol;
             slotRow = playerSlotRow;
         } else {
             frameX = gp.tileSize * 2;
             frameY = gp.tileSize;
             frameWidth = gp.tileSize * 6;
             frameHeight = gp.tileSize * 5;
             slotCol = npcSlotCol;
             slotRow = npcSlotRow;
         }

        // FRAME
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // SLOT
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 20;
        int slotX = slotXStart;
        int slotY = slotYStart;
        int slotSize = gp.tileSize + 3;

         // DRAW ITEMS
         for (int i = 0; i < entity.inventory.size(); i++) {
             int x = i;
             int y = 0;
             while (x >= 5) {
                 x -= 4;
                 y++;
             }

             if (entity.inventory.get(i) == entity.currentWeapon || entity.inventory.get(i) == entity.currentShield || entity.inventory.get(i) == entity.currentLight) {
                 g2.setColor(new Color(240, 190, 90));
                 g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
             }

             if (x == playerSlotCol && y == playerSlotRow && gp.selectedX != -1 && gp.selectedY != -1) {
                 g2.drawImage(entity.inventory.get(i).down1, gp.selectedX, gp.selectedY, null);
             } else {
                 g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);
             }

             // DISPLAY AMOUNT
             if(entity == gp.player && entity.inventory.get(i).amount > 1) {
                 g2.setFont(g2.getFont().deriveFont(32f));
                 int amountX, amountY;

                 String s = String.valueOf(entity.inventory.get(i).amount);
                 amountX = getXForAlignToRightText(s, slotX + 44);
                 amountY = slotY + gp.tileSize;

                 // SHADOW
                 g2.setColor(new Color(60, 60, 60));
                 g2.drawString(s, amountX, amountY);

                 // NUMBER
                 g2.setColor(Color.white);
                 g2.drawString(s, amountX - 3, amountY - 3);
             }
             slotX += slotSize;

             if (i == 4 || i == 9 || i == 14) {
                 slotX = slotXStart;
                 slotY += slotSize;
             }
         }

        // CURSOR
         if(cursor) {
             int cursorX = slotXStart + (slotSize * slotCol);
             int cursorY = slotYStart + (slotSize * slotRow);
             int cursorWidth = gp.tileSize;
             int cursorHeight = gp.tileSize;


             // DRAW CURSOR
             g2.setColor(Color.white);
             g2.setStroke(new BasicStroke(3));
             g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

             // DESCRIPTION FRAME
             int dFrameY = frameY + frameHeight;
             int dFrameHeight = gp.tileSize * 3;

             // DRAW DESCRIPTION TEXT
             int textX = frameX + 20;
             int textY = dFrameY + gp.tileSize;
             g2.setFont(g2.getFont().deriveFont(28F));

             int itemIndex = getItemIndexOnSlot(slotCol, slotRow);
             if (itemIndex < entity.inventory.size()) {
                 drawSubWindow(frameX, dFrameY, frameWidth, dFrameHeight);
                 for (String str : entity.inventory.get(itemIndex).description.split("\n")) {
                     g2.drawString(str, textX, textY);
                     textY += 32;
                 }
             }
         }
     }

     public int getXForCenteredText(String text) {
         int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
         return gp.screenWidth / 2 - length / 2;
     }

    public int getXForAlignToRightText(String text, int tailX) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return tailX - length;
    }

     public void drawDialogueScreen() {
         // WINDOW
         int x = gp.tileSize * 3;
         int y = gp.tileSize / 2;
         int width = gp.screenWidth - (gp.tileSize * 6);
         int height = gp.tileSize * 4;

         drawSubWindow(x, y, width, height);

         g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32));
         x += gp.tileSize;
         y += gp.tileSize;

         for(String line : currentDialogue.split("\n")) {
             g2.drawString(line, x, y);
             y += 40;
         }
     }

     public void drawOptionScreen() {
         g2.setColor(Color.white);
         g2.setFont(g2.getFont().deriveFont(32F));

         // SUB WINDOW
         int frameX = gp.tileSize * 6;
         int frameY = gp.tileSize;
         int frameWidth = gp.tileSize * 8;
         int frameHeight = gp.tileSize * 10;
         drawSubWindow(frameX, frameY, frameWidth, frameHeight);

         switch(subState) {
             case 0:
                 optionTop(frameX, frameY);
                 break;
             case 1:
                 fullScreenNotification(frameX, frameY);
                 break;
             case 2:
                 optionControl(frameX, frameY);
                 break;
             case 3:
                 endGameConfirmation(frameX, frameY);
                 break;
         }

         gp.keyH.enterPressed = false;
     }

     public void optionTop(int frameX, int frameY) {
         int textX, textY;

         // TITLE
         String text = "Options";
         textX = getXForCenteredText(text);
         textY = frameY + gp.tileSize;
         g2.drawString(text, textX, textY);

         // FULL SCREEN ON/OFF
         textX = frameX + gp.tileSize;
         textY += gp.tileSize * 2;
         g2.drawString("Full Screen", textX, textY);
         if(commandNum == 0) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 gp.fullScreenOn = !gp.fullScreenOn;
                 subState = 1;
             }
         }

         // MUSIC
         textY += gp.tileSize;
         g2.drawString("Music", textX, textY);
         if(commandNum == 1) {
             g2.drawString(">", textX - 25, textY);
         }

         // SE
         textY += gp.tileSize;
         g2.drawString("Sound Effects", textX, textY);
         if(commandNum == 2) {
             g2.drawString(">", textX - 25, textY);
         }

         // CONTROL
         textY += gp.tileSize;
         g2.drawString("Control", textX, textY);
         if(commandNum == 3) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 subState = 2;
                 commandNum = 0;
             }
         }

         // END GAME
         textY += gp.tileSize;
         g2.drawString("End Game", textX, textY);
         if(commandNum == 4) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 subState = 3;
                 commandNum = 0;
             }
         }

         // BACK
         textY += gp.tileSize * 2;
         g2.drawString("Back", textX, textY);
         if(commandNum == 5) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 gp.gameState = gp.playState;
                 commandNum = 0;
             }
         }

         // FULL SCREEN CHECKBOX
         textX = (int) (frameX + gp.tileSize * 4.5);
         textY = frameY + gp.tileSize * 2 + 24;
         g2.setStroke(new BasicStroke(3));
         g2.drawRoundRect(textX, textY, 24, 24, 10, 10);
         if(gp.fullScreenOn) {
             g2.fillRoundRect(textX, textY, 24, 24, 10, 10);
         }

         // MUSIC VOLUME
         textY += gp.tileSize;
         g2.drawRoundRect(textX, textY, 120, 24, 10, 10);
         int volumeWidth = 24 * gp.music.volumeScale;
         g2.fillRoundRect(textX, textY, volumeWidth, 24, 10, 10);

         // SE
         textY += gp.tileSize;
         g2.drawRoundRect(textX, textY, 120, 24, 10, 10);
         volumeWidth = 24 * gp.se.volumeScale;
         g2.fillRoundRect(textX, textY, volumeWidth, 24, 10, 10);

         gp.config.saveConfig();
     }

     public void fullScreenNotification(int frameX, int frameY) {
         int textX = frameX + gp.tileSize;
         int textY = frameY + gp.tileSize * 3;

         currentDialogue = "The change will take \neffect after restarting \nthe game.";

         for(String str : currentDialogue.split("\n")) {
             g2.drawString(str, textX, textY);
             textY += 40;
         }

         // BACK
         textY = frameY + gp.tileSize * 9;
         g2.drawString("Back", textX, textY);
         if(commandNum == 0) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 subState = 0;
             }
         }
     }

     public void optionControl(int frameX, int frameY) {
         int textX, textY;

         // TITLE
         String text = "Control";
         textX = getXForCenteredText(text);
         textY = frameY + gp.tileSize;
         g2.drawString(text, textX, textY);

         textX = frameX + gp.tileSize;
         textY += gp.tileSize;
         g2.drawString("Move", textX, textY);
         textY += gp.tileSize;
         g2.drawString("Confirm/Attack", textX, textY);
         textY += gp.tileSize;
         g2.drawString("Shoot/Cast", textX, textY);
         textY += gp.tileSize;
         g2.drawString("Character Screen", textX, textY);
         textY += gp.tileSize;
         g2.drawString("Pause", textX, textY);
         textY += gp.tileSize;
         g2.drawString("Options", textX, textY);

         textX = frameX + gp.tileSize * 6;
         textY = frameY + gp.tileSize * 2;
         g2.drawString("WASD", textX, textY);
         textY += gp.tileSize;
         g2.drawString("ENTER", textX, textY);
         textY += gp.tileSize;
         g2.drawString("F", textX, textY);
         textY += gp.tileSize;
         g2.drawString("E", textX, textY);
         textY += gp.tileSize;
         g2.drawString("P", textX, textY);
         textY += gp.tileSize;
         g2.drawString("ESC", textX, textY);

         // BACK
         textX = frameX + gp.tileSize;
         textY = frameY + gp.tileSize * 9;
         g2.drawString("Back", textX, textY);
         if(commandNum == 0) {
             g2.drawString(">", textX - 25, textY);
             if(gp.keyH.enterPressed) {
                 subState = 0;
                 commandNum = 3;
             }
         }
     }

     public void endGameConfirmation(int frameX, int frameY) {
         int textX = frameX + gp.tileSize;
         int textY = frameY + gp.tileSize * 3;

         currentDialogue = "Quit the game and \nreturn to the title screen?";
         for(String line: currentDialogue.split("\n"))
         {
             g2.drawString(line,textX,textY);
             textY += 40;
         }
         //YES
         String text = "Yes";
         textX = getXForCenteredText(text);
         textY += gp.tileSize * 3;
         g2.drawString(text,textX,textY);
         if(commandNum == 0)
         {
             g2.drawString(">",textX-25,textY);
             if(gp.keyH.enterPressed)
             {
                 subState = 0;
                 gp.ui.titleScreenState = 0;
                 gp.gameState = gp.titleState;
                 gp.stopMusic();
             }
         }

         //NO
         text = "No";
         textX = getXForCenteredText(text);
         textY += gp.tileSize;
         g2.drawString(text,textX,textY);
         if(commandNum == 1)
         {
             g2.drawString(">",textX-25,textY);
             if(gp.keyH.enterPressed)
             {
                 subState = 0;
                 commandNum = 4; //back to end row
             }
         }
     }

     public void drawSubWindow(int x, int y, int width, int height) {
         Color c = new Color(0, 0, 0, 210);
         g2.setColor(c);
         g2.fillRoundRect(x, y, width, height, 35, 35);

         c = new Color(255, 255, 255);
         g2.setColor(c);
         g2.setStroke(new BasicStroke(5));
         g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
     }

     public void drawTitleScreen() {
         g2.setColor(new Color(0, 0, 0));
         g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

         if(titleScreenState == 0) {

             // TITLE NAME
             g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96));
             String text = "Blue Boy Adventure";
             int x = getXForCenteredText(text);
             int y = gp.tileSize * 3;

             // SHADOW
             g2.setColor(Color.GRAY);
             g2.drawString(text, x + 5, y + 5);

             // MAIN COLOR
             g2.setColor(Color.WHITE);
             g2.drawString(text, x, y);

             // BLUE BOY IMAGE
             x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
             y += gp.tileSize * 2;
             g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

             // MENU
             g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48));

             text = "NEW GAME";
             x = getXForCenteredText(text);
             y += (int) (gp.tileSize * 3.5);
             g2.drawString(text, x, y);
             if (commandNum == 0) {
                 g2.drawString(">", x - gp.tileSize, y);
             }

             text = "LOAD GAME";
             x = getXForCenteredText(text);
             y += gp.tileSize;
             g2.drawString(text, x, y);
             if (commandNum == 1) {
                 g2.drawString(">", x - gp.tileSize, y);
             }

             text = "QUIT";
             x = getXForCenteredText(text);
             y += gp.tileSize;
             g2.drawString(text, x, y);
             if (commandNum == 2) {
                 g2.drawString(">", x - gp.tileSize, y);
             }
         } else if(titleScreenState == 1) {
             g2.setColor(Color.WHITE);
             g2.setFont(g2.getFont().deriveFont(42F));

             String text = "Select your class!";
             int x = getXForCenteredText(text);
             int y = gp.tileSize * 3;
             g2.drawString(text, x, y);

             text = "Fighter";
             x = getXForCenteredText(text);
             y += gp.tileSize * 2;
             g2.drawString(text, x, y);

             if(commandNum == 0) {
                 g2.drawString(">", x - gp.tileSize, y);
             }

             text = "Thief";
             x = getXForCenteredText(text);
             y += gp.tileSize;
             g2.drawString(text, x, y);

             if(commandNum == 1) {
                 g2.drawString(">", x - gp.tileSize, y);
             }

             text = "Sorcerer";
             x = getXForCenteredText(text);
             y += gp.tileSize;
             g2.drawString(text, x, y);

             if(commandNum == 2) {
                 g2.drawString(">", x - gp.tileSize, y);
             }

             text = "Back";
             x = getXForCenteredText(text);
             y += gp.tileSize * 3;
             g2.drawString(text, x, y);

             if(commandNum == 3) {
                 g2.drawString(">", x - gp.tileSize, y);
             }
         }
     }

     public int getItemIndexOnSlot(int slotCol, int slotRow) {
        return slotCol + (slotRow * 5);
     }

     public void drawTransition() {
         counter++;
         g2.setColor(new Color(0, 0, 0, counter * 5));
         g2.fillRoundRect(0, 0, gp.screenWidth, gp.screenHeight, 10, 10);
         if(counter == 50) {
             counter = 0;
             gp.gameState = gp.playState;
             gp.currentMap = gp.eventHandler.tempMap;
             gp.player.worldX = gp.tileSize * gp.eventHandler.tempCol;
             gp.player.worldY = gp.tileSize * gp.eventHandler.tempRow;
             gp.eventHandler.previousEventX = gp.player.worldX;
             gp.eventHandler.previousEventY = gp.player.worldY;
         }
     }

     public void drawTradeScreen() {
         switch(subState) {
             case 0:
                 tradeSelect();
                 break;
             case 1:
                 tradeBuy();
                 break;
             case 2:
                 tradeSell();
                 break;
         }
         gp.keyH.enterPressed = false;
     }

     public void tradeSelect() {
         drawDialogueScreen();

         // DRAW WINDOW
         int x = gp.tileSize * 15;
         int y = gp.tileSize * 4;
         int width = gp.tileSize * 3;
         int height = (int) (gp.tileSize * 3.5);
         drawSubWindow(x, y, width, height);

         // DRAW TEXTS
         x += gp.tileSize;
         y += gp.tileSize;
         g2.drawString("Buy", x, y);
         if(commandNum == 0) {
             g2.drawString(">", x - 24, y);
             if(gp.keyH.enterPressed) {
                 subState = 1;
             }
         }
         y += gp.tileSize;
         g2.drawString("Sell", x, y);
         if(commandNum == 1) {
             g2.drawString(">", x - 24, y);
             if(gp.keyH.enterPressed) {
                 subState = 2;
             }
         }
         y += gp.tileSize;
         g2.drawString("Leave", x, y);
         if(commandNum == 2) {
             g2.drawString(">", x - 24, y);
             if(gp.keyH.enterPressed) {
                 commandNum = 0;
                 gp.gameState = gp.dialogueState;
                 currentDialogue = "Come agian, hehe!";
             }
         }
     }

     public void tradeBuy() {
         // DRAW PLAYER INVENTORY
         drawInventory(gp.player, false);

         // DRAW NPC INVENTORY
         drawInventory(npc, true);

         // DRAW HINT WINDOW
         int x = gp.tileSize * 2;
         int y = gp.tileSize * 9;
         int width = gp.tileSize * 6;
         int height = gp.tileSize * 2;
         drawSubWindow(x, y, width, height);
         g2.drawString("[ESC] Back", x + 24, y + 60);

         // DRAW PLAYER COIN WINDOW
         x = gp.tileSize * 12;
         y = gp.tileSize * 9;
         width = gp.tileSize * 6;
         height = gp.tileSize * 2;
         drawSubWindow(x, y, width, height);
         g2.drawString("Your Coin: " + gp.player.coin, x + 24, y + 60);

         // DRAW PRICE WINDOW
         int itemIndex = getItemIndexOnSlot(npcSlotCol, npcSlotRow);
         if(itemIndex < npc.inventory.size()) {
             x = (int) (gp.tileSize * 5.5);
             y = (int) (gp.tileSize * 5.5);
             width = (int) (gp.tileSize * 2.5);
             height = gp.tileSize;
             drawSubWindow(x, y, width, height);
             g2.drawImage(coin, x + 10, y + 7, 32, 32, null);

             int price = npc.inventory.get(itemIndex).price;
             String text = String.valueOf(price);
             x = getXForAlignToRightText(text, gp.tileSize * 8 - 20);
             g2.drawString(text, x, y + 32);

             // BUY AN ITEM
             if(gp.keyH.enterPressed) {
                 if(npc.inventory.get(itemIndex).price > gp.player.coin) {
                     subState = 0;
                     gp.gameState = gp.dialogueState;
                     currentDialogue = "You need more coin to buy that!";
                     drawDialogueScreen();
                 } else {
                     if (gp.player.canObtainItem(npc.inventory.get(itemIndex))) {
                         gp.player.coin -= npc.inventory.get(itemIndex).price;
                     } else {
                         subState = 0;
                         gp.gameState = gp.dialogueState;
                         currentDialogue = "You cannot carry any more!";
                     }
                 }
             }
         }
     }

     public void tradeSell() {
         // DRAW PLAYER INVENTORY
         drawInventory(gp.player, true);

         // DRAW HINT WINDOW
         int x = gp.tileSize * 2;
         int y = gp.tileSize * 9;
         int width = gp.tileSize * 6;
         int height = gp.tileSize * 2;
         drawSubWindow(x, y, width, height);
         g2.drawString("[ESC] Back", x + 24, y + 60);

         // DRAW PLAYER COIN WINDOW
         x = gp.tileSize * 12;
         y = gp.tileSize * 9;
         width = gp.tileSize * 6;
         height = gp.tileSize * 2;
         drawSubWindow(x, y, width, height);
         g2.drawString("Your Coin: " + gp.player.coin, x + 24, y + 60);

         // DRAW PRICE WINDOW
         int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
         if(itemIndex < gp.player.inventory.size()) {
             x = (int) (gp.tileSize * 15.5);
             y = (int) (gp.tileSize * 5.5);
             width = (int) (gp.tileSize * 2.5);
             height = gp.tileSize;
             drawSubWindow(x, y, width, height);
             g2.drawImage(coin, x + 10, y + 7, 32, 32, null);

             int price = gp.player.inventory.get(itemIndex).price / 2;
             String text = String.valueOf(price);
             x = getXForAlignToRightText(text, gp.tileSize * 18 - 20);
             g2.drawString(text, x, y + 32);

             // SELL AN ITEM
             if(gp.keyH.enterPressed) {
                 if(gp.player.inventory.get(itemIndex) == gp.player.currentWeapon || gp.player.inventory.get(itemIndex) == gp.player.currentShield) {
                     commandNum = 0;
                     subState = 0;
                     gp.gameState = gp.dialogueState;
                     currentDialogue = "You cannot sell an equipped item!";
                 } else {
                     if(gp.player.inventory.get(itemIndex).amount > 1) {
                         gp.player.inventory.get(itemIndex).amount--;
                     } else {
                         gp.player.inventory.remove(itemIndex);
                     }
                     gp.player.coin += price;
                 }
             }
         }
     }
}
