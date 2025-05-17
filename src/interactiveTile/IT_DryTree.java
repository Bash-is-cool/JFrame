package interactiveTile;

import entity.Entity;
import main.GamePanel;

public class IT_DryTree extends InteractiveTile {
    GamePanel gp;

    public IT_DryTree(GamePanel gp, int col, int row) {
        super(gp, col, row);
        this.gp = gp;
        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        down1 = setup("/interactiveTiles/drytree", gp.tileSize, gp.tileSize);
        destructible = true;
        life = 3;
    }

    public boolean isCorrectTool(Entity entity) {
        boolean isCorrectItem = entity.currentWeapon.type == typeAxe;
        return isCorrectItem;
    }

    public void playSE() {
        gp.playSE(12);
    }

    public InteractiveTile getDestroyedForm() {
        return new IT_Trunk(gp, worldX / gp.tileSize, worldY / gp.tileSize);
    }
}
