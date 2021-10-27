package evolution.src;

public class FoodField extends Field {
    private boolean isFood;
    private int timeToGrow;
    
    public FoodField() {
        this.isFood = true;
        this.timeToGrow = 0;
    }
    
    @Override
    public boolean isFood() {
        return isFood;
    }
    
    @Override
    public void giveFood(Parameters parameters) {
        isFood = false;
        timeToGrow = parameters.foodGrowthTime();
    }

    @Override
    public boolean produceFood() {
        if (!isFood && --timeToGrow == 0) {
            isFood = true;
            return true;
        }
        return false;
    }
}
