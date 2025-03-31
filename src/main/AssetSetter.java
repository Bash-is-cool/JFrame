package main;

import entity.NPC_OldMan;
import object.OBJ_Boots;
import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
    }

    public void setNPC() {
        gp.npc[0] = new NPC_OldMan(gp);
        gp.npc[0].worldX = gp.tileSize * 21;
        gp.npc[0].worldY = gp.tileSize * 21;

        gp.obj[0] = new OBJ_Key(gp);
        gp.obj[0].worldX = 23 * gp.tileSize;
        gp.obj[0].worldY = 39 * gp.tileSize;

        gp.obj[1] = new OBJ_Door(gp);
        gp.obj[1].worldX = 25 * gp.tileSize;
        gp.obj[1].worldY = 39 * gp.tileSize;

        gp.obj[2] = new OBJ_Door(gp);
        gp.obj[2].worldX = 21  * gp.tileSize;
        gp.obj[2].worldY = 39 * gp.tileSize;
    }
}
