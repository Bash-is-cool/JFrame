package entity;

import main.GamePanel;
import main.KeyHandler;
import object.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    int hasKey = 0;
    public boolean attackCanceled = false;
    public boolean lightUpdated = false;

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

        setDefault();
    }

    public void setDefault() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        gp.currentMap = 0;

        deafaultSpeed = 4;
        speed = deafaultSpeed;
        direction = "down";

        // PLAYER STATUS
        level = 1;
        maxLife = 6;
        life = maxLife;
        maxMana = 4;
        mana = maxMana;
        ammo = 10;
        strength = 5;
        dexterity = 1;
        exp = 0;
        nextLevelExp = 5;
        coin = 500;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = null;
        projectile = new OBJ_Fireball(gp); // USE THIS
        attack = getAttack();
        defense = getDefense();

        getImage();
        getAttackImage();
        getGuardImage();
        setItems();
        setDialogue();
    }

    public void setDefaultPositions() {
        gp.currentMap = 0;
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";
    }

    public void setDialogue() {
        dialogues[0][0] = "You are level " + level + " now!\n" + "You feel stronger!";
    }

    public void restoreStatus() {
        life = maxLife;
        mana = maxMana;
        speed = deafaultSpeed;
        invincible = false;
        transparent = false;
        attacking = false;
        guarding = false;
        knockBack = false;
        lightUpdated = true;
    }

    public void setItems() {
        inventory.clear();
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Axe(gp));
    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        motion1Duration = currentWeapon.motion1Duration;
        motion2Duration = currentWeapon.motion2Duration;
        return attack = strength * currentWeapon.attackValue;
    }

    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    public int getCurrentWeaponSlot() {
        int currentWeaponSlot = 0;
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == currentWeapon) {
                currentWeaponSlot = i;
            }
        }
        return currentWeaponSlot;
    }

    public int getCurrentShieldSlot() {
        int currentShieldSlot = 0;
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == currentShield) {
                currentShieldSlot = i;
            }
        }
        return currentShieldSlot;
    }

    public void update() {
        if(knockBack) {
            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);
            gp.cChecker.checkEntity(this, gp.npc);
            gp.cChecker.checkEntity(this, gp.monster);
            gp.cChecker.checkEntity(this, gp.iTile);

            if (collisionOn) {
                knockBackCounter = 0;
                knockBack = false;
                speed = deafaultSpeed;
            } else if (!collisionOn) {
                switch (knockBackDirection) {
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
            knockBackCounter++;
            if (knockBackCounter == 10) {
                knockBackCounter = 0;
                knockBack = false;
                speed = deafaultSpeed;
            }
        } else if (attacking) {
            attacking();
        } else if(keyH.spacePressed) {
            guarding = true;
            guardCounter++;
        } else if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed) {
            if (keyH.upPressed && keyH.leftPressed) {
                direction = "upLeft";
            } else if (keyH.upPressed && keyH.rightPressed) {
                direction = "upRight";
            } else if (keyH.downPressed && keyH.leftPressed) {
                direction = "downLeft";
            } else if (keyH.downPressed && keyH.rightPressed) {
                direction = "downRight";
            } else if (keyH.upPressed) {
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

            // CHECK INTERACTIVE TILE COLLISION
            gp.cChecker.checkEntity(this, gp.iTile);

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
                    case "upLeft":
                        worldX -= (int) (speed / Math.sqrt(2));
                        worldY -= (int) (speed / Math.sqrt(2));
                        break;
                    case "upRight":
                        worldX += (int) (speed / Math.sqrt(2));
                        worldY -= (int) (speed / Math.sqrt(2));
                        break;
                    case "downLeft":
                        worldX -= (int) (speed / Math.sqrt(2));
                        worldY += (int) (speed / Math.sqrt(2));
                        break;
                    case "downRight":
                        worldX += (int) (speed / Math.sqrt(2));
                        worldY += (int) (speed / Math.sqrt(2));
                        break;
                }
            }

            if(keyH.enterPressed && !attackCanceled) {
                gp.playSE(7);
                attacking = true;
                spriteCounter = 0;
            }

            attackCanceled = false;
            gp.keyH.enterPressed = false;
            guarding = false;
            guardCounter = 0;

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
            guarding = false;
            guardCounter = 0;
        }

        if(gp.keyH.shotKeyPressed && !projectile.alive && shotAvailableCounter == 30 && projectile.haveResource(this)) {
            // SET DEFAULT DIRECTION COORDINATES AND USER
            projectile.set(worldX, worldY, direction, true, this);

            // SUBTRACT THE COST (MANA, AMMO, etc.)
            projectile.subtractResource(this);
            
            // CHECK VACANCY
            for(int i = 0; i < gp.projectile[1].length; i++) {
                if(gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = projectile;
                    break;
                }
            }
            
            shotAvailableCounter = 0;
            gp.playSE(11);
        }

        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 60) {
                invincible = false;
                transparent = false;
                invincibleCounter = 0;
            }
        }

        if(shotAvailableCounter < 30) {
            shotAvailableCounter++;
        }

        if(life > maxLife) {
            life = maxLife;
        }

        if(mana > maxMana) {
            mana = maxMana;
        }

        if(!gp.keyH.op) {
            if(life <= 0) {
                gp.ui.commandNum = -1;
                gp.gameState = gp.gameOverState;
                gp.playSE(13);
            }
        } else {
        }
    }

    public void damageProjectile(int i) {
        if(i != 999) {
            Entity projectile = gp.projectile[gp.currentMap][i];
            projectile.alive = false;
            generateParticle(projectile, projectile);
        }
    }

    public void damageInteractiveTile(int i) {
        if(i != 999 && gp.iTile[gp.currentMap][i].destructible && gp.iTile[gp.currentMap][i].isCorrectTool(this) && !gp.iTile[gp.currentMap][i].invincible) {
            gp.iTile[gp.currentMap][i].playSE();
            gp.iTile[gp.currentMap][i].life--;
            gp.iTile[gp.currentMap][i].invincible = true;

            generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]);
            generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]);

            if(gp.iTile[gp.currentMap][i].life == 0) {
                //gp.iTile[gp.currentMap][i].checkDrop();
                gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
            }
        }
    }

    public void pickUpObject(int i) {
        if(i != 999) {
            // PICKUP ONLY ITEMS
            if(gp.obj[gp.currentMap][i].type == typePickupOnly) {
                gp.obj[gp.currentMap][i].use(this);
                gp.obj[gp.currentMap][i] = null;
            }
            // TYPE OBSTACLE 
            else if(gp.obj[gp.currentMap][i].type == typeObstacle) {
                if(keyH.enterPressed) {
                    attackCanceled = true;
                    gp.obj[gp.currentMap][i].interact();
                }
            }

            // INVENTORY ITEMS
            else {
                String text;
                if(canObtainItem(gp.obj[gp.currentMap][i])) {
                    gp.playSE(1);
                    text = "You got a " + gp.obj[gp.currentMap][i].name + "!";
                } else {
                    text = "You cannot carry anymore items.";
                }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null;
            }
        }
    }

    public void dropObject(int index) {
        if(!collisionOn) {
            for(int i = 0; i < gp.obj[1].length; i++) {
                if(gp.obj[i] == null) {
                    gp.obj[gp.currentMap][i] = inventory.remove(index);

                    if(direction.equals("up")) {
                        gp.obj[gp.currentMap][i].worldY = worldY - gp.tileSize;
                    } else if(direction.equals("down")) {
                        gp.obj[gp.currentMap][i].worldY = worldY + gp.tileSize;
                    } else {
                        gp.obj[gp.currentMap][i].worldY = worldY;
                    }

                    if(direction.equals("left") || direction.equals("upLeft") || direction.equals("downLeft")) {
                        gp.obj[gp.currentMap][i].worldX = worldX - gp.tileSize;
                    } else if(direction.equals("right") || direction.equals("upRight") || direction.equals("downRight")) {
                        gp.obj[gp.currentMap][i].worldX = worldX + gp.tileSize;
                    } else {
                        gp.obj[gp.currentMap][i].worldX = worldX;
                    }
                    break;
                }
            }
        }
    }

    public void interactNPC(int i) {
        if(i != 999) {
            if (gp.keyH.enterPressed) {
                attackCanceled = true;
                gp.npc[gp.currentMap][i].speak();
            }
            gp.npc[gp.currentMap][i].move(direction);
        }
    }

    public void contactMonster(int i) {
        if(i != 999) {
            if(!invincible && !gp.monster[gp.currentMap][i].dying) {
                gp.playSE(6);
                int damage = gp.monster[gp.currentMap][i].attack - defense;
                if(damage < 0)
                    damage = 0;
                life -= damage;
                invincible = true;
                transparent = true;
            }
        }
    }

    public void damageMonster(int i, Entity attacker, int attack, int knockBackPower) {
        if(i != 999) {
            if(!gp.monster[gp.currentMap][i].invincible) {
                gp.playSE(5);

                if(knockBackPower > 0) {
                    setKnockBack(gp.monster[gp.currentMap][i], attacker, knockBackPower);
                }

                if(gp.monster[gp.currentMap][i].offBalance) {
                    attack *= 5;
                }

                int damage = attack - gp.monster[gp.currentMap][i].defense;
                if(damage < 0)
                    damage = 0;
                gp.monster[gp.currentMap][i].life -= damage;
                gp.ui.addMessage(damage + " damage!");

                gp.monster[gp.currentMap][i].invincible = true;
                gp.monster[gp.currentMap][i].damageReaction();

                if(gp.monster[gp.currentMap][i].life < 1) {
                    gp.monster[gp.currentMap][i].dying = true;
                    gp.ui.addMessage("Killed the " + gp.monster[gp.currentMap][i].name + "!");
                    gp.ui.addMessage("Exp +" + gp.monster[gp.currentMap][i].exp);
                    exp += gp.monster[gp.currentMap][i].exp;
                    checkLevelUp();
                }
            }
        }
    }

    public void checkLevelUp() {
        if(exp >= nextLevelExp) {
            level++;
            nextLevelExp *= 2;
            maxLife += 2;
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();
            gp.playSE(8);
            gp.gameState = gp.dialogueState;
            life = maxLife;
            setDialogue();
            startDialogue(this, 0);
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);

        if(itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);

            if(selectedItem.type == typeSword || selectedItem.type == typeAxe || selectedItem.type == typePickaxe) {
                currentWeapon = selectedItem;
                attack = getAttack();
                getAttackImage();
            }

            if(selectedItem.type == typeShield) {
                currentShield = selectedItem;
                defense = getDefense();
            }

            if(selectedItem.type == typeLight) {
                if(currentLight == selectedItem) {
                    currentLight = null;
                } else {
                    currentLight = selectedItem;
                }
                lightUpdated = true;
            }

            if(selectedItem.type == typeConsumable) {
                if(selectedItem.use(this)) {
                    if (selectedItem.amount > 1) {
                        selectedItem.amount--;
                    } else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }

    public int searchItemInInventory(String itemName) {
        int itemIndex = 999;

        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i).name.equals(itemName)) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
    }

    public boolean canObtainItem(Entity item) {
        boolean canObtain = false;
        Entity newItem = gp.eGenerator.getObject(item.name);

        if(newItem.stackable) {
            int index = searchItemInInventory(newItem.name);
            if(index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            } else { // New item so need to check vacancy
                if(inventory.size() != maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        } else { // Not stackable so check vacancy
            if(inventory.size() != maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }
        return canObtain;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction) {
            case "up":
                if(!attacking) {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }

                if(attacking) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }

                if(guarding) {
                    image = guardUp;
                }
                break;
            case "down":
                if(!attacking) {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }

                if(attacking) {
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }

                if(guarding) {
                    image = guardDown;
                }
                break;
            case "left":
                if(!attacking) {
                    if (spriteNum == 1) {image = left1;}
                    if (spriteNum == 2) {image = left2;}
                }

                if(attacking) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {image = attackLeft1;}
                    if (spriteNum == 2) {image = attackLeft2;}
                }

                if(guarding) {
                    image = guardLeft;
                }
                break;
            case "right":
                if(!attacking) {
                    if (spriteNum == 1) {image = right1;}
                    if (spriteNum == 2) {image = right2;}
                }

                if(attacking) {
                    if (spriteNum == 1) {image = attackRight1;}
                    if (spriteNum == 2) {image = attackRight2;}
                }

                if(guarding) {
                    image = guardRight;
                }
                break;
            case "upLeft":
                if(!attacking) {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }

                if(attacking) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }

                if(guarding) {
                    image = guardUp;
                }
                break;
            case "downLeft":
                if(!attacking) {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }

                if(attacking) {
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }

                if(guarding) {
                    image = guardDown;
                }
                break;
            case "upRight":
                if(!attacking) {
                    if (spriteNum == 1) {image = up1;}
                    if (spriteNum == 2) {image = up2;}
                }

                if(attacking) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) {image = attackUp1;}
                    if (spriteNum == 2) {image = attackUp2;}
                }

                if(guarding) {
                    image = guardUp;
                }
                break;
            case "downRight":
                if(!attacking) {
                    if (spriteNum == 1) {image = down1;}
                    if (spriteNum == 2) {image = down2;}
                }

                if(attacking) {
                    if (spriteNum == 1) {image = attackDown1;}
                    if (spriteNum == 2) {image = attackDown2;}
                }

                if(guarding) {
                    image = guardDown;
                }
                break;
        }

        if(transparent) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // Reset Alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        if(gp.keyH.hitBox) {
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); //Shows hit box
        }
    }

    public void getImage() {
        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getAttackImage() {
        if(currentWeapon.type == typeSword) {
            attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize * 2, gp.tileSize);
        }

        if(currentWeapon.type == typeAxe) {
            attackUp1 = setup("/player/boy_axe_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_axe_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_axe_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_axe_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_axe_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_axe_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_axe_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_axe_right_2", gp.tileSize * 2, gp.tileSize);
        }

        if(currentWeapon.type == typePickaxe) {
            attackUp1 = setup("/player/boy_pick_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_pick_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_pick_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_pick_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_pick_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_pick_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_pick_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_pick_right_2", gp.tileSize * 2, gp.tileSize);
        }
    }

    public void getSleepingImage(BufferedImage image) {
        up1 = image;
        up2 = image;
        down1 = image;
        down2 = image;
        left1 = image;
        left2 = image;
        right1 = image;
        right2 = image;
    }

    public void getGuardImage() {
        guardUp = setup("/player/boy_guard_up", gp.tileSize, gp.tileSize);
        guardDown = setup("/player/boy_guard_down", gp.tileSize, gp.tileSize);
        guardLeft = setup("/player/boy_guard_left", gp.tileSize, gp.tileSize);
        guardRight = setup("/player/boy_guard_right", gp.tileSize, gp.tileSize);
    }
}
