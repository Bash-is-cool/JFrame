package object.fish;

import main.GamePanel;

public class OBJ_Fish_Goldfish extends OBJ_Fish {
    public static final String objName = "Goldfish";

    public OBJ_Fish_Goldfish(GamePanel gp) {
        super(gp);
        name = objName;
        down1 = setup("/objects/fish/fresh/goldfish", gp.tileSize, gp.tileSize);
        water = "fresh";
        healValue = 2;
        description = "[" + name + "]\nA common " + water + "water fish.\nRestores " + healValue + " health.";
        price = 5;
        timeDecay = 0.8f;
        randDirection = 45;
        rarity = 60;
    }
}
