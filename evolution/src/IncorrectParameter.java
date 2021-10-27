package evolution.src;

public class IncorrectParameter extends Exception {
    private final String param;
    
    public IncorrectParameter(String param) {
        this.param = param;
    }

    public void print() {
        System.err.println("Incorrect parameter: '" + param + "'.");
    }
}
