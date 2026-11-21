import java.util.ArrayList;
import java.util.Arrays;

/** Story progression is independent of drawing and can be checked without a window. */
public final class Investigation {
    public static boolean caseSolved;
    private Investigation() {}

    public static void reset(){
        PlayerInventory.heldItem = null;
        PlayerInventory.items.clear();
        PlayerInventory.clues.clear();
        PlayerInventory.showing = false;
        caseSolved = false;
        PuzzleManager.close();
        RoomManager.reset();
    }

    public static DialougeManager question(Suspect suspect){
        DialougeManager dialogue = suspect.question(PlayerInventory.clues);
        if(suspect.questionedWithEvidence && suspect.reward != null && !suspect.rewardGiven){
            PlayerInventory.items.add(suspect.reward);
            suspect.rewardGiven = true;
            dialogue.contents.add("Received: " + suspect.reward.name + ".");
        }
        return dialogue;
    }

    public static boolean examine(Clue clue){
        if(clue.locked){
            if(!PlayerInventory.isHolding(clue.requiredTool)) return false;
            clue.locked = false;
        }
        return true;
    }

    public static boolean has(Clue clue){ return PlayerInventory.clues.contains(clue); }

    public static boolean readyToAccuse(){
        return has(RoomManager.meetingNote) && has(RoomManager.libraryLedger)
            && has(RoomManager.solventReport) && has(RoomManager.accessLog) && has(RoomManager.cameraFootage)
            && RoomManager.alex.questionedWithEvidence && RoomManager.maya.questionedWithEvidence
            && RoomManager.morgan.questionedWithEvidence;
    }

    public static DialougeManager accuse(Suspect suspect){
        String[] lines;
        if(caseSolved) lines = new String[]{"The case is already closed. Morgan is in custody."};
        else if(!readyToAccuse()) lines = new String[]{"We need every lead before making an arrest."};
        else if(suspect != RoomManager.morgan) lines = new String[]{
            "The evidence does not support that accusation.",
            suspect == RoomManager.alex ? "The library register clears Alex at 5:15." : "The camera identifies Morgan, not Maya.",
            "Review the archive and try again."};
        else {
            caseSolved = true;
            lines = new String[]{"Morgan's access log places him at the scene.",
                "The rag links him to the lab; the camera shows the attack.",
                "He killed Mr. Woffinden to hide his equipment theft.",
                "Morgan is in custody. Case closed."};
        }
        return new DialougeManager(GamePanel.loadImage("detective.png"), "Detective",
            new ArrayList<>(Arrays.asList(lines)), "Case review");
    }
}
