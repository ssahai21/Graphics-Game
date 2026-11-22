import javax.swing.ImageIcon;
import java.util.ArrayList;

public class DialougeManager {
    public ImageIcon speakerImage;
    public String speakerName;
    public ArrayList<String> contents;
    public String currLine;

    private int lineIndex = 0;

    public DialougeManager(ImageIcon speakerImage, String speakerName, ArrayList<String> contents) {
        this.speakerName = speakerName;
        this.contents = contents;
        currLine = "";
        this.speakerImage = speakerImage;
    }

    public void reset() {
        lineIndex = 0;
        currLine = "";
    }

    public boolean advance() {
        if (lineIndex >= contents.size()) {
            return false;
        }
        currLine = contents.get(lineIndex++);
        return true;
    }
}
