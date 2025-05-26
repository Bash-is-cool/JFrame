package object.tool;

import entity.Entity;
import main.GamePanel;

public class OBJ_Pickaxe extends Entity {
    public static final String objName = "Pickaxe";
    public OBJ_Pickaxe(GamePanel gp) {
        super(gp);

        type = typePickaxe;
        name = objName;
        down1 = setup("/objects/tool/09", gp.tileSize, gp.tileSize);
        attackValue = 2;
        attackArea.width = 30;
        attackArea.height = 30;
        description = "[" + name + "]\nYou will dig it!";
        price = 75;
        knockBackPower = 10;
        motion1Duration = 10;
        motion2Duration = 20;
    }
}
