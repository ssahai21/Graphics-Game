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
    private int lineIndex = 0;

    public DialougeManager(ImageIcon speakerImage, String speakerName, ArrayList<String> contents, String inventoryBlurb){
        this.speakerName = speakerName;
        this.contents = contents;
        this.inventoryBlurb = inventoryBlurb;
        currLine = "";
        this.speakerImage = speakerImage;
    }
    public void reset(){
        lineIndex = 0;
        currLine = "";
    }

    public boolean advance(){
        if(lineIndex >= contents.size()) return false;
        currLine = contents.get(lineIndex++);
        return true;
    }
}
