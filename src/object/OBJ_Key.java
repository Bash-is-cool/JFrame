package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Key extends SuperObject {
    GamePanel gp;

    public OBJ_Key(GamePanel gp) {
        this.gp = gp;

        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        utilityTool.scaledImage(image, gp.tileSize, gp.tileSize);
    }
}
