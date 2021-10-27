package evolution.src;

import java.util.HashMap;

public class Parameters {
    private final HashMap<String, Integer> parametersInt;
    private final HashMap<String, Float> parametersFloat;
    private final HashMap<String, String> parametersString;

    public Parameters() {
        parametersInt = new HashMap<>();
        parametersFloat = new HashMap<>();
        parametersString = new HashMap<>();
    }

    public void addIntParameter(String param, int value) {
        parametersInt.put(param, value);
    }

    public void addFloatParameter(String param, float value) {
        parametersFloat.put(param, value);
    }

    public void addStringParameter(String param, String value) {
        parametersString.put(param, value);
    }

    public int startWorms() {
        return parametersInt.get("start_worms");
    }

    public int startEnergy() {
        return parametersInt.get("start_energy");
    }

    public char[] startProgram() {
        return parametersString.get("start_program").toCharArray();
    }

    public int foodGain() {
        return parametersInt.get("food_gain");
    }

    public int foodGrowthTime() {
        return parametersInt.get("food_growth_time");
    }

    public int roundCost() {
        return parametersInt.get("round_cost");
    }

    public float reproductionProb() {
        return parametersFloat.get("reproduction_prob");
    }

    public float parentsEnergyPortion() {
        return parametersFloat.get("parents_energy_portion");
    }

    public int reproductionLimit() {
        return parametersInt.get("reproduction_limit");
    }

    public float instRemProb() {
        return parametersFloat.get("inst_rem_prob");
    }

    public float instAddProb() {
        return parametersFloat.get("inst_add_prob");
    }

    public float instChangeProb() {
        return parametersFloat.get("inst_change_prob");
    }

    public char[] instList() {
        return parametersString.get("inst_list").toCharArray();
    }

    public int printInterval() {
        return parametersInt.get("print_interval");
    }

    public int numOfRounds() {
        return parametersInt.get("num_of_rounds");
    }
}
