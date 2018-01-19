package lsfRetriever.lsfLectures;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class VeranstaltungScrapper {
    public static final String[] keysOfInterest = {"Termin", "Hinweise zum Termin", "Dozent", "Dozenten", "Raum "};

    public static DATA getVeranstaltungRaw(String url, Document doc) {
        Elements elements = doc.select(".normal[colspan=5]");
        if (elements.size() == 0) {
            System.out.println("WARNING!!! Could not parse " + url);
            return null;
        }
        String name = elements.get(0).text();
        String sprache = elements.get(1).text();
        String info = elements.get(3).text();

        Element vorlesungSemesterAll = elements.get(4);
        Elements vorlesungSemesterPairs = vorlesungSemesterAll.select("table td");
        String[] vorlesungSemesterPairsArray = new String[vorlesungSemesterPairs.size()];

        for (int i = 0; i < vorlesungSemesterPairs.size(); i++) {
            vorlesungSemesterPairsArray[i] = vorlesungSemesterPairs.get(i).text();
        }

        List<Map<String, String>> gruppen = getZugBloecke(doc);

        return new DATA(url, name, sprache, info, vorlesungSemesterPairsArray, gruppen);

    }

    private static List<Map<String, String>> getZugBloecke(Document doc) {
        // select all elements ascending from an element of the normal class who
        // don't have an attribute style starting with font-size
        Elements elements = doc.select(".normal td:not([style^=font-size])");
        HashMap<String, String> currentZug = null;
        List<Map<String, String>> termine = new LinkedList<>();
        Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()) {
            Element currentElement = iterator.next();

            if (isZugIdentifier(currentElement)) {
                currentZug = new HashMap<String, String>();
                termine.add(currentZug);
                String value = iterator.next().text();
                currentZug.put("Termin", value);
                currentElement = iterator.next();// so we dont do an empty check
                // afterwards
            }
            if (isOfInterest(currentElement)) {
                String key = currentElement.html();
                String value = iterator.next().text();
                currentZug.put(key, value);
            }

        }
        return termine;
    }

    private static boolean isOfInterest(Element e) {
        String eText = e.text();

        for (String key : keysOfInterest) {
            if (eText.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isZugIdentifier(Element e) {
        return e.text().equals("Termin");
    }
}
