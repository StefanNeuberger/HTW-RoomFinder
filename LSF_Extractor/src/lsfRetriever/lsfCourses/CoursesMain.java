package lsfRetriever.lsfCourses;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CoursesMain {
    private static final String VORL_VER_ROOT = "https://lsf.htw-berlin.de/qisserver/rds?state=wtree&search=1&trex=step&root120171=19570&P.vx=kurz";
    private static final boolean AUTO_FLUSH = true;
    public static final String LINKS_OUTPUT_FILE = "subjects_links.txt";

    public static void main(String args[]) throws IOException {
        LinkedList<Subject> subjects = Scrapper.getSubjects(VORL_VER_ROOT);
        serializeSubjects(subjects, "subjects.ser");
        writeToFile(subjects, LINKS_OUTPUT_FILE);
        System.out.println(subjects.size());
    }

    private static void serializeSubjects(List<Subject> subjects, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(subjects);
        } catch (IOException e) {

        }
    }

    private static void writeToFile(LinkedList<Subject> subjects, String path) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path), AUTO_FLUSH)) {
            for (Subject subject : subjects) {
                pw.println(subject.url);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
