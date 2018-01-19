package lsfRetriever.lsfCourses;

import org.jsoup.nodes.Element;

import java.io.Serializable;

public class Subject  implements Serializable{
    public final String name;
    public final String url;

    public Subject(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return name + " : " + url;
    }

    public static Subject convertToSubject(Element element) {
        int offset = "Mehr Informationen zu ".length();

        String url = element.attr("href");
        String name = element.attr("title").substring(offset);
        return new Subject(name, url);
    }
}