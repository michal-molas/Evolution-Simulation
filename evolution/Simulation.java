package evolution;

import evolution.src.World;

import java.io.File;

public class Simulation {
    private static final int ERROR_CODE = 1;
    
    public static void main(String[] args) {
        if (args.length == 2) {
            World world = new World(new File(args[0]), new File(args[1]));

            if (world.isInputCorrect())
                world.performSimulation();
            else
                System.exit(ERROR_CODE);
        }
        else {
            System.out.println("Incorrect number of arguments.");
            System.exit(ERROR_CODE);
        }
    }
}
