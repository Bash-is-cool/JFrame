package object.tool;

import entity.Entity;
import main.GamePanel;

public class OBJ_Sword_Normal extends Entity {
    public static final String objName = "Normal Sword";
    public OBJ_Sword_Normal(GamePanel gp) {
        super(gp);

        type = typeSword;
        name = objName;
        down1 = setup("/objects/tool/02", gp.tileSize, gp.tileSize);
        attackValue = 1;
        attackArea.width = 36;
        attackArea.height = 36;
        description = "[" + name + "]\nOld Sword";
        price = 20;
        knockBackPower = 2;
        motion1Duration = 5;
        motion2Duration = 25;
    }
}
