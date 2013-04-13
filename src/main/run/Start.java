package main.run;

import main.core.Clique;
import main.core.MacroState;
import main.core.SolutionType;
import main.core.utils.CliqueReader;
import main.ui.SolutionFrame;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 */
public class Start {

    public static void main(String[] args) {

        Clique clique;

        if (args.length > 0) {
            clique = CliqueReader.readCliqueInstance(args[0]);
        } else {
            clique = CliqueReader.readCliqueInstance("instances/pa_11_3.clq");
        }

        // Initialise the solution with a single node
        clique.initialiseSolution();

        // Find a local minima
        clique.expandSolution();

        // Print solution
        System.out.print("Solution:");
        for (int i : clique.getSolution()) {
            System.out.print(String.format(" %d", i + 1));
        }
        System.out.println();
        System.out.println(String.format("Quality: %d", clique.functionEvaluation()));

//        System.out.println("Neighbours:");
//        for (Set<Integer> set : clique.getNeighbours(clique.getSolution())) {
//            for (int i : set) {
//                System.out.print((i + 1) + " ");
//            }
//            System.out.println();
//        }

        clique.createNeighbourhoodMatrix();

//        for (Set<Integer> set : clique.getAllSolutions()) {
//            for (int s : set) {
//                System.out.print((s + 1) + " ");
//            }
//            System.out.println();
//        }

//        clique.getNeighbourhoodMatrix().print();

//        for(SolutionType solutionType : clique.getSolutionTypes()){
//            System.out.println(solutionType);
//        }

        EnumMap<SolutionType, Double> solutionTypeStatistics = clique.getSolutionTypeStatistics();
        for(SolutionType solutionType : SolutionType.values()){
            System.out.println(String.format("%1$s %2$.3f%%", solutionType, solutionTypeStatistics.get(solutionType)));
        }

        for(MacroState macroState : clique.getCollapsedSearchLandscape()){
            System.out.println(macroState.getSolutionsEvaluation());
            System.out.println("  " + Arrays.toString(macroState.getSolutions().toArray()));
        }

        SolutionFrame solutionFrame = new SolutionFrame();
        solutionFrame.setPicture(clique.getCollapsedSearchLandscape(), clique.getNeighbourhoodMatrix(), clique.getAllSolutions().size());

    }

}
