package main.core.utils;

import main.core.Clique;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 */
public class CliqueReader {



    public static Clique readCliqueInstance(String fileName) {
        try {
            int nodesNum = 0;
            int edgesNum = 0;
            boolean[][] adjacencyMatrix = null;

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
            return new Clique(nodesNum, edgesNum, adjacencyMatrix);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: unable to open file");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("ERROR: I/O exception");
            System.exit(1);
        }

        return null;
    }

}
