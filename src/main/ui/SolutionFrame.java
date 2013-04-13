package main.ui;

import main.core.MacroState;
import main.core.NeighbourhoodMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class SolutionFrame extends JFrame {

    private List<MacroState> macroStateList;
    private NeighbourhoodMatrix neighbourhoodMatrix;
    private int solutionsNum;

    private HashMap<Integer, Integer> solutionCostNumbers;
    int maxVertical = 0;
    int minVertical = 0;
    int maxHorizontal = 0;
    int betweenVertical = 0;
    int betweenHorizontal = 0;

    List<Solution> solutionPoints = new LinkedList<Solution>();
    List<LineDescription> lines = new LinkedList<LineDescription>();

    public SolutionFrame() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setVisible(true);
        setSize(800, 600);
    }

    public void computePicture() {

        solutionCostNumbers = new HashMap<Integer, Integer>();
        for (MacroState macroState : macroStateList) {
            if (solutionCostNumbers.containsKey(macroState.getSolutionsEvaluation())) {
                solutionCostNumbers.put(macroState.getSolutionsEvaluation(), solutionCostNumbers.get(macroState.getSolutionsEvaluation()) + macroState.getSolutions().size());
            } else {
                solutionCostNumbers.put(macroState.getSolutionsEvaluation(), macroState.getSolutions().size());
            }
        }

        maxVertical = Collections.max(solutionCostNumbers.keySet());
        minVertical = Collections.min(solutionCostNumbers.keySet());
        maxHorizontal = Collections.max(solutionCostNumbers.values());
        betweenVertical = this.getHeight() / (maxVertical - minVertical + 2);
        betweenHorizontal = this.getWidth() / (maxHorizontal + 2);

        solutionPoints = new LinkedList<Solution>();

        for (Map.Entry<Integer, Integer> entry : solutionCostNumbers.entrySet()) {
            int sols = 1;
            for (MacroState macroState : macroStateList) {
                if (macroState.getSolutionsEvaluation() == entry.getKey()) {
                    for (Integer solution : macroState.getSolutions()) {
                        solutionPoints.add(new Solution(solution, sols * betweenHorizontal, getHeight() - entry.getKey() * betweenVertical));
                        sols++;
                    }
                }
            }
        }

        lines = new LinkedList<LineDescription>();

        for (int i = 0; i < solutionsNum; i++) {
            int x1 = 0;
            int y1 = 0;
            for (Solution solution : solutionPoints) {
                if (i == solution.getNumber()) {
                    x1 = solution.getX();
                    y1 = solution.getY();
                }
            }
            for (int j = 0; j < solutionsNum; j++) {
                if (neighbourhoodMatrix.getCell(i, j)) {
                    int x2 = 0;
                    int y2 = 0;
                    for (Solution solution : solutionPoints) {
                        if (j == solution.getNumber()) {
                            x2 = solution.getX();
                            y2 = solution.getY();
                        }
                    }
                    lines.add(new LineDescription(x1, y1, x2, y2));
                }
            }
        }
    }

    public void setPicture(List<MacroState> macroStateList, NeighbourhoodMatrix neighbourhoodMatrix, int nodesNum) {
        this.macroStateList = macroStateList;
        this.neighbourhoodMatrix = neighbourhoodMatrix;
        this.solutionsNum = nodesNum;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (macroStateList != null) {
            computePicture();

            Graphics2D g2 = (Graphics2D) g;
            for (Solution solutionPoint : solutionPoints) {
                g2.draw(new Ellipse2D.Double(solutionPoint.getX(), solutionPoint.getY(), 5, 5));
            }
            for (LineDescription line : lines) {
                g2.draw(new Line2D.Double(line.getX1(), line.getY1(), line.getX2(), line.getY2()));
            }
        }
    }

    class Solution {

        private int number;
        private int x;
        private int y;

        Solution(int number, int x, int y) {
            this.number = number;
            this.x = x;
            this.y = y;
        }

        public int getNumber() {
            return number;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    class LineDescription {
        private int x1;
        private int x2;
        private int y1;
        private int y2;

        LineDescription(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public int getX1() {
            return x1;
        }

        public int getX2() {
            return x2;
        }

        public int getY1() {
            return y1;
        }

        public int getY2() {
            return y2;
        }
    }

}
