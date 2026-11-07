import javax.swing.ImageIcon;
import java.util.*;
import java.awt.*;

public class DialougeManager {
    public ImageIcon speakerImage;
    public String speakerName;
    public static boolean display = true;
    public ArrayList<String> contents;
    public String inventoryBlurb;
    public String currLine;

    private Image tempscaling;

    public DialougeManager(ImageIcon speakerImage, String speakerName, ArrayList<String> contents, String inventoryBlurb){
        this.speakerName = speakerName;
        this.contents = contents;
        this.inventoryBlurb = inventoryBlurb;
        currLine = "";
        tempscaling = speakerImage.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
        this.speakerImage = new ImageIcon(tempscaling);
    }
    
}
