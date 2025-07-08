package dataProcessing;

import org.apache.commons.io.IOExceptionList;

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
    public static final Map<Character, Integer> directionMap = Map.of(
            'N', 1, 'S', 2, 'E', 3, 'W', 4
    );

    public static void main(String[] args_and_other_unnecessary_stuff) {
        File inputDir = new File("src/main/resources/storms/");
        File[] files = inputDir.listFiles();

        for(File storm: files) {
            String stormName = storm.getName();
            String[] lines = readLines(1, storm);


        }
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
        } catch (Exception e) {
            System.out.println("something done did mess up bad line 46 readlines() LetterEncode.java");
            e.printStackTrace();
        }
        return lines.subList(skipNum, lines.size()).toArray(new String[lines.size() - skipNum]);
    }
}
