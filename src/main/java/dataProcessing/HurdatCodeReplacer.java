package dataProcessing;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HurdatCodeReplacer {

    private static final String DIRECTORY = "src/main/resources/storms";

    // Map all NOAA HURDAT codes to numeric codes (you define the numeric codes)
    private static final Map<String, String> codeMap = new HashMap<>();

    static {
        // Single-letter codes (record identifier, status, detail flags)
        codeMap.put("L", "1");  // Landfall (or record identifier, you might differentiate)
        codeMap.put("C", "2");  // Closest approach to coast
        codeMap.put("G", "3");  // Genesis
        codeMap.put("I", "4");  // Intensity peak
        codeMap.put("P", "5");  // Minimum central pressure
        codeMap.put("R", "6");  // Rapid changes detail
        codeMap.put("S", "7");  // Status change
        codeMap.put("T", "8");  // Track detail
        codeMap.put("W", "9");  // Max sustained wind speed

        // Two-letter system status codes (storm intensity/status)
        codeMap.put("TD", "10");  // Tropical depression
        codeMap.put("TS", "11");  // Tropical storm
        codeMap.put("HU", "12");  // Hurricane
        codeMap.put("EX", "13");  // Extratropical cyclone
        codeMap.put("SD", "14");  // Subtropical depression
        codeMap.put("SS", "15");  // Subtropical storm
        codeMap.put("LO", "16");  // Low (not tropical/subtropical/extratropical)
        codeMap.put("WV", "17");  // Tropical wave
        codeMap.put("DB", "18");  // Disturbance
    }

    public static void main(String[] args) throws IOException {
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));

        if (files == null || files.length == 0) {
            System.out.println("No CSV files found in " + DIRECTORY);
            return;
        }

        for (File file : files) {
            replaceCodesInFile(file.toPath());
        }
    }

    private static void replaceCodesInFile(Path path) {
        try {
            System.out.println("Processing: " + path);
            BufferedReader reader = Files.newBufferedReader(path);
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",", -1);

                for (int i = 0; i < cells.length; i++) {
                    String trimmed = cells[i].trim();

                    // Replace empty cell with -999
                    if (trimmed.isEmpty()) {
                        cells[i] = "-999";
                    } else {
                        // Check if trimmed cell matches a code in map (case-sensitive)
                        // If so, replace with mapped numeric string
                        if (codeMap.containsKey(trimmed)) {
                            cells[i] = codeMap.get(trimmed);
                        } else {
                            cells[i] = trimmed;
                        }
                    }
                }
                output.append(String.join(",", cells)).append("\n");
            }

            reader.close();

            // Overwrite the file with replaced content
            Files.write(path, output.toString().getBytes());

        } catch (IOException e) {
            System.err.println("Error processing file: " + path);
            e.printStackTrace();
        }
    }
}
