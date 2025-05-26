package object.tool;

import entity.Entity;
import main.GamePanel;

public class OBJ_Fishing_Rod extends Entity {
    public static final String objName = "Fishing Rod";

    public OBJ_Fishing_Rod(GamePanel gp) {
        super(gp);

        type = typeFishingRod;
        name = objName;
        down1 = setup("/objects/tool/44", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nA simple fishing rod.\nUse near water to fish.";
        price = 25;
        attackValue = 1;
        attackArea.width = 36;
        attackArea.height = 36;

        // Fishing rod has longer reach
        knockBackPower = 10;
        motion1Duration = 40;
        motion2Duration = 85;
    }

    @Override
    public boolean use(Entity entity) {
        int tileNum = getTileInFront(gp.player);

        if (isWaterTile(tileNum)) {
            gp.gameState = gp.fishingState;
            gp.ui.resetFishing();
            return true;
        } else {
            gp.ui.addMessage("Can't fish here!");
            return false;
        }
    }


    private boolean isWaterTile(int tileNum) {
        System.out.println(tileNum);
        return tileNum >= 18 && tileNum <= 31;
    }

    public int getTileInFront(Entity entity) {
        int tileX = 0;
        int tileY = 0;
        
        switch(entity.direction) {
            case "up":
                tileX = entity.getCol();
                tileY = entity.getRow() - 1;
                break;
            case "down":
                tileX = entity.getCol();
                tileY = entity.getRow() + 1;
                break;
            case "left":
                tileX = entity.getCol() - 1;
                tileY = entity.getRow();
                break;
            case "right":
                tileX = entity.getCol() + 1;
                tileY = entity.getRow();
                break;
        }
        
        // Ensure we don't go out of bounds
        if (tileX < 0) tileX = 0;
        if (tileY < 0) tileY = 0;
        if (tileX >= gp.maxWorldCol) tileX = gp.maxWorldCol - 1;
        if (tileY >= gp.maxWorldRow) tileY = gp.maxWorldRow - 1;
        
        return gp.tileM.mapTileNum[gp.currentMap][tileX][tileY];
    }
}