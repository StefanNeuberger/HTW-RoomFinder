package lsfRetriever.lsfCourses;

import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    //The identifier for the semester. it apperars that is is build as follows: 1YYYYi where i is 1 for the SoSe and 2 for the WiSe
    private static final String IDENTIFIER = "120171";
    private static final String IDENTIFIER_KEY = IDENTIFIER + "=";


    /**
     * Returns all Pages, which contain Subjects. Those are all pages, which don't contain pages with longer page ids.
     * e.g if the path of the page is "root120162=18868|19618|19326" and it contains no pages with the id of "root120162=18868|19618|19326|*"
     * then it must be a final page which contains subject urls.
     *
     * @param rootUrl the root page from which to look from
     * @return all subjects which can be reached from the root page
     */
    public static LinkedList<String> getPagesContainingSubjects(String rootUrl) throws IOException {
        LinkedList<String> pagesWithSubjects = new LinkedList<String>();
        LinkedList<String> notVisited = new LinkedList<String>();
        notVisited.add(rootUrl);

        while (!notVisited.isEmpty()) {
            String currentPage = notVisited.removeFirst();
            LinkedList<String> childPages = getChildPages(currentPage);
            notVisited.addAll(childPages);
            if (childPages.isEmpty()) {
                pagesWithSubjects.add(currentPage);
            }
        }
        return pagesWithSubjects;
    }

    //returns the urls from pages to which the current one points
    private static LinkedList<String> getChildPages(String urlToSearch) throws IOException {
        LinkedList<String> childPages = new LinkedList<String>();
        // connect to the page, return the HTML and save it in a Document Objekt
        Document d = Jsoup.connect(urlToSearch).get();

        String currentLocationInTree = getCurrentPageID(urlToSearch);
        String nextLocationInTree = currentLocationInTree + "|";
        Elements elementsWithUrl = d.getElementsByAttribute("href");

        for (Element elementWithUrl : elementsWithUrl) {
            String url = elementWithUrl.attr("href");
            if (url.contains(nextLocationInTree)) {
                childPages.add(url);
            }
        }
        return childPages;
    }

    /**
     * Returns the number of the given url.
     * e.g
     * https://lsf.htw-berlin .de/qisserver/rds?state=wtree&search=1&trex=step&root120162
     * =18868|19618|19326&P.vx=kurz will return 19326
     *
     * @param url the url whose key to return
     * @return the number of the url
     */
    private static String getCurrentPageID(String url) {
        String[] tmp = url.split(IDENTIFIER_KEY);
        String s = tmp[tmp.length - 1];
        int max = s.length() - "&P.vx=kurz".length();
        return s.substring(0, max);
    }
}
