package dev.rafaelcordeiro.logisticsroutingapp.executables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilterJSONLinearFile {

    public static File sourceFile;
    public static File destFile;

    public static void main(String[] args) {
        sourceFile = new File("src/main/resources/sampa.geojson");
        destFile = new File("src/main/resources/sampa-out.geojson");
        try (BufferedReader bf = new BufferedReader(new FileReader(sourceFile))) {
            String line = "";
            Integer i = 0;
            BufferedWriter bw = new BufferedWriter(new FileWriter(destFile));
            while ((line = bf.readLine()) != null) {
//                if (line.toLowerCase().replace(" ", "").contains("\"city\":\"mogidascruzes\"")) {
////                    System.out.println(line);
//                    bw.write(line + "\n");
//                    bw.flush();
//                }
                if (line.contains("\"geometry\":null")) {
                    i++;
                }
            }
            System.out.println(i);
            bw.close();
//            System.out.println("count = " + i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
