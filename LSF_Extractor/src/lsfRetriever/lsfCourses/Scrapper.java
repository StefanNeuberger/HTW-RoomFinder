package lsfRetriever.lsfCourses;

import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scrapper {
    /**
     * Returns the pages of all Subjects, which can be reached from the given root url.
     *
     * @param rootUrl the root url
     * @return a list containing all subjects, which can be reached from the root url
     */
    public static LinkedList<Subject> getSubjects(String rootUrl) throws IOException {
        LinkedList<String> pagesWithSubjects = Crawler.getPagesContainingSubjects(rootUrl);
        return getSubjects(pagesWithSubjects);
    }

    private static LinkedList<Subject> getSubjects(LinkedList<String> pagesWithSubjects) throws IOException {
        LinkedList<Subject> subjects = new LinkedList<Subject>();
        Document d;

        for (String page : pagesWithSubjects) {
            d = Jsoup.connect(page).get();
            // returns elements of the class .ver.  ver probably stands for veranstaltung
            Elements elements = d.select(".ver");

            for (Element element : elements) {
                subjects.add(Subject.convertToSubject(element));
            }
        }
        return subjects;
    }
}