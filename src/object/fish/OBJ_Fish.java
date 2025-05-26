
package object.fish;

import entity.Entity;
import main.GamePanel;

public class OBJ_Fish extends Entity {

    public float timeDecay;     // How quickly the progress bar decreases
    public int randDirection;   // How often the fish changes direction
    public int rarity;         // 1-100, higher number means rarer
    public int healValue;
    public String water;

    public OBJ_Fish(GamePanel gp) {
        super(gp);
        type = typeConsumable;
        stackable = true;
    }

    @Override
    public boolean use(Entity entity) {
        gp.player.life += healValue;
        gp.ui.addMessage("Health + " + healValue + "!");
        return true;
    }
}