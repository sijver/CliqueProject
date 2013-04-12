package main.core;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 */
public class Clique {

    // Variables to represent the instance and the solution
    private int nodesNum;
    private int edgesNum;
    private boolean[][] adjacencyMatrix;
    private Set<Integer> solution = new HashSet<Integer>();
    private NeighbourhoodMatrix neighbourhoodMatrix;
    private List<Set<Integer>> allSolutions;
    private List<SolutionType> solutionTypes;
    private EnumMap<SolutionType, Double> solutionTypeStatistics;
    private List<MacroState> collapsedSearchLandscape;

    public Clique(int nodesNum, int edgesNum, boolean[][] adjacencyMatrix) {
        this.nodesNum = nodesNum;
        this.edgesNum = edgesNum;
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public boolean isClique(List<Integer> vector) {
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

    public int evaluateSolution(Set<Integer> vector) {
        return vector.size();
    }

    public void expandSolution() {
        // Initial list of candidates
        List<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < nodesNum; i++) {
            for (int j : solution) {
                if (adjacencyMatrix[i][j]) {
                    candidates.add(i);
                }
            }
        }

        // Expand current clique
        while (candidates.size() > 0) {
            // Adding random candidate to current solution
            int pos = new Random().nextInt(candidates.size());
            solution.add(candidates.get(pos));
            candidates.remove(pos);

            // Adjusting the candidate set
            List<Integer> newCandidates = new ArrayList<Integer>();
            for (int i : candidates) {
                boolean skip = false;
                for (int j : solution) {
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
        solution.add(new Random().nextInt(nodesNum));
    }

    public List<Set<Integer>> getNeighbours(Set<Integer> vector) {
        // List of all nodes which are not in the solution
        List<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < nodesNum; i++) {
            if (!vector.contains(i)) {
                candidates.add(i);
            }
        }

        List<Integer> vectorList = new LinkedList<Integer>(vector);

        List<Set<Integer>> neighbours = new LinkedList<Set<Integer>>();

        for (int i = 0; i < vectorList.size(); i++) {
            int rememberSolutionNode = vectorList.get(i);
            for (int j = 0; j < candidates.size(); j++) {
                vectorList.set(i, candidates.get(j));
                if (isClique(vectorList)) {
                    neighbours.add(new HashSet<Integer>(vectorList));
                }
            }
            vectorList.set(i, rememberSolutionNode);
        }

        return neighbours;
    }

    public Set<Integer> getSolution() {
        return solution;
    }

    // Returns list of all possible cliques of current graph. Each solution represented like HashSet.
    public void computeAllSolutions() {
        if (allSolutions == null) {
            allSolutions = new LinkedList<Set<Integer>>();
            for (int i = 0; i < nodesNum; i++) {
                allSolutions.add(new HashSet<Integer>(Arrays.asList(new Integer[]{i})));
            }

            for (int j = 0; j < allSolutions.size(); j++) {
                for (int i = 0; i < nodesNum; i++) {
                    Set<Integer> candidateSolution = new HashSet<Integer>((Set<Integer>) allSolutions.toArray()[j]);
                    candidateSolution.add(i);
                    if (!allSolutions.contains(candidateSolution) && isClique(new LinkedList<Integer>(candidateSolution))) {
                        allSolutions.add(candidateSolution);
                    }
                }
            }
        }
    }

    public void computeAllSolutionTypes() {
        if (solutionTypes == null) {
            createNeighbourhoodMatrix();
            solutionTypes = new LinkedList<SolutionType>();
            for (int i = 0; i < allSolutions.size(); i++) {
                int down = 0;
                int up = 0;
                int side = 0;

                Set<Integer> currentSolution = allSolutions.get(i);
                int currentSolutionEvaluation = evaluateSolution(currentSolution);

                List<Integer> currentSolutionNeighbours = neighbourhoodMatrix.getAllNeighboursOfSolution(i);
                for (Integer currentSolutionNeighbour : currentSolutionNeighbours) {
                    int newSolutionEvaluation = evaluateSolution(allSolutions.get(currentSolutionNeighbour));
                    if (newSolutionEvaluation < currentSolutionEvaluation) {
                        down++;
                    } else if (newSolutionEvaluation == currentSolutionEvaluation) {
                        side++;
                    } else if (newSolutionEvaluation > currentSolutionEvaluation) {
                        up++;
                    }
                }

                if (down == 0 && side == 0) {
                    solutionTypes.add(SolutionType.SLMIN);
                } else if (down == 0 && side > 0 && up > 0) {
                    solutionTypes.add(SolutionType.LMIN);
                } else if (down == 0 && up == 0) {
                    solutionTypes.add(SolutionType.IPLAT);
                } else if (down > 0 && side > 0 && up > 0) {
                    solutionTypes.add(SolutionType.LEDGE);
                } else if (down > 0 && side == 0 && up > 0) {
                    solutionTypes.add(SolutionType.SLOPE);
                } else if (down > 0 && side > 0 && up == 0) {
                    solutionTypes.add(SolutionType.LMAX);
                } else if (side == 0 && up == 0) {
                    solutionTypes.add(SolutionType.SLMAX);
                }
            }
        }
    }

    public boolean areNeighbours(Set<Integer> vector1, Set<Integer> vector2) {
        int differences = 0;
        for (int s1 : vector1) {
            if (!vector2.contains(s1)) {
                differences++;
            }
        }
        if (vector2.size() > vector1.size()) {
            differences += vector2.size() - vector1.size();
        }
        return differences < 2;
    }

    public void createNeighbourhoodMatrix() {
        if (neighbourhoodMatrix == null) {
            computeAllSolutions();
            neighbourhoodMatrix = new NeighbourhoodMatrix(allSolutions.size());
            for (int i = 0; i < allSolutions.size(); i++) {
                for (int j = 0; j < i; j++) {
                    if (areNeighbours(allSolutions.get(i), allSolutions.get(j))) {
                        neighbourhoodMatrix.setCell(i, j, true);
                    }
                }
            }
        }
    }

    public NeighbourhoodMatrix getNeighbourhoodMatrix() {
        createNeighbourhoodMatrix();
        return neighbourhoodMatrix;
    }

    public List<Set<Integer>> getAllSolutions() {
        computeAllSolutions();
        return allSolutions;
    }

    public List<SolutionType> getSolutionTypes() {
        computeAllSolutionTypes();
        return solutionTypes;
    }

    public void computeSolutionTypeStatistics() {
        if (solutionTypeStatistics == null) {
            computeAllSolutionTypes();
            solutionTypeStatistics = new EnumMap<SolutionType, Double>(SolutionType.class);
            for (SolutionType solutionType : SolutionType.values()) {
                solutionTypeStatistics.put(solutionType, 0.0);
            }
            for (SolutionType solutionType : solutionTypes) {
                solutionTypeStatistics.put(solutionType, solutionTypeStatistics.get(solutionType) + 1);
            }
            for (SolutionType solutionType : SolutionType.values()) {
                solutionTypeStatistics.put(solutionType, solutionTypeStatistics.get(solutionType) / solutionTypes.size() * 100);
            }
        }
    }

    public EnumMap<SolutionType, Double> getSolutionTypeStatistics() {
        computeSolutionTypeStatistics();
        return solutionTypeStatistics;
    }

    public void collapsePlateaus() {
        if (collapsedSearchLandscape == null) {
            computeAllSolutionTypes();
            collapsedSearchLandscape = new LinkedList<MacroState>();
            boolean[] isCheckedSolution = new boolean[allSolutions.size()];
            for (int i = 0; i < allSolutions.size(); i++) {

                if (!isCheckedSolution[i]) {
                    int currentSolutionEvaluation = evaluateSolution(allSolutions.get(i));
                    MacroState macroState = new MacroState(currentSolutionEvaluation);
                    Set<Integer> currentSolutionNeighbours = new HashSet<Integer>(neighbourhoodMatrix.getAllNeighboursOfSolution(i));
                    currentSolutionNeighbours.add(i);
                    for(int j = 0; j < currentSolutionNeighbours.size(); j++){
                        if(allSolutions.get((Integer)currentSolutionNeighbours.toArray()[j]).size() == currentSolutionEvaluation){
                            currentSolutionNeighbours.addAll(neighbourhoodMatrix.getAllNeighboursOfSolution((Integer)currentSolutionNeighbours.toArray()[j]));
                        }
                    }

                    for (Integer currentSolutionNeighbour : currentSolutionNeighbours) {
                        if (!isCheckedSolution[currentSolutionNeighbour]) {
                            if (currentSolutionEvaluation == evaluateSolution(allSolutions.get(currentSolutionNeighbour))) {
                                macroState.addSolutionToMacroState(currentSolutionNeighbour);
                                isCheckedSolution[currentSolutionNeighbour] = true;
                            }
                        }
                    }
                    isCheckedSolution[i] = true;
                    collapsedSearchLandscape.add(macroState);
                }
            }
        }
    }

    public List<MacroState> getCollapsedSearchLandscape() {
        collapsePlateaus();
        return collapsedSearchLandscape;
    }
}
