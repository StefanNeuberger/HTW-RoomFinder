package lsfRetriever.lsfLectures.utils;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class IOHelper {
    /**
     * Removes duplicates lines from a given file and save them in another file. The new file has the same name
     * appended with "_no_dups"
     */
    public static String removeDuplicates(String path) {
        String output_path = path.split("\\.")[0] + "_no_dups." + path.split("\\.")[1];
        LinkedHashSet<String> set = new LinkedHashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output_path)))) {
            while (br.ready()) {
                set.add(br.readLine());
            }
            set.forEach(pw::println);
        } catch (IOException e) {
        }
        System.out.println("after removing duplicates size = " + set.size());
        return output_path;
    }

    /**
     * Reads all lines from a file
     */
    public static List<String> readLinesFromFile(String path) throws IOException {
        return readLinesFromFile(path, Integer.MAX_VALUE);
    }

    /**
     * Reads up to max lines from a file
     */
    public static List<String> readLinesFromFile(String path, int max) throws IOException {
        LinkedList<String> urlsList = new LinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String url;
        int n = 0;
        while ((url = br.readLine()) != null) {
            urlsList.add(url);
            n++;
            if (n == max) {
                break;
            }
        }
        return urlsList;
    }

}
