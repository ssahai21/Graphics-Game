import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Suspect {
    public static final int WIDTH = 120;
    public static final int HEIGHT = 180;
    public String name;
    public ImageIcon image;
    public Room location;
    public Coordinate position;
    public boolean moving;
    public boolean questioned;
    public boolean questionedWithEvidence;
    public ArrayList<String> dialouge;
    public ArrayList<String> evidenceDialouge;
    public Clue relevantClue;
    public Item reward;
    public boolean rewardGiven;

    public Suspect(String name, ImageIcon image, Room location, Coordinate position,
                   List<String> dialouge, Clue relevantClue, List<String> evidenceDialouge){
        this.name = name;
        this.image = image;
        this.location = location;
        this.position = position;
        this.dialouge = new ArrayList<>(dialouge);
        this.relevantClue = relevantClue;
        this.evidenceDialouge = new ArrayList<>(evidenceDialouge);
    }

    public boolean contains(int x, int y){
        return x >= position.x && x < position.x + WIDTH
            && y >= position.y && y < position.y + HEIGHT;
    }

    public DialougeManager question(List<Clue> evidence){
        boolean hasEvidence = relevantClue != null && evidence.contains(relevantClue);
        questioned = true;
        if(hasEvidence) questionedWithEvidence = true;
        return new DialougeManager(image, name,
            new ArrayList<>(hasEvidence ? evidenceDialouge : dialouge), "");
    }
}
