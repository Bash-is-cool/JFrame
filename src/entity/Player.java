package entity;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;
import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

public class Player extends Entity {
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    int hasKey = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        attackArea.width = 36;
        attackArea.height = 36;

        setDefault();
        getPlayerImage();
        getPlayerAttack();
    }

    public void setDefault() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
//        worldX = gp.tileSize * 10;
//        worldY = gp.tileSize * 13;

        speed = 4;
        direction = "down";

        // PLAYER STATUS
        maxLife = 6;
        life = maxLife;
    }

    public void update() {
        if (attack) {
            attacking();
        } else if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);
            
            // CHECK NPC COLLISION
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);

            // CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // CHECK EVENT
            gp.eventHandler.checkEvent();

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if(!collisionOn && !keyH.enterPressed) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }

            gp.keyH.enterPressed = false;

            spriteCounter++;
            if (spriteCounter > 10) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        } else {
            standCounter++;

            if(standCounter == 20) {
                spriteNum = 1;
                standCounter = 0;
            }
        }

        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void attacking() {
        spriteCounter++;

        if(spriteCounter < 6) {
            spriteNum = 1;
        }

        if(spriteCounter > 5 && spriteCounter < 26) {
            spriteNum = 2;

            // Save the current woldX, worldY, solidArea
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Adjust player's worldX/Y for the attackArea
            switch (direction) {
                case "up": worldY -= attackArea.height; break;
                case "down": worldY += attackArea.height; break;
                case "left": worldX -= attackArea.width;
                case "right": worldX += attackArea.width;
            }

            // attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            // Check monster collision with updated values
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            damageMonster(monsterIndex);

            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.height = solidAreaHeight;
            solidArea.width = solidAreaWidth;
        }

        if(spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
            attack = false;
        }
    }

    public void pickUpObject(int i) {
        if(i != 999) {
            String objectName = gp.obj[i].name;
            switch(objectName) {
                case "Key":
                    gp.playSE(1);
                    hasKey++;
                    gp.obj[i] = null;
                    System.out.println("Key: " + hasKey);
                    gp.ui.showMessage("You got a Key!");
                    break;
                case "Door":
                    if(hasKey > 0) {
                        gp.playSE(4);
                        gp.obj[i] = null;
                        hasKey--;
                        System.out.println("Key: " + hasKey);
                        gp.ui.showMessage("You opened the door!");
                    } else {
                        gp.ui.showMessage("You need a Key!");
                    }
                    break;
                case "Boots":
                    gp.playSE(3);
                    speed += 2;
                    gp.obj[i] = null;
                    gp.ui.showMessage("Speed Up!");
                    break;
                case "Chest":
                    gp.ui.gameFinished = true;
                    gp.stopMusic();
                    gp.playSE(2);
                    break;
            }
        }
    }

    public void interactNPC(int i) {
        if(gp.keyH.enterPressed) {
            if (i != 999) {
                gp.gameState = gp.dialogueState;
                gp.npc[i].speak();
            } else {
                attack = true;
            }
        }
    }

    public void contactMonster(int i) {
        if(i != 999) {
            if(!invincible) {
                life -= 1;
                invincible = true;
            }
        }
    }

    public void damageMonster(int i) {
        if(i != 999) {
            if(!gp.monster[i].invincible) {
                gp.monster[i].life -= 1;
                gp.monster[i].invincible = true;

                if(gp.monster[i].life < 1) {
                    gp.monster[i] = null;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        //g2.setColor(Color.WHITE);

        //g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction) {
            case "up":
                if(!attack) {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }

                if(attack) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }
                break;
            case "down":
                if(!attack) {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }

                if(attack) {
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }
                break;
            case "left":
                if(!attack) {
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                }

                if(attack) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {image = attackLeft1;}
                    if (spriteNum == 2) {image = attackLeft2;}
                }
                break;
            case "right":
                if(!attack) {
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                }

                if(attack) {
                    if (spriteNum == 1) {image = attackRight1;}
                    if (spriteNum == 2) {image = attackRight2;}
                }
                break;
        }

        if(invincible) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // Reset Alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        if(gp.keyH.hitBox) {
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); //Shows hit box
        }
    }

    public void getPlayerImage() {
        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttack() {
        attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize * 2, gp.tileSize);
    }
}