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
        int tileX = 0;
        int tileY = 0;

        // Calculate the tile coordinates based on player direction
        switch(gp.player.direction) {
            case "up":
                tileX = (gp.player.worldX) / gp.tileSize;
                tileY = (gp.player.worldY - gp.player.solidArea.y - gp.tileSize) / gp.tileSize;
                break;
            case "down":
                tileX = (gp.player.worldX) / gp.tileSize;
                tileY = (gp.player.worldY + gp.player.solidArea.y + gp.tileSize) / gp.tileSize;
                break;
            case "left":
                tileX = (gp.player.worldX - gp.player.solidArea.x - gp.tileSize) / gp.tileSize;
                tileY = (gp.player.worldY) / gp.tileSize;
                break;
            case "right":
                tileX = (gp.player.worldX + gp.player.solidArea.x + gp.tileSize) / gp.tileSize;
                tileY = (gp.player.worldY) / gp.tileSize;
                break;
        }

        // Get the tile number at calculated position
        int tileNum = gp.tileM.mapTileNum[gp.currentMap][tileX][tileY];

        if (isWaterTile(tileNum, gp.player.direction)) {
            gp.gameState = gp.fishingState;
            gp.ui.resetFishing();
            return true;
        } else {
            gp.ui.addMessage("Can't fish here!");
            return false;
        }
    }


    private boolean isWaterTile(int tileNum, String direction) {
        // Add the tile numbers that represent water in your game
        // For example, if tiles 33 and 34 are water tiles:

//        boolean up = direction.equals("up") || direction.equals("upLeft") || direction.equals("upRight");
//        boolean down = direction.equals("down") || direction.equals("downLeft") || direction.equals("downRight");
//        boolean left = direction.equals("left") || direction.equals("upLeft") || direction.equals("downLeft");
//        boolean right = direction.equals("right") || direction.equals("upRight") || direction.equals("downRight");
//
//        return (up && tileNum >= 19 && tileNum <= 21) || (down && tileNum >= 14 && tileNum <= 16) || (left && (tileNum == 16 || tileNum == 18 || tileNum == 21)) || (right && (tileNum == 14 || tileNum == 17 || tileNum == 19));
        return tileNum >= 14 && tileNum <= 21;
    }
}
