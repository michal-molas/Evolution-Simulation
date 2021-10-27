package evolution.src;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class Board {
    private static final String BLUE;
    private static final String RED;
    private static final String YELLOW;
    private static final String RESET;
    
    static {
        String OS = System.getProperty("os.name");
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            BLUE = "\u001B[34m";
            RED = "\u001B[31m";
            YELLOW = "\u001B[33m";
            RESET = "\u001B[0m";
        }
        else {
            BLUE = "";
            RED = "";
            YELLOW = "";
            RESET = "";
        }
    }
    
    private final Field[][] field;
    private int round;
    private final ArrayList<Worm> worms;
    private int numFoodFields;

    public Board(Field[][] fields, Parameters parameters) {
        this.field = fields;

        this.worms = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < parameters.startWorms(); i++)
            addWorm(new Worm(parameters, r.nextInt(xSize()), r.nextInt(ySize())));
        
        this.numFoodFields = coundFoodFields();

        this.round = 0;
    }
    
    private int coundFoodFields() {
        int cnt = 0;
        for (int x = 0; x < xSize(); x++)
            for (int y = 0; y < ySize(); y++)
                cnt += (field[x][y].isFood() ? 1 : 0);
            
        return cnt;
    }
    
    private int getData(DataType dt, Worm worm) {
        int data = 0;
        switch (dt) {
            case AGE:
                data = worm.age();
                break;
            case ENERGY:
                data = worm.energy();
                break;
            case PROGRAM:
                data = worm.programSize();
                break;
        }
        return data;
    }

    private int maxData(DataType dt) {
        int maxD = 0;
        int data;
        for (Worm worm : worms) {
            data = getData(dt, worm);
            if(data > maxD)
                maxD = data;
        }
        return maxD;
    }

    private float avgData(DataType dt) {
        if(worms.size() == 0)
            return 0.0f;
        float sumD = 0.0f;
        int data;
        for (Worm rob : worms) {
            data = getData(dt, rob);
            sumD += data;
        }
        return sumD / worms.size();
    }

    private int minData(DataType dt) {
        if (worms.size() == 0)
            return 0;

        int minD = worms.get(0).energy();
        int data;
        for (int i = 1; i < worms.size(); i++) {
            data = getData(dt, worms.get(i));
            if (data < minD)
                minD = data;
        }
        return minD;
    }

    public void printState() {
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));
        System.out.print(round + ", ");
        System.out.print("worms: " + numOfWorms() + ", ");
        System.out.print("food: " + numOfFoodFields() + ", ");
        System.out.print("program: " + minData(DataType.PROGRAM) + "/" +
                df.format(avgData(DataType.PROGRAM)) + "/" + maxData(DataType.ENERGY) + ", ");
        System.out.print("energy: " + minData(DataType.ENERGY) + "/" +
                df.format(avgData(DataType.ENERGY)) + "/" + maxData(DataType.ENERGY) + ", ");
        System.out.println("age: " + minData(DataType.AGE) + "/" +
                df.format(avgData(DataType.AGE)) + "/" + maxData(DataType.AGE));
    }

    public int ySize() {
        return field[0].length;
    }

    public int xSize() {
        return field.length;
    }

    public boolean isFood(int x, int y) {
        return field[x][y].isFood();
    }

    private void addWorm(Worm worm) {
        worms.add(worm);
        field[worm.x()][worm.y()].addWorm();
    }
    
    private void removeWorm(Worm worm) {
        worms.remove(worm);
        field[worm.x()][worm.y()].removeWorm();
    }

    // zwraca true, jeśli był jeszcze jakikolwiek ruch
    private boolean moveWorms(Parameters parametry) {
        boolean wasMove = false;
        for (int i = worms.size(); i --> 0;) {
            wasMove = wasMove || worms.get(i).performInst(this, parametry);
            if(worms.get(i).isDead())
                removeWorm(worms.get(i));
        }
        return wasMove;
    }
    
    public void print() {
        // If there is at least one worm on a field, then I print how many of worms are there
        // (if there is more than 9, then I print 'M' (many) for readability).
        // Else if there is food on a field i print 'X'.
        // Else I print ' '.
        System.out.println(BLUE + "Board after " + round + " rounds looks like this: ");
        
        System.out.print('╔');
        for(int x = 0; x < xSize(); x++)
            System.out.print('═');
        
        System.out.println('╗');
        
        for (int y = 0; y < ySize(); y++) {
            System.out.print('║');
            for (int x = 0; x < xSize(); x++) {
                if (field[x][y].numOfWorms() > 9)
                    System.out.print(YELLOW + 'M');
                else if (field[x][y].numOfWorms() > 0)
                    System.out.print(YELLOW + field[x][y].numOfWorms());
                else if (field[x][y].isFood())
                    System.out.print(RED + 'X');
                else
                    System.out.print(' ');
            }
            System.out.println(BLUE + '║');
        }
        
        System.out.print('╚');
        for(int x = 0; x < xSize(); x++)
            System.out.print('═');
        
        System.out.println('╝' + RESET);
    }

    private void reproduceWorms(Parameters parameters) {
        Worm newWorm;
        for (int i = 0; i < worms.size(); i++) {
            newWorm = worms.get(i).reproduce(parameters);
            if (newWorm != null)
                addWorm(newWorm);
        }
    }

    public void removeFoodFromField(Parameters parameters, int x, int y) {
        field[x][y].giveFood(parameters);
        numFoodFields--;
    }

    private void removeDead() {
        for(int i = worms.size(); i --> 0;)
            if(worms.get(i).isDead())
                removeWorm(worms.get(i));
    }
    
    private void endRound(Parameters parameters) {
        for (Worm rob : worms)
            rob.endRound(parameters);
    }
    
    public void removeFromField(int x, int y) {
        field[x][y].removeWorm();
    }

    public void addToField(int x, int y) {
        field[x][y].addWorm();
    }

    private void produceFood() {
        for (int x = 0; x < xSize(); x++)
            for (int y = 0; y < ySize(); y++)
                if (field[x][y].produceFood())
                    numFoodFields++;
    }
    
    public void performRound(Parameters parameters) {
        Collections.shuffle(worms);
        
        while (moveWorms(parameters));
        
        endRound(parameters);
        removeDead();
        reproduceWorms(parameters);
        produceFood();
        
        round++;
    }

    public int round() {
        return round;
    }
    
    public boolean areWormsDead() {
        return worms.size() == 0;
    }
    
    public int numOfWorms() {
        return worms.size();
    }

    public int numOfFoodFields() {
        return numFoodFields;
    }
}
