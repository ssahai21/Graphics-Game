# After Hours

A Java school murder mystery with six connected rooms, three suspects, five pieces of evidence, five keys, and a screwdriver. Search rooms, test alibis, and use the case file to identify the killer.

Run these commands from the project folder with a JDK installed:

```sh
javac -d out *.java drawapp/*.java
java -cp out GameManager
```

Use A and D to walk, E near either edge to enter the adjacent room, I to open evidence, and the mouse to collect objects or question suspects. Click to advance dialogue. Click a key or tool in inventory to hold it before using it on a door or object. Click the same item again, or the bottom-right hand square, to put it away. The held item stays selected after use. [L] on the map means a locked room.

In inventory, click a clue to reread it or choose Review Case to check evidence and make an accusation. An incomplete investigation blocks an arrest; a mistaken accusation lets you review and retry. New Case resets the investigation. Menu returns to the title without resetting progress. Progress is kept only while the game is running.

The route runs from Main Hall through Classroom, Library, Science Lab, Records Office, and Security Room. Follow-up conversations can provide keys, so returning to a suspect with new evidence matters. Text scenes on a black background introduce the case and show its resolution after a successful accusation. Use Continue to advance.

Three puzzle containers now hold important rewards: the classroom lockbox contains the meeting note, the library repair chest contains the lab key and screwdriver, and the records safe contains the security key. Click a container to open its puzzle. Use the on-screen buttons; number locks also accept digits, Backspace, and Enter. Close (or Escape) returns to the room while keeping your progress. Collect Contents transfers rewards after solving. Empty containers cannot give rewards again. New Case resets every puzzle.

PuzzleManager.java holds the number-code, ordering, and neighboring-light puzzle rules. PuzzleContainer.java connects a room object to its locked contents. Add future containers to a room's containers list in RoomManager.

The story setup lives in RoomManager.java; Investigation.java manages objectives, tool checks, rewards, and accusations. The flat 2D artwork is described in images/ARTWORK.md.

Run the full progression checks without opening a window:

```sh
javac -d out *.java drawapp/*.java tests/*.java
java -Djava.awt.headless=true -Ddrawingpanel.headless=true -cp out ProgressionTest
java -Djava.awt.headless=true -Ddrawingpanel.headless=true -cp out PuzzleTest
```
