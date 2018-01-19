package lsfRetriever.lsfLectures;

import java.io.IOException;
import java.util.*;

import lsfRetriever.lsfCourses.CoursesMain;
import lsfRetriever.lsfLectures.utils.IOHelper;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LecturesMain {

    public static void main(String[] args) throws IOException {
        //testSmallSet();
        int numberOfParallelThreads = 4;

        getVeranstaltungenParallel(numberOfParallelThreads);
        IOHelper.removeDuplicates(DATA.MERGED_DATA_PATH);
    }

    private static void getVeranstaltungenParallel(int nrOfThreads) throws IOException {
        if (nrOfThreads > 4) {
            throw new RuntimeException("Don't start more than 4 parallel requests to lsf!");
        }
        //first remove the duplicate lecture links and afterwards save them in a list
        String path = IOHelper.removeDuplicates(CoursesMain.LINKS_OUTPUT_FILE);
        List<String> urlList = IOHelper.readLinesFromFile(path);
        System.out.println(urlList.size());

        DATA.deleteFilesIfExist();

        List<Thread> threadList = new LinkedList<>();
        //divide the urls equaly and give each to a different thread
        for (List<String> urlSublist : split(nrOfThreads, urlList)) {
            Thread t = getVeranstaltungInNewThread(urlSublist);
            threadList.add(t);
        }

        //wait for all the threads and indicate when one is ready
        for (Thread thread : threadList) {
            try {
                thread.join();
                System.out.println(thread.getName() + " ready.");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("who dare disturb me?");
            }
        }

        DATA.writeMergedFiles();
    }

    //splits the list in n approximately equal lists
    private static Collection<List<String>> split(int n, List<String> origList) {
        int max = origList.size();
        int step = max / n;
        LinkedList<List<String>> result = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            int start = i * step;
            //if its the last iteration use the size of the list rather than the step
            int end = (i == n - 1) ? origList.size() : (i + 1) * step;
            result.add(origList.subList(start, end));

        }
        return result;

    }

    /**
     * starts a new thread which writes the insert statements for all lectures from the given url list
     */
    private static Thread getVeranstaltungInNewThread(List<String> urlList) throws IOException {
        Thread t = new Thread((() -> getAllVeranstaltungen(urlList)));
        System.out.println("starting thread " + t.getName() + " on " + urlList.size() + " elements.");
        t.start();
        return t;
    }

    /**
     * writes the insert statements for all lectures from the given url list and prints status reports from time to time
     */
    private static void getAllVeranstaltungen(List<String> urlList) {
        int max = urlList.size();
        int i = 0;
        for (String url : urlList) {
            Document doc = connectFailSafer(url);
            DATA v = VeranstaltungScrapper.getVeranstaltungRaw(url, doc);
            try {
                if (v != null) {
                    v.writeInsertTableToFile();
                }
                i++;
                if (i % 50 == 0) {
                    System.out.printf("%s %d/%d urls ready.%n", Thread.currentThread().getName(), i, max);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(url);
            }
        }
    }

    /**
     * tries to connect to a url up to 5 times.
     */
    private static Document connectFailSafer(String url) {
        int tries = 5;
        while (true) {
            try {
                return Jsoup.connect(url).get();
            } catch (HttpStatusException e) {
                //internal server error
                if (e.getStatusCode() == 500) {
                    try {
                        if (tries == 0) {
                            throw new RuntimeException(e + "failed to connect.");
                        }
                        Thread.sleep(100);
                        tries--;

                    } catch (InterruptedException ie) {
                        throw new RuntimeException(ie);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void testSmallSet() throws IOException {
        String mit5zuege = "https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=119033";
        String SE = "https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=119949";
        String rand = "https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=120088";
        String difficultStudiengangField = "https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=118725";
        String url = difficultStudiengangField;

        List<String> urlList = new ArrayList<>();
        urlList.add(difficultStudiengangField);
        urlList.add(SE);
        urlList.add(rand);
        urlList.add(mit5zuege);
        DATA.deleteFilesIfExist();
        getAllVeranstaltungen(urlList);
        DATA.writeMergedFiles();
    }
}
