import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 */
public class Clique {

    // Variables to represent the instance and the solution
    private int nodesNum;
    private int edgesNum;
    private boolean[][] adjacencyMatrix;
    private Set<Short> solution = new HashSet<Short>();
    private NeighbourhoodMatrix neighbourhoodMatrix;
    private List<Set<Short>> allSolutions;

    public void readInstance(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;

            Pattern intPattern = Pattern.compile("\\d+");
            Matcher matcher;

            // Read file line by line
            while ((strLine = bufferedReader.readLine()) != null) {
                // Skip comments
                if (!strLine.startsWith("c")) {
                    matcher = intPattern.matcher(strLine);
                    // Read graph information
                    if (strLine.startsWith("p")) {
                        // Number of nodes
                        if (matcher.find()) {
                            nodesNum = Integer.parseInt(matcher.group());
                            adjacencyMatrix = new boolean[nodesNum][nodesNum];
                        }
                        // Number of edges
                        if (matcher.find()) {
                            edgesNum = Integer.parseInt(matcher.group());
                        }
                        // Reading edges
                    } else if (strLine.startsWith("e")) {
                        int n1, n2;
                        if (matcher.find()) {
                            n1 = Integer.parseInt(matcher.group()) - 1;
                            if (matcher.find()) {
                                n2 = Integer.parseInt(matcher.group()) - 1;
                                adjacencyMatrix[n1][n2] = true;
                                adjacencyMatrix[n2][n1] = true;
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: unable to open file");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("ERROR: I/O exception");
            System.exit(1);
        }

    }

    public boolean isClique(List<Short> vector){
        for (int i : vector) {
            for (int j : vector) {
                if (i != j && !adjacencyMatrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int functionEvaluation() {
        // Checking if solution is correct
        for (int i : solution) {
            for (int j : solution) {
                if (i != j && !adjacencyMatrix[i][j]) {
                    System.out.print("ERROR: clique is not valid. ");
                    System.out.println(String.format("%d and %d are not connected.", i + 1, j + 1));
                    System.exit(1);
                }
            }
        }
        return solution.size();
    }

    public void expandSolution() {
        // Initial list of candidates
        List<Short> candidates = new ArrayList<Short>();
        for (short i = 0; i < nodesNum; i++) {
            for (short j : solution) {
                if (adjacencyMatrix[i][j]) {
                    candidates.add(i);
                }
            }
        }

        // Expand current clique
        while (candidates.size() > 0) {
            // Adding random candidate to current solution
            short pos = (short)new Random().nextInt(candidates.size());
            solution.add(candidates.get(pos));
            candidates.remove(pos);

            // Adjusting the candidate set
            List<Short> newCandidates = new ArrayList<Short>();
            for (short i : candidates) {
                boolean skip = false;
                for (short j : solution) {
                    if (!adjacencyMatrix[i][j]) {
                        skip = true;
                    }
                }
                if (!skip) {
                    newCandidates.add(i);
                }
            }
            candidates = newCandidates;
        }
    }

    public void initialiseSolution() {
        solution.add((short)new Random().nextInt(nodesNum));
    }

    public List<Set<Short>> getNeighbours(Set<Short> vector){
        // List of all nodes which are not in the solution
        List<Short> candidates = new ArrayList<Short>();
        for(short i = 0; i < nodesNum; i++){
            if(!vector.contains(i)){
                candidates.add(i);
            }
        }

        List<Short> vectorList = new LinkedList<Short>(vector);

        List<Set<Short>> neighbours = new LinkedList<Set<Short>>();

        for(short i = 0; i < vectorList.size(); i++){
            short rememberSolutionNode = vectorList.get(i);
            for(short j = 0; j < candidates.size(); j++){
                vectorList.set(i, candidates.get(j));
                if(isClique(vectorList)){
                    neighbours.add(new HashSet<Short>(vectorList));
                }
            }
            vectorList.set(i, rememberSolutionNode);
        }

        return neighbours;
    }

    public Set<Short> getSolution() {
        return solution;
    }

    // Returns list of all possible cliques of current graph. Each solution represented like HashSet.
    public List<Set<Short>> getAllCliques(){
        List<Set<Short>> listOfSolutions = new LinkedList<Set<Short>>();
        for(short i = 0; i < nodesNum; i++){
            listOfSolutions.add(new HashSet<Short>(Arrays.asList(new Short[]{i})));
        }

        for(int j = 0; j < listOfSolutions.size(); j++){
            for(short i = 0; i < nodesNum; i++){
                Set<Short> candidateSolution = new HashSet<Short>((Set<Short>)listOfSolutions.toArray()[j]);
                candidateSolution.add(i);
                if(!listOfSolutions.contains(candidateSolution) && isClique(new LinkedList<Short>(candidateSolution))){
                    listOfSolutions.add(candidateSolution);
                }
            }
        }
        for(Set<Short> set : listOfSolutions){
            for(short s : set){
                System.out.print((s + 1) + " ");
            }
            System.out.println();
        }
        return listOfSolutions;
    }

    public boolean areNeighbours(Set<Short> vector1, Set<Short> vector2){
        int differences = 0;
        for(short s1 : vector1){
            if(!vector2.contains(s1)){
                differences++;
            }
        }
        if(vector2.size() > vector1.size()){
            differences += vector2.size() - vector1.size();
        }
        return differences < 2;
    }

    public void createNeighbourhoodMatrix(){
        List<Set<Short>> allSolutions = getAllCliques();
        neighbourhoodMatrix = new NeighbourhoodMatrix(allSolutions.size());
        for(int i = 0; i < allSolutions.size(); i++){
            for(int j = 0; j < i; j++){
                if (areNeighbours(allSolutions.get(i), allSolutions.get(j))) {
                    neighbourhoodMatrix.setCell(i, j, true);
                }
            }
        }
        neighbourhoodMatrix.print();
    }

}
