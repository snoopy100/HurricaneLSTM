package dataProcessing;

//import org.apache.commons.io.IOExceptionList;

import java.io.*;
import java.util.*;

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

    public static void main(String[] args) {
        // change that for working on mac
        // dont forget to change bellow to src/main/whatever
        File inputDir = new File("src/main/resources/storms");
        System.out.println(System.getProperty("user.dir"));
        System.out.println("dinputDir exists : " + inputDir.exists());
        File[] files = inputDir.listFiles();

        for(File storm: files) {
            System.out.println(storm.getName());
            String stormName = storm.getName();
            String[] lines = readLines(1, storm);
            for (int i = 0; i < lines.length; i++) {
                String[] entries = splitLine(lines[i]);
                for (int e = 0; e < entries.length; e++) {
                    String entry = entries[e];
                    if(entry.contains("N")) {
                        entry = directionEncode(entry, 0);
                    } else if(entry.contains("S")) {
                        entry = directionEncode(entry, 180);
                    } else if(entry.contains("E")) {
                        entry = directionEncode(entry, 90);
                    } else if (entry.contains("W")) {
                        entry = directionEncode(entry, 270);
                    } else {
                        entry = String.valueOf(systemCodeMap.get(entry.replaceAll(",", ""))) + ",";
                    }
                }// iterated thru entries
                lines[i] = String.join(",", entries);
            } // iterated thru lines
            // iterated through all the lines now
            FileWriter writer = null;
            try {
                writer = new FileWriter(storm);
            } catch(Exception e) {e.printStackTrace();}
            try {
                writer.write(String.join("\n", lines));
                writer.close();
            } catch(Exception e) {e.printStackTrace();}
        } // iterated thru storms
    }

    public static String directionEncode(String entry, int x) {
        System.out.println("direction encoded");
        entry = entry.replaceAll("[^A-Za-z,]","");
        entry = String.valueOf(Double.parseDouble(entry) + x);
        return entry;
    }

    public static String[] splitLine(String line) {
        return line.split(",");
    }

    public static String[] readLines(int skipNum, File fileName) {
        System.out.println(fileName.getName());
        // return srting[] of all lines in file minus first skipnum
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader(fileName.getAbsolutePath()));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                System.out.println("Line read");
            }
        } catch(Exception e) { e.printStackTrace(); System.out.println("Line readlines() letternencode"); }

        return (lines.subList(skipNum, lines.size())).toArray(new String[0]);
    }
}
