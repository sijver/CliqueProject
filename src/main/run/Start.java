package main.run;

import main.core.Clique;
import main.core.SolutionType;
import main.core.utils.CliqueReader;

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
            clique = CliqueReader.readCliqueInstance("instances/pa_12_10.clq");
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

        System.out.println("Neighbours:");
        for (Set<Short> set : clique.getNeighbours(clique.getSolution())) {
            for (short i : set) {
                System.out.print((i + 1) + " ");
            }
            System.out.println();
        }

        clique.createNeighbourhoodMatrix();

        for (Set<Short> set : clique.getAllSolutions()) {
            for (short s : set) {
                System.out.print((s + 1) + " ");
            }
            System.out.println();
        }

        clique.getNeighbourhoodMatrix().print();

        for(SolutionType solutionType : clique.getSolutionTypes()){
            System.out.println(solutionType);
        }

        EnumMap<SolutionType, Double> solutionTypeStatistics = clique.getSolutionTypeStatistics();
        for(SolutionType solutionType : SolutionType.values()){
            System.out.println(String.format("%1$s %2$.3f%%", solutionType, solutionTypeStatistics.get(solutionType)));
        }

    }

}
