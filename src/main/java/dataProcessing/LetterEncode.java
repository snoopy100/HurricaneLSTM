package dataProcessing;

//import org.apache.commons.io.IOExceptionList;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class LetterEncode {
    public static final Map<String, Integer> systemCodeMap = Map.ofEntries(
            Map.entry("TD", 1), Map.entry("TS", 2), Map.entry("HU", 3),
            Map.entry("EX", 4), Map.entry("SD", 5), Map.entry("SS", 6),
            Map.entry("LO", 7), Map.entry("WV", 8), Map.entry("DB", 9)
    );

    // For direction letters (e.g., N/S/E/W)
    /* public static final Map<String, Integer> directionMap = Map.ofEntries(
            Map.entry("E", 1)
            Map.entry("W", 2)
            Map.entry("N", 3)
            Map.entry("S", 4)
    ); */

    public static void main(String[] args_and_other_unnecessary_stuff) {
        // change that for working on mac
        File inputDir = new File("src/main/resources/storms");
        System.out.println(System.getProperty("user.dir"));
        System.out.println("dinputDir exists : " + inputDir.exists());
        File[] files = inputDir.listFiles();

        for(File storm: files) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(storm);
            } catch(Exception e) {e.printStackTrace();}
            String stormName = storm.getName();
            String[] lines = readLines(1, storm);
            for (int i = 0; i < lines.length; i++) {
                String[] entries = splitLine(lines[i]);
                for (int e = 0; i < entries.length; e++) {
                    String entry = entries[e];
                    if(entry.contains("N,")) {
                        entry = directionEncode(entry, 0);
                    } else if(entry.contains("S,")) {
                        entry = directionEncode(entry, 180);
                    } else if(entry.contains("E,")) {
                        entry = directionEncode(entry, 90);
                    } else if (entry.contains("W,")) {
                        entry = directionEncode(entry, 270);
                    } else {
                        entry = String.valueOf(systemCodeMap.get(entry.replaceAll(",", ""))) + ",";
                    }
                }// iterated thru entries
                lines[i] = String.join(",", entries);
            } // iterated thru lines
            // iterated through all the lines now
            try {
                writer.write(String.join("\n", lines));
                writer.close();
            } catch(Exception e) {e.printStackTrace();}
        } // iterated thru storms
    }

    private static String directionEncode(String entry, int x) {
        entry = entry.replaceAll("[^A-Za-z,]","");
        entry = String.valueOf(Double.parseDouble(entry) + x);
        return entry;
    }

    private static String[] splitLine(String line) {
        return line.split(",");
    }

    private static String[] readLines(int skipNum, File fileName) {
        List<String> lines = new ArrayList<String>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            System.out.println("something done did mess up bad line 46 readlines() LetterEncode.java");
            e.printStackTrace();
        }
        return lines.subList(skipNum, lines.size()).toArray(new String[lines.size() - skipNum]);
    }
}
