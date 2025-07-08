package dataProcessing;

import java.io.*;
import java.util.*;

public class Hurdat2Splitter {

    public static void main(String[] args) throws IOException {
        File input = new File("src/main/resources/hurdat2.txt"); // Replace with your path
        BufferedReader reader = new BufferedReader(new FileReader(input));

        String line;
        int maxTimesteps = 0;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("AL") || line.startsWith("EP")) {
                // Parse header
                String[] headerParts = line.split(",");
                String stormID = headerParts[0].trim();
                String stormName = headerParts[1].trim().replace(" ", "_");
                int numTimesteps = Integer.parseInt(headerParts[2].trim());

                // Create output file
                File stormFile = new File("src/main/resources/storms/" + stormID + "_" + stormName + ".csv");
                stormFile.createNewFile();
                FileWriter writer = new FileWriter(stormFile);
                writer.write(numTimesteps + "\n"); // Add your own column headers

                for (int i = 0; i < numTimesteps; i++) {
                    String dataLine = reader.readLine();
                    writer.write(dataLine + "\n");
                }

                writer.close();

                // Track max timesteps
                if (numTimesteps > maxTimesteps) {
                    maxTimesteps = numTimesteps;
                }
            }
        }

        reader.close();
        System.out.println("Longest storm sequence: " + maxTimesteps + " timesteps.");
    }
}
