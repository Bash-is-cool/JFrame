package monster;

import java.util.Random;

import entity.Entity;
import main.GamePanel;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_Mana_Crystal;

public class MON_Bat extends Entity {
    GamePanel gp;

    public MON_Bat(GamePanel gp) {
        super(gp);
        this.gp = gp;
        type = typeMonster;
        name = "Bat";
        deafaultSpeed = 4;
        speed = deafaultSpeed;
        maxLife = 7;
        life = maxLife;
        attack = 7;
        defense = 0;
        exp = 7;

        solidArea.x = 3;
        solidArea.y = 16;
        solidArea.width = 42;
        solidArea.height = 21;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/monster/bat_down_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monsterbat_down_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/bat_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/bat_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/bat_down_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/bat_down_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/bat_down_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/bat_down_2", gp.tileSize, gp.tileSize);
    }

    public void setAction() {
        if(onPath) {
        } else {
            // Get a random direction
            getRandomDirection(10);
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
    }

    public void checkDrop() {
        // CAST A DIE
        int i = new Random().nextInt(100) + 1;

        // SET DROP
        if(i < 50) {
            dropItem(new OBJ_Coin_Bronze(gp));
        }

        if(i >= 50 && i < 75) {
            dropItem(new OBJ_Heart(gp));
        }

        if(i >= 75 && i < 100) {
            dropItem(new OBJ_Mana_Crystal(gp));
        }
    }
}
