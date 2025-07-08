package dataProcessing;

import java.io.*;
import java.util.*;

public class FeatureLabelMaker {
    public static void main(String[] args) throws IOException {
        File stormDir = new File("src/main/resources/storms");
        File trainFeatureDir = new File("src/main/resources/train/features");
        File trainLabelDir = new File("src/main/resources/train/labels");
        File testFeatureDir = new File("src/main/resources/test/features");
        File testLabelDir = new File("src/main/resources/test/labels");

        // Ensure output directories exist
        trainFeatureDir.mkdirs();
        trainLabelDir.mkdirs();
        testFeatureDir.mkdirs();
        testLabelDir.mkdirs();

        File[] stormFiles = stormDir.listFiles();
        if (stormFiles == null || stormFiles.length == 0) {
            System.out.println("No storm files found.");
            return;
        }

        System.out.println("Number of storms: " + stormFiles.length);

        Random rand = new Random();
        int trainCount = 0;
        int testCount = 0;
        int maxLength = 0; // Variable to track the longest time series
        File longestStormFile = null; // To store the longest storm file

        for (File storm : stormFiles) {
            System.out.println("Current storm: " + storm.getName());

            List<String> lines = new ArrayList<>();
            try (BufferedReader stormReader = new BufferedReader(new FileReader(storm))) {
                // Skip header
                stormReader.readLine();
                String line;
                while ((line = stormReader.readLine()) != null) {
                    lines.add(line);
                }
            }

            if (lines.size() < 3) {
                System.out.println("Skipping storm (too few data rows): " + storm.getName());
                continue;
            }

            // Update maxLength and longestStormFile
            if (lines.size() > maxLength) {
                maxLength = lines.size();
                longestStormFile = storm;
            }

            // Randomly assign to train or test
            boolean isTrain = rand.nextDouble() < 0.9;

            // Assign the file to the correct directory (train or test)
            File featureFile = isTrain ? new File(trainFeatureDir, storm.getName()) : new File(testFeatureDir, storm.getName());
            File labelFile = isTrain ? new File(trainLabelDir, storm.getName()) : new File(testLabelDir, storm.getName());

            // Write features and labels
            try (
                    FileWriter featureWriter = new FileWriter(featureFile, true);
                    FileWriter labelWriter = new FileWriter(labelFile, true)
            ) {
                // Write features: all but last
                for (int i = 0; i < lines.size() - 1; i++) {
                    featureWriter.write(lines.get(i) + "\n");
                }

                // Write labels: all but first
                for (int i = 1; i < lines.size(); i++) {
                    labelWriter.write(lines.get(i) + "\n");
                }
            }

            // Update train or test counter
            if (isTrain) {
                trainCount++;
            } else {
                testCount++;
            }
        }

        // Print out the number of storms in train and test sets
        System.out.println("Number of storms in train set: " + trainCount);
        System.out.println("Number of storms in test set: " + testCount);

        // Print out the longest storm file and its length
        if (longestStormFile != null) {
            System.out.println("Longest storm file: " + longestStormFile.getName() + " with " + maxLength + " records.");
        }
    }
}
