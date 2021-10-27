package evolution.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Parser {
    private static final String[] INT_PARAMETERS =
            new String[]{"print_interval", "food_gain", "food_growth_time", "num_of_rounds", "round_cost",
                    "reproduction_limit", "start_energy", "start_worms"};
    private static final String[] FLOAT_PARAMETERS =
            new String[]{"inst_add_prob", "reproduction_prob", "inst_rem_prob", "inst_change_prob", "parents_energy_portion"};

    private static final String MAX_LIST_OF_INSTR = "eglrs";

    private static final String[] STRING_PARAMETERS = new String[]{"start_program", "inst_list"};

    private Scanner scanner;

    private void printErrorWrongFile(String fileName, String msg) {
        System.err.println("Wrong file '" + fileName + "': " + msg + ".");
    }
    
    private void printErrorNoFile(String fileName) {
        System.err.println("No file: '" + fileName + "'.");
    }

    public Field[][] parseBoard(File file) {
        Field[][] fields;

        // calculating size

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            printErrorNoFile(file.getName());
            return null;
        }

        int sizeY = 1;
        int sizeX;

        String firstLine = scanner.nextLine();
        sizeX = firstLine.length();

        while (scanner.hasNextLine()) {
            sizeY++;
            scanner.nextLine();
        }

        fields = new Field[sizeX][sizeY];

        // creating a board

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            printErrorNoFile(file.getName());
            return null;
        }

        String currLine;

        for (int y = 0; y < sizeY; y++) {
            currLine = scanner.nextLine();
            if (sizeX != currLine.length()) {
                printErrorWrongFile(file.getName(), "error in line " + (y+1));
                return null;
            }
            for (int x = 0; x < sizeX; x++) {
                if (currLine.charAt(x) == 'x')
                    fields[x][y] = new FoodField();
                else
                    fields[x][y] = new Field();
            }    
        }

        return fields;
    }

    private boolean isInParamList(String string, String[] lista) {
        for (String s : lista)
            if (string.equals(s))
                return true;

        return false;
    }

    private boolean isInstruction(char c) {
        return MAX_LIST_OF_INSTR.contains(c + "");
    }

    public Parameters parseParameters(File file) throws IncorrectParameter {
        Parameters parameters = new Parameters();

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            printErrorNoFile(file.getName());
            return null;
        }

        Scanner lineScanner;
        HashSet<String> foundParams = new HashSet<>();

        String parameter;
        String argument;
        int lineNum = 0;
        while (scanner.hasNextLine()) {
            lineScanner = new Scanner(scanner.nextLine());
            lineNum++;
            if (lineScanner.hasNext()) {
                parameter = lineScanner.next();

                if (lineScanner.hasNext()) {
                    argument = lineScanner.next();

                    if (lineScanner.hasNext())
                        throw new IncorrectParameter(parameter);
                }
                else {
                    throw new IncorrectParameter(parameter);
                }
            }
            else {
                printErrorWrongFile(file.getName(), "error in line " + lineNum);
                return null;
            }

            if (isInParamList(parameter, INT_PARAMETERS)) {
                int value;
                try {
                    value = Integer.parseInt(argument);
                }
                catch (NumberFormatException e) {
                    throw new IncorrectParameter(parameter);
                }
                if(value < 0)
                    throw new IncorrectParameter(parameter);
                
                parameters.addIntParameter(parameter, value);
            }
            else if (isInParamList(parameter, FLOAT_PARAMETERS)) {
                float value;
                try {
                    value = Float.parseFloat(argument);
                }
                catch (NumberFormatException e) {
                    throw new IncorrectParameter(parameter);
                }
                if(value < 0.0f || value > 1.0f)
                    throw new IncorrectParameter(parameter);
                
                parameters.addFloatParameter(parameter, value);
            }
            else if (isInParamList(parameter, STRING_PARAMETERS)) {
                boolean isInstList = parameter.equals("inst_list");
                HashSet<Character> instSet = new HashSet<>();
                for (int i = 0; i < argument.length(); i++) {
                    if(!isInstruction(argument.charAt(i)))
                        throw new IncorrectParameter(parameter);

                    if(isInstList) {
                        if(!instSet.contains(argument.charAt(i)))
                            instSet.add(argument.charAt(i));
                        else
                            throw new IncorrectParameter(parameter);
                    }
                }

                parameters.addStringParameter(parameter, argument);
            }
            else {
                throw new IncorrectParameter(parameter); 
            }

            if (!foundParams.contains(parameter)) {
                foundParams.add(parameter);
            } else {
                printErrorWrongFile(file.getName(), "repeated parameter '" + parameter + "'");
                return null;
            }
        }

        String[] parametersList = new String[INT_PARAMETERS.length + FLOAT_PARAMETERS.length + STRING_PARAMETERS.length];
        int position = 0;
        System.arraycopy(INT_PARAMETERS, 0, parametersList, position, INT_PARAMETERS.length);
        position += INT_PARAMETERS.length;
        System.arraycopy(FLOAT_PARAMETERS, 0, parametersList, position, FLOAT_PARAMETERS.length);
        position += FLOAT_PARAMETERS.length;
        System.arraycopy(STRING_PARAMETERS, 0, parametersList, position, STRING_PARAMETERS.length);
        
        for (String param : parametersList) {
            if (!foundParams.contains(param)) {
                printErrorWrongFile(file.getName(), "no parameter '" + param + "'");
                return null;
            }
        }
        
        // checking specific cases
        
        if (parameters.printInterval() == 0)
            throw new IncorrectParameter("print_interval");
        
        if (parameters.foodGrowthTime() == 0)
            throw new IncorrectParameter("food_growth_time");

        return parameters;
    }
}
