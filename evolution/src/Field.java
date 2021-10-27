package evolution.src;

public class Field {
    private int numOfWorms;
    
    public Field() {
        this.numOfWorms = 0;
    }
    
    public boolean isFood() {
        return false;
    }

    public void giveFood(Parameters parameters) {}

    public boolean produceFood() {
        return false;
    }
    
    public void addWorm() {
        numOfWorms++;
    }
    
    public void removeWorm() {
        numOfWorms--;
    }
    
    public int numOfWorms() {
        return numOfWorms;
    }
}
