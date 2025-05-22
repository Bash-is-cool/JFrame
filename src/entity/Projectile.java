package entity;

import main.GamePanel;

public class Projectile extends Entity {
    Entity user;

    public Projectile(GamePanel gp) {
        super(gp);
    }

    public void update() {
        if(user == gp.player) {
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            if(monsterIndex != 999) {
                gp.player.damageMonster(monsterIndex, this, attack * (gp.player.level / 2), knockBackPower);
                generateParticle(user.projectile, gp.monster[gp.currentMap][monsterIndex]);
                alive = false;
            }
        }

        if(user != gp.player) {
            boolean contactPlayer = gp.cChecker.checkPlayer(this);
            if(!gp.player.invincible && contactPlayer) {
                damagePlayer(attack);
                generateParticle(user.projectile, user.projectile);
                alive = false;
            }
        }
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

        life--;
        if(life <= 0)
            alive = false;

        spriteCounter++;
        if(spriteCounter > 12) {
            if(spriteNum == 1) {
                spriteNum = 2;
            } else {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void set(int worldX, int worldY, String direction, boolean alive, Entity user) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.alive = alive;
        this.user = user;
        this.life = this.maxLife;
    }

    public boolean haveResource(Entity user) {
        return false;
    }
    public void subtractResource(Entity user) {
    }
}
