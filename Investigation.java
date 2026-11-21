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

    public static String accuse(Suspect suspect){
        if(caseSolved) return "Morgan's already in custody.";
        if(!readyToAccuse()) return "I should finish checking the evidence and talk to everyone again.";
        if(suspect == RoomManager.alex) return "Maya's sign-in sheet puts Alex in the library the whole time.";
        if(suspect != RoomManager.morgan) return "That doesn't fit what happened on the recording.";
        caseSolved = true;
        return "That's enough to bring Morgan in.";
    }
}
