import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author abe
 */
public class EightPuzzle {

    static Scanner keyboard = new Scanner(System.in);
    static final byte[] goalState = {1,2,3,4,5,6,7,8,0};
    static byte[] currentPuzzle = {1,2,3,4,5,6,7,8,0};
    static final byte hamming = 1, manhattan = 2;
    
    private static boolean isSolvable(byte[] puzzle) {
        int inversions = 0;

        for (int i = 0; i < puzzle.length; i++) {
            if (puzzle[i] == 0) {
                continue;
            }

            for (int j = i; j < puzzle.length; j++) {
                if (puzzle[j] != 0 && puzzle[j] < puzzle[i]) {
                    inversions++;
                }
            }
        }
        // amount of inversions determines if solvable or not
        return (inversions % 2) == 0;
    }

    private static void randomizeArray(byte[] puzzle) {
        int nextIndex;
        Random r = new Random();
        for(int i = puzzle.length - 1; i > 0; i--) {
            nextIndex = r.nextInt(i + 1);
            if(nextIndex != i) {
                // swap using xor trick
                puzzle[nextIndex] ^= puzzle[i];
                puzzle[i] ^= puzzle[nextIndex];
                puzzle[nextIndex] ^= puzzle[i];
            }
        }
    }
    
    private static void generateRandomPuzzle() {
        System.out.println("Generating random problem...");
        currentPuzzle[0] = 1;
        currentPuzzle[1] = 2;
        currentPuzzle[2] = 3;
        currentPuzzle[3] = 4;
        currentPuzzle[4] = 5;
        currentPuzzle[5] = 6;
        currentPuzzle[6] = 7;
        currentPuzzle[7] = 8;
        currentPuzzle[8] = 0;
        
        randomizeArray(currentPuzzle);
        
        // check if solvable
        if(isSolvable(currentPuzzle)) {
             System.out.println("New random puzzle");
            for(byte b : currentPuzzle) {
                System.out.print(b + " ");
            }
            System.out.println();
        } else {
            // try again until solvable
            generateRandomPuzzle();
        }
    }
    
    private static void generateRandomPuzzleStealthily() {
        currentPuzzle[0] = 1;
        currentPuzzle[1] = 2;
        currentPuzzle[2] = 3;
        currentPuzzle[3] = 4;
        currentPuzzle[4] = 5;
        currentPuzzle[5] = 6;
        currentPuzzle[6] = 7;
        currentPuzzle[7] = 8;
        currentPuzzle[8] = 0;
        
        randomizeArray(currentPuzzle);
        
        // check if solvable
        if(!isSolvable(currentPuzzle)) {
            // try again until solvable
            generateRandomPuzzleStealthily();
        }
    }
    
    private static void inputPuzzle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input your 8-puzzle in the following format: 1 2 3 4 5 6 7 8 0");
        System.out.print("Enter here: ");
        
        String input = scanner.nextLine();
        Scanner byteGrabber = new Scanner(input);
        
        try {
            for(int i = 0; i < 9; i++) {
                currentPuzzle[i] = byteGrabber.nextByte();
            }
            
            // check if solvable
            if(isSolvable(currentPuzzle)) {
                 System.out.println("Your input puzzle");
                for(byte b : currentPuzzle) {
                    System.out.print(b + " ");
                }
                System.out.println();
            } else {
                // try again until solvable
                System.out.println("Not solvable, please try again");
                inputPuzzle();
            }
        } catch(Exception e) {
            System.out.println("ERROR ENCOUNTERED: " + e.getMessage());
            System.out.println("Please try again");
            inputPuzzle();
        }
    }
    
    private static void solvePuzzle() {
        System.out.println("\nSolving puzzle using hamming heuristic...");
        aStarSearch(currentPuzzle, hamming);
        System.out.println("\nSolving puzzle using manhattan heuristic...");
        aStarSearch(currentPuzzle, manhattan);
    }
    
    private static byte getHammingScore(byte[] puzzle) {
        byte hammingScore = 0;
        for(int i = 0; i < 9; i++) {
            if(puzzle[i] != 0 && puzzle[i] != goalState[i]) {
                hammingScore++;
            }
        }
        return hammingScore;
    }
    
    private static byte getManhattanScore(byte[] puzzle) {
        int score = 0;
        for(int i = 0; i < 9; i++) {
            int currentValue = puzzle[i];
            if(currentValue != 0) {
                int realRow = i / 3;
                int realColumn = i % 3;
                int expectedRow = (currentValue - 1) / 3;
                int expectedColumn = (currentValue - 1) % 3;
                int distance = Math.abs(expectedRow - realRow) + Math.abs(expectedColumn - realColumn);
                score += distance;
            }
        }
        return (byte) score;
    }
    
    private static byte calculateHeuristicCost(byte[] puzzle, byte heuristic) {
        byte cost = -1;
        if(heuristic == 1) {
            cost = getHammingScore(puzzle);
        } else if(heuristic == 2) {
            cost = getManhattanScore(puzzle);
        } else {
            //invalid response
            System.out.println("invalid heuristic given");
        }
        return cost;
    }
    
    private static void printPuzzle(byte[] puzzle) {
        for(int i = 0; i < 9; i++) {
            System.out.print(puzzle[i] + " ");
        }
        System.out.println();
    }
    
    private static double[] aStarDepthAnalysis(byte[] puzzle, byte heuristic, int depth) {
       Node currentPuzzleNode = new Node(puzzle);
       currentPuzzleNode.setPathCost(0);
       Comparator<Node> compareHeuristics = Comparator.<Node>comparingInt(n1->n1.getHeuristicCost()).thenComparingInt(n2->n2.getHeuristicCost());
       PriorityQueue<Node> frontier = new PriorityQueue<>(compareHeuristics);
       Set<String> explored = new HashSet<>();
       
       frontier.add(currentPuzzleNode);
       
       long startTimeOfSearch = System.nanoTime();
       int nodesGenerated = 0;
       double[] returnArray = new double[2];
       
       while(!frontier.isEmpty()) {
           currentPuzzleNode = frontier.poll();
           if(currentPuzzleNode.getPathCost() == depth) {
               long endTimeOfSearch = System.nanoTime();
               returnArray[0] = nodesGenerated;
               returnArray[1] = (endTimeOfSearch - startTimeOfSearch) / 1000000.0;
               return returnArray;
           }
           if(Arrays.equals(currentPuzzleNode.getPuzzle(), goalState)) {
               generateRandomPuzzleStealthily();
               aStarDepthAnalysis(currentPuzzle, heuristic, depth);
           }
           explored.add(currentPuzzleNode.toString());
           
           // loop through all children of current node
           for(Node child : currentPuzzleNode.getChildren()) {
               nodesGenerated++;
               child.setPathCost(currentPuzzleNode.getPathCost() + 1);
               if(!explored.contains(child.toString()) && !frontier.contains(child)) {
                   // calculate and set cost of child and add to frontier
                   child.setHeuristicCost(child.getPathCost() + calculateHeuristicCost(child.getPuzzle(), heuristic));
                   frontier.add(child);
               } else if(frontier.contains(child) && child.getPathCost() > (currentPuzzleNode.getPathCost() + 1)) {
                   frontier.remove(child);
                   frontier.add(child);
               }
           }
           
       }
       return returnArray;
    }
    
    private static void aStarSearch(byte[] puzzle, byte heuristic) {
       Node currentPuzzleNode = new Node(puzzle);
       currentPuzzleNode.setPathCost(0);
       Comparator<Node> compareHeuristics = Comparator.<Node>comparingInt(n1->n1.getHeuristicCost()).thenComparingInt(n2->n2.getHeuristicCost());
       PriorityQueue<Node> frontier = new PriorityQueue<>(compareHeuristics);
       Set<String> explored = new HashSet<>();
       
       frontier.add(currentPuzzleNode);
       
       long startTimeOfSearch = System.nanoTime();
       int nodesGenerated = 0;
       
       while(!frontier.isEmpty()) {
           currentPuzzleNode = frontier.poll();
           printPuzzle(currentPuzzleNode.getPuzzle());
           if(Arrays.equals(currentPuzzleNode.getPuzzle(), goalState)) {
               System.out.println("Goal state found");
               System.out.println("Depth: " + currentPuzzleNode.getPathCost());
               System.out.println("Search cost for heuristic " + heuristic + ": " + nodesGenerated);
               long endTimeOfSearch = System.nanoTime();
               System.out.println("Time taken for solution: " + ((endTimeOfSearch - startTimeOfSearch) / 1000000) + " milliseconds");
               return;
           }
           explored.add(currentPuzzleNode.toString());
           
           // loop through all children of current node
           for(Node child : currentPuzzleNode.getChildren()) {
               nodesGenerated++;
               child.setPathCost(currentPuzzleNode.getPathCost() + 1);
               if(!explored.contains(child.toString()) && !frontier.contains(child)) {
                   // calculate and set cost of child and add to frontier
                   // this is treated as setting the total cost to the child node
                   child.setHeuristicCost(child.getPathCost() + calculateHeuristicCost(child.getPuzzle(), heuristic));
                   frontier.add(child);
               } else if(frontier.contains(child) && child.getPathCost() > (currentPuzzleNode.getPathCost() + 1)) {
                   frontier.remove(child);
                   frontier.add(child);
               }
           }
           
       }
    }
    
    // this method gets data on an average of 85 cases
    // it will be called 12 times to analyze 1020 puzzles in total
    private static void analyze1020Puzzles(int depth) {
        int solutionDepth1 = 0;
        int solutionDepth2 = 0;
        double time1 = 0.0, time2 = 0.0;
        int d2h1;
        int d2h2;
        double[] returnArray1;
        double[] returnArray2;
        for(int i = 0; i < 85; i++) {
            generateRandomPuzzleStealthily();
            returnArray1 = aStarDepthAnalysis(currentPuzzle, hamming, depth);
            returnArray2 = aStarDepthAnalysis(currentPuzzle, manhattan, depth);
            solutionDepth1 += returnArray1[0];
            solutionDepth2 += returnArray2[0];
            time1 += returnArray1[1];
            time2 += returnArray2[1];
        }
        d2h1 = solutionDepth1 / 85;
        d2h2 = solutionDepth2 / 85;
        time1 = time1 / 85.0;
        time2 = time2 / 85.0;
        System.out.format("%4d%16d%16d%20.2f%20.2f%22s\n", depth, d2h1, d2h2, time1, time2, "85");
    }
    
    
    private static void interactiveMenu() {
        System.out.println("\nChoose your input from the menu options.");
        System.out.println("1. Generate a random solvable 8-puzzle problem and solve it.");
        System.out.println("2. Input a specific 8-puzzle configuration.");
        System.out.println("3. Test 1020 random test cases with various solution depths.");
        System.out.println("4. Exit the program.");
        
        System.out.print("Your selection: ");
        int selection = keyboard.nextInt();
        
        switch(selection) {
            case 1:
                generateRandomPuzzle();
                solvePuzzle();
                interactiveMenu();
                break;
            case 2:
                inputPuzzle();
                solvePuzzle();
                interactiveMenu();
                break;
            case 3:
                System.out.format("%38s%32s\n", "", "A* Search Costs (nodes generated)");
                System.out.format("%4s%16s%16s%24s%24s%16s\n", "d", "h1", "h2", "h1 runtime (ms)", "h2 runtime (ms)", "# Cases");
                analyze1020Puzzles(2);
                analyze1020Puzzles(4);
                analyze1020Puzzles(6);
                analyze1020Puzzles(8);
                analyze1020Puzzles(10);
                analyze1020Puzzles(12);
                analyze1020Puzzles(14);
                analyze1020Puzzles(16);
                analyze1020Puzzles(18);
                analyze1020Puzzles(20);
                analyze1020Puzzles(22);
                analyze1020Puzzles(24);
                interactiveMenu();
                break;
            case 4:
                System.out.println("Goodbye.");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid response, please try again.");
                interactiveMenu();
                break;
        }
                
    }
    
    
    public static void main(String[] args) {
        interactiveMenu();
    }
    
}
