package evolution.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Worm {
    private enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT;
    }
    
    private enum Instruction {
        EAT,
        GO,
        LEFT,
        RIGHT,
        SNIFF;
    }
    
    private static final HashMap<Character, Instruction> instMap;
    
    static {
        HashMap<Character, Instruction> temp = new HashMap<>();
        temp.put('j', Instruction.EAT);
        temp.put('i', Instruction.GO);
        temp.put('l', Instruction.LEFT);
        temp.put('p', Instruction.RIGHT);
        temp.put('w', Instruction.SNIFF);
        
        instMap = temp;
    }
    
    private final ArrayList<Instruction> program;
    private int currInst;

    private int energy;
    private Direction dir;
    private int x;
    private int y;

    private int age;

    public Worm(Parameters parameters, int x, int y) {
        this.energy = parameters.startEnergy();

        this.program = new ArrayList<>();
        for (int i = 0; i < parameters.startProgram().length; i++)
            this.program.add(instMap.get(parameters.startProgram()[i]));
        
        this.currInst = 0;
        
        this.x = x;
        this.y = y;

        this.age = 0;

        Random r = new Random();
        this.dir = Direction.values()[r.nextInt(4)];
    }

    private Worm(ArrayList<Instruction> program, int energy, Direction dir, int x, int y) {
        this.program = program;

        this.energy = energy;
        this.dir = dir;

        this.age = 0;

        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() { return y; }

    public int energy() {
        return energy;
    }

    public int age() {
        return age;
    }

    public int programSize() {
        return program.size();
    }

    private boolean shouldReproduce(Parameters parameters) {
        Random r = new Random();
        float los = r.nextFloat();
        return los <= parameters.reproductionProb() && energy >= parameters.reproductionLimit();
    }

    private int betterMod(int a, int b) {
        return (a + b) % b;
    }

    public Worm reproduce(Parameters parameters) {
        if (shouldReproduce(parameters)) {
            int childEnergy = Math.round(energy * parameters.parentsEnergyPortion());
            energy -= childEnergy;
            Direction childsDir = Direction.values()[betterMod(dir.ordinal() + 2, 4)];
            return new Worm(mutatedProgram(parameters), childEnergy, childsDir, x, y);
        }
        return null;
    }

    private ArrayList<Instruction> mutatedProgram(Parameters parameters) {
        ArrayList<Instruction> mutatedPr = new ArrayList<>(program);
        
        Random r = new Random(System.currentTimeMillis());
        if (r.nextFloat() <= parameters.instRemProb() && !mutatedPr.isEmpty())
            mutatedPr.remove(mutatedPr.size() - 1);
        if (r.nextFloat() <= parameters.instAddProb())
            mutatedPr.add(instMap.get(parameters.instList()[r.nextInt(parameters.instList().length)]));
        if (r.nextFloat() <= parameters.instChangeProb() && !mutatedPr.isEmpty())
            mutatedPr.set(r.nextInt(mutatedPr.size()), instMap.get(parameters.instList()[r.nextInt(parameters.instList().length)]));
        
        return mutatedPr;
    }

    // qTurns > -4 means how much quarter turns to do 
    private void turn(int qTurns) {
        assert (qTurns >= -4);
        dir = Direction.values()[betterMod(dir.ordinal() + qTurns, 4)];
    }

    private void consume(Board board, Parameters parameters) {
        energy += parameters.foodGain();
        board.removeFoodFromField(parameters, x, y);
    }

    private void goTo(Board board, Parameters parameters, int targetX, int targetY) {
        board.removeFromField(x, y);
        x = betterMod(targetX, board.xSize());
        y = betterMod(targetY, board.ySize());
        board.addToField(x, y);

        if(board.isFood(x, y))
            consume(board, parameters);
    }

    private void goForward(Board board, Parameters parameters) {
        int[] newCoordinates = {x, y};
        newCoordinates[(dir.ordinal() + 1) % 2] += (dir.ordinal() < 2 ? 1 : -1);
        goTo(board, parameters, newCoordinates[0], newCoordinates[1]);
    }

    private Direction sniff(Board board) {
        if(board.isFood(x, betterMod(y + 1, board.ySize())))
            return Direction.UP;
        if(board.isFood(betterMod(x + 1, board.xSize()), y))
            return Direction.RIGHT;
        if(board.isFood(x, betterMod(y - 1, board.ySize())))
            return Direction.DOWN;
        if(board.isFood(betterMod(x - 1, board.xSize()), y))
            return Direction.LEFT;
        return null;
    }

    private void eat(Board board, Parameters parameters) {
        Direction k = sniff(board);
        if (k != null) {
            dir = k;
            goForward(board, parameters);
        }
        else {
            int rozX = board.xSize();
            int rozY = board.ySize();
            
            if (board.isFood(betterMod(x + 1, rozX), betterMod(y + 1, rozY))) {
                goTo(board, parameters, x + 1, y + 1);
                dir = Direction.UP;
            }
            else if (board.isFood(betterMod(x + 1, rozX), betterMod(y - 1, rozY))) {
                goTo(board, parameters, x + 1, y - 1);
                dir = Direction.RIGHT;
            }
            else if (board.isFood(betterMod(x - 1, rozX), betterMod(y - 1, rozY))) {
                goTo(board, parameters, x - 1, y - 1);
                dir = Direction.DOWN;
            }
            else if (board.isFood(betterMod(x - 1, rozX), betterMod(y + 1, rozY))) {
                goTo(board, parameters, x - 1, y + 1);
                dir = Direction.LEFT;
            }
        }
    }

    public boolean isDead() {
        return energy < 0;
    }
    
    public boolean performInst(Board board, Parameters parameters) {
        if (currInst < program.size() && energy-- > 0) {
            switch (program.get(currInst++)) {
                case LEFT:
                    turn(-1);
                    break;
                case RIGHT:
                    turn(1);
                    break;
                case GO:
                    goForward(board, parameters);
                    break;
                case SNIFF:
                    Direction k = sniff(board);
                    if (k != null)
                        dir = k;
                    break;
                case EAT:
                    eat(board, parameters);
                    break;
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void endRound(Parameters parameters) {
        energy -= parameters.roundCost();
        currInst = 0;
        age++;
    }
}
