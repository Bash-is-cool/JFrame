package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Tent extends Entity {
    GamePanel gp;
    public static final String objName = "Tent";

    public OBJ_Tent(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = typeConsumable;
        name = objName;
        down1 = setup("/objects/tent", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nYou can sleep until\nuntil next morning";
        price = 300;
        stackable = true;
    }

    public boolean use(Entity entity) {
        if(gp.eManager.lighting.dayState == gp.eManager.lighting.night) {
            gp.gameState = gp.sleepState;
            gp.player.getSleepingImage(down1);
            gp.playSE(15);
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            return true;
        } else {
            gp.gameState = gp.dialogueState;
            dialogues[0][0] = "You cannot sleep now.";
            startDialogue(this, 0);
            return false;
        }
    }
}
