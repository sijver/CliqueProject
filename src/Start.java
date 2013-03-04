import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 */
public class Start {

    public static void main(String[] args) {

        Clique clique = new Clique();

        if(args.length > 0){
            clique.readInstance(args[0]);
        } else {
            clique.readInstance("instances\\pa_12_10.clq");
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
        for(Set<Short> set : clique.getNeighbours(clique.getSolution())){
            for(short i : set){
                System.out.print((i + 1) + " ");
            }
            System.out.println();
        }

        clique.createNeighbourhoodMatrix();

    }

}
