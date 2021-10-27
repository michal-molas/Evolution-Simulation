package evolution.src;

import java.io.File;

public class World {
    private static final String RED;
    private static final String GREEN;
    private static final String RESET;

    static {
        String OS = System.getProperty("os.name");
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            RED = "\u001B[31m";
            GREEN = "\u001B[32m";
            RESET = "\u001B[0m";
        }
        else {
            RED = "";
            GREEN = "";
            RESET = "";
        }
    }
    
    // turns on or off printing simulation state
    private static final boolean PRINT_STATE = false;
    
    private final Board board;
    private Parameters parameters;
    
    public World(File boardFile, File paramFile) {
        Parser parser = new Parser();

        try {
            parameters = parser.parseParameters(paramFile);
        }
        catch (IncorrectParameter e) {
            e.print();
            parameters = null;
        }
        
        Field[][] fields = parser.parseBoard(boardFile);
        if (fields != null && parameters != null)
            board = new Board(fields, parameters);
        else
            board = null;
    }
    
    public boolean isInputCorrect() { return parameters != null && board != null; }

    public void performSimulation() {
        board.print();

        while (board.round() < parameters.numOfRounds()) {
            board.performRound(parameters);
            
            if (PRINT_STATE)
                board.printState();
            
            if (board.areWormsDead()) {
                System.out.println(RED + "End of simulation, all worms are dead: " + board.round() + RESET);
                return;
            }
            else if (board.round() % parameters.printInterval() == 0) {
                board.print();
            }
        }
        
        if (board.round() % parameters.printInterval() != 0)
            board.print();
        
        System.out.println(GREEN + "End of simulation, worms have managed to live for " + parameters.numOfRounds() + " rounds" + RESET);
    }
}
