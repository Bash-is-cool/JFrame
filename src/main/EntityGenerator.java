package main;

import entity.Entity;
import object.*;
import object.fish.*;
import object.tool.*;

public class EntityGenerator {
    GamePanel gp;
    public EntityGenerator(GamePanel gp) {
        this.gp = gp;
    }

    public Entity getObject(String name) {

        return switch (name) {
            case OBJ_Axe.objName -> new OBJ_Axe(gp);
            case OBJ_Boots.objName -> new OBJ_Boots(gp);
            case OBJ_Chest.objName -> new OBJ_Chest(gp);
            case OBJ_Coin_Bronze.objName -> new OBJ_Coin_Bronze(gp);
            case OBJ_Door.objName -> new OBJ_Door(gp);
            case OBJ_Door_Iron.objName -> new OBJ_Door_Iron(gp);
            case OBJ_Fireball.objName -> new OBJ_Fireball(gp);
            case OBJ_Heart.objName -> new OBJ_Heart(gp);
            case OBJ_Key.objName -> new OBJ_Key(gp);
            case OBJ_Lantern.objName -> new OBJ_Lantern(gp);
            case OBJ_Mana_Crystal.objName -> new OBJ_Mana_Crystal(gp);
            case OBJ_Pickaxe.objName -> new OBJ_Pickaxe(gp);
            case OBJ_Potion_Red.objName -> new OBJ_Potion_Red(gp);
            case OBJ_Rock.objName -> new OBJ_Rock(gp);
            case OBJ_Shield_Blue.objName -> new OBJ_Shield_Blue(gp);
            case OBJ_Shield_Wood.objName -> new OBJ_Shield_Wood(gp);
            case OBJ_Sword_Normal.objName -> new OBJ_Sword_Normal(gp);
            case OBJ_Tent.objName -> new OBJ_Tent(gp);
            case OBJ_Fishing_Rod.objName -> new OBJ_Fishing_Rod(gp);
            case OBJ_Fish_Angelfish.objName -> new OBJ_Fish_Angelfish(gp);
            case OBJ_Fish_Bass.objName -> new OBJ_Fish_Bass(gp);
            case OBJ_Fish_Catfish.objName -> new OBJ_Fish_Catfish(gp);
            case OBJ_Fish_Goldfish.objName -> new OBJ_Fish_Goldfish(gp);
            case OBJ_Fish_RainbowTrout.objName -> new OBJ_Fish_RainbowTrout(gp);
                default -> null;
        };
    }
}
