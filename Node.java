import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author abe
 */
public class Node {
    
    private byte[] puzzle;
    private int pathCost;
    private int heuristicCost;
    
    public Node(byte[] puzzle) {
        this.puzzle = puzzle;
        this.heuristicCost = Integer.MAX_VALUE;
        this.pathCost = Integer.MAX_VALUE;
    }
    
    public byte[] getPuzzle() {
        return puzzle;
    }
    
    public void setPuzzle(byte[] puzzle) {
        this.puzzle = puzzle;
        this.heuristicCost = Integer.MAX_VALUE;
    }
   
    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }
    
    public int getPathCost() {
        return pathCost;
    }
    
    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }
    
    public int getHeuristicCost() {
        return heuristicCost;
    }
    
    private void swapIndex(byte[] puzzle, byte firstIndex, byte secondIndex) {
        if(firstIndex != secondIndex) {
            puzzle[firstIndex] ^= puzzle[secondIndex];
            puzzle[secondIndex] ^= puzzle[firstIndex];
            puzzle[firstIndex] ^= puzzle[secondIndex];
        }
    }
    
    private Node actionUpChild(Node currentNode, byte zeroIndex){
        byte[] newPuzzle = Arrays.copyOf(currentNode.getPuzzle(), currentNode.getPuzzle().length);
        byte nextZeroIndex = (byte) (zeroIndex - 3);
        swapIndex(newPuzzle, zeroIndex, nextZeroIndex);
        return new Node(newPuzzle);
    }
    
    private Node actionDownChild(Node currentNode, byte zeroIndex){
        byte[] newPuzzle = Arrays.copyOf(currentNode.getPuzzle(), currentNode.getPuzzle().length);
        byte nextZeroIndex = (byte) (zeroIndex + 3);
        swapIndex(newPuzzle, zeroIndex, nextZeroIndex);
        return new Node(newPuzzle);
    }
    
    private Node actionLeftChild(Node currentNode, byte zeroIndex){
        byte[] newPuzzle = Arrays.copyOf(currentNode.getPuzzle(), currentNode.getPuzzle().length);
        byte nextZeroIndex = (byte) (zeroIndex - 1);
        swapIndex(newPuzzle, zeroIndex, nextZeroIndex);
        return new Node(newPuzzle);
    }
    
    private Node actionRightChild(Node currentNode, byte zeroIndex){
        byte[] newPuzzle = Arrays.copyOf(currentNode.getPuzzle(), currentNode.getPuzzle().length);
        byte nextZeroIndex = (byte) (zeroIndex + 1);
        swapIndex(newPuzzle, zeroIndex, nextZeroIndex);
        return new Node(newPuzzle);
    }
    
    public ArrayList<Node> getChildren() {
        byte zeroIndex = getZeroIndex(this.puzzle);
        ArrayList<Node> children = new ArrayList<Node>();
        
        switch(zeroIndex) {
            case 0:
                // move down or right
                children.add(actionDownChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                break;
            case 1:
                // move left, down, or right
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionDownChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                break;
            case 2:
                // move left or down
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionDownChild(this, zeroIndex));
                break;
            case 3:
                // move up, right, or down
                children.add(actionUpChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                children.add(actionDownChild(this, zeroIndex));
                break;
            case 4:
                // move left, right, up, or down
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                children.add(actionUpChild(this, zeroIndex));
                children.add(actionDownChild(this, zeroIndex));
                break;
            case 5:
                // move left, up, or down
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionUpChild(this, zeroIndex));
                children.add(actionDownChild(this, zeroIndex));
                break;
            case 6:
                // move up or right
                children.add(actionUpChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                break;
            case 7:
                // move left, up, or right
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionUpChild(this, zeroIndex));
                children.add(actionRightChild(this, zeroIndex));
                break;
            case 8:
                // move left or up
                children.add(actionLeftChild(this, zeroIndex));
                children.add(actionUpChild(this, zeroIndex));
                break;
            default:
                System.out.println("Incorrect zero index when getting children.");
                break;
        }
        
        return children;      
    }
    
    private byte getZeroIndex(byte[] puzzle) {
        for(byte i = 0; i < puzzle.length; i++) {
            if(puzzle[i] == 0) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        String result = "";
        for(byte b : puzzle) {
            result += (b + " ");
        }
        return result;
    }
}
