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
    private Set<Short> solution = new HashSet<Short>();
    private NeighbourhoodMatrix neighbourhoodMatrix;
    private List<Set<Short>> allSolutions;
    private List<SolutionType> solutionTypes;
    private EnumMap<SolutionType, Double> solutionTypeStatistics;
    private List<MacroState> collapsedSearchLandscape;

    public Clique(int nodesNum, int edgesNum, boolean[][] adjacencyMatrix) {
        this.nodesNum = nodesNum;
        this.edgesNum = edgesNum;
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public boolean isClique(List<Short> vector) {
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

    public int evaluateSolution(Set<Short> vector) {
        return vector.size();
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
            short pos = (short) new Random().nextInt(candidates.size());
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
        solution.add((short) new Random().nextInt(nodesNum));
    }

    public List<Set<Short>> getNeighbours(Set<Short> vector) {
        // List of all nodes which are not in the solution
        List<Short> candidates = new ArrayList<Short>();
        for (short i = 0; i < nodesNum; i++) {
            if (!vector.contains(i)) {
                candidates.add(i);
            }
        }

        List<Short> vectorList = new LinkedList<Short>(vector);

        List<Set<Short>> neighbours = new LinkedList<Set<Short>>();

        for (short i = 0; i < vectorList.size(); i++) {
            short rememberSolutionNode = vectorList.get(i);
            for (short j = 0; j < candidates.size(); j++) {
                vectorList.set(i, candidates.get(j));
                if (isClique(vectorList)) {
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
    public void computeAllSolutions() {
        if (allSolutions == null) {
            allSolutions = new LinkedList<Set<Short>>();
            for (short i = 0; i < nodesNum; i++) {
                allSolutions.add(new HashSet<Short>(Arrays.asList(new Short[]{i})));
            }

            for (int j = 0; j < allSolutions.size(); j++) {
                for (short i = 0; i < nodesNum; i++) {
                    Set<Short> candidateSolution = new HashSet<Short>((Set<Short>) allSolutions.toArray()[j]);
                    candidateSolution.add(i);
                    if (!allSolutions.contains(candidateSolution) && isClique(new LinkedList<Short>(candidateSolution))) {
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
            for (short i = 0; i < allSolutions.size(); i++) {
                short down = 0;
                short up = 0;
                short side = 0;

                Set<Short> currentSolution = allSolutions.get(i);
                int currentSolutionEvaluation = evaluateSolution(currentSolution);

                List<Short> currentSolutionNeighbours = neighbourhoodMatrix.getAllNeighboursOfSolution(i);
                for (Short currentSolutionNeighbour : currentSolutionNeighbours) {
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

    public boolean areNeighbours(Set<Short> vector1, Set<Short> vector2) {
        int differences = 0;
        for (short s1 : vector1) {
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

    public List<Set<Short>> getAllSolutions() {
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
            for (short i = 0; i < allSolutions.size(); i++) {

                if (!isCheckedSolution[i]) {
                    int currentSolutionEvaluation = evaluateSolution(allSolutions.get(i));
                    MacroState macroState = new MacroState(currentSolutionEvaluation);
                    Set<Short> currentSolutionNeighbours = new HashSet<Short>(neighbourhoodMatrix.getAllNeighboursOfSolution(i));
                    currentSolutionNeighbours.add(i);
                    for(int j = 0; j < currentSolutionNeighbours.size(); j++){
                        if(allSolutions.get((Short)currentSolutionNeighbours.toArray()[j]).size() == currentSolutionEvaluation){
                            currentSolutionNeighbours.addAll(neighbourhoodMatrix.getAllNeighboursOfSolution((Short)currentSolutionNeighbours.toArray()[j]));
                        }
                    }

                    for (Short currentSolutionNeighbour : currentSolutionNeighbours) {
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
