package main.core;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class MacroState {

    private List<Integer> solutions;

    private int solutionsEvaluation;

    public MacroState(int solutionsEvaluation) {
        this.solutionsEvaluation = solutionsEvaluation;
        solutions = new LinkedList<Integer>();
    }

    public void addSolutionToMacroState(Integer solutionNumber){
        solutions.add(solutionNumber);
    }

    public List<Integer> getSolutions() {
        return solutions;
    }

    public int getSolutionsEvaluation() {
        return solutionsEvaluation;
    }
}
