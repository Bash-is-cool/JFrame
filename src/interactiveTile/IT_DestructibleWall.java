package interactiveTile;

import entity.Entity;
import main.GamePanel;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_Mana_Crystal;

import java.awt.*;
import java.util.Random;

public class IT_DestructibleWall extends InteractiveTile {
    GamePanel gp;

    public IT_DestructibleWall(GamePanel gp, int col, int row) {
        super(gp, col, row);
        this.gp = gp;
        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        down1 = setup("/interactiveTiles/destructiblewall", gp.tileSize, gp.tileSize);
        destructible = true;
        life = 3;
    }

    public boolean isCorrectTool(Entity entity) {
        return entity.currentWeapon.type == typePickaxe;
    }

    public void playSE() {
        gp.playSE(12);
    }

    public Color getParticleColor() {
        return new Color(65, 65, 65);
    }

    public int getParticleSize() {
        return 6; // 6 pixels
    }

    public int getParticleSpeed() {
        return 1;
    }

    public int getParticleMaxLife() {
        return 20;
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
