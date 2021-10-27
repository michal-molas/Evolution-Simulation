# Evolution-Simulation

This program simulates evolution of "worms". 

## Program description

The program takes as input a board and certain parameters, which affect the behaviour of worms.\
It starts with a population of *start_worms* worms. The worms main goal is to eat food which is placed on specific food fields, which are given in the board input.\
When eaten the field needs *food_growth_time* rounds to grow the food back.\
The worms can perform one of 5 actions:
- **eat** (e) - if any neighbouring (including corners) field has a food, then go there and eat the food  
- **go** (g) - move one field forward
- **turn left** (l) - turn 90 degrees to the left
- **turn right** (r) - turn 90 degrees to the right
- **sniff** (s) - if any neighbouring (excluding corners) field has a food, turn towards it

Each round every living worm executes his program which is a list of the instructions from above.\
For example the program may look like this: "egllsrge".\
Each worm in the first round has the same program *start_program* and next generations can obtain new programs through mutatuions.\
After each round every worm has a chance to reproduce (if it's energy is above *reproduction_limit*), creating a new worm.\
The chance of this happening is *reproduction_prob*.\ 
The new worm inherits it's parent's program, which can be mutated.\
There are 3 types of possible mutations:
- **removal of one instruction** with probability *inst_rem_prob*
- **change of one instruction** to an instruction from *inst_list*, with with probability *inst_change_prob*
- **addition of one instruction** from *inst_list* to the end of the program, with with probability *inst_add_prob*

Worms in the first round start with *start_energy* of energy.\
Worms made in reproduction start with *parents_energy_portion* of parent's energy (rounded to an integer),\
while that amount is taken from a parent.\
At the end of each round every worm loses *round_cost* energy.\
Eating food gives a worm *food_gain* energy.\
Each instruction takes 1 energy point to perform.\
If at any point a worm has 0 or less energy it dies and is removed from the board.

The simulation ends after *num_of_rounds* or when all worms are dead.\
Every *print_interval* rounds, the board is printed to the console.

## Input data
Input contains of two files, one with board and one with parameters.\
The board must be rectangular with spaces as empty fields and X's as food fields.\
Examples of boards are in folder *data*.\

Parameters file must contain:
- *start_worms* - positive integer
- *start_energy* - positive integer
- *start_program* - string containing letters e, g, l, r, s
- *food_gain* - positive integer
- *food_growth_time* - positive integer
- *round_cost* - positive integer
- *reproduction_prob* - float number in range [0,1]
- *parents_energy_portion* - float number in range [0,1]
- *reproduction_limit* - positive integer
- *inst_rem_prob* - float number in range [0,1]
- *inst_add_prob* - float number in range [0,1]
- *inst_change_prob* - float number in range [0,1]
- *inst_list* - string containing letters e, g, l, r, s (without repetition)
- *print_interval* - positive integer
- *num_of_rounds* - positive integer

Examples of parameter files are in the *data* folder as well.\
It is quite difficult creating parameters that do not make the worms go extinct quickly, \
so i recommend using those example parameters first before trying to make ones yourself.

## Running the program
The program can be run (on linux) using these command:
- javac evolution/Simulation.java
- java evolution/Simulation data/board[number].txt data/params[number].txt


