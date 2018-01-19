import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String[] args)throws IOException {
//		String monsterMit5Zuege="https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=119033";
		String SE="https://lsf.htw-berlin.de/qisserver/rds?state=wsearchv&search=2&veranstaltung.veranstid=119949";
		Document doc = Jsoup.connect(SE).get();
		//Element e = doc.select("tr:has(.bgbrownblack)").get(0);
		Elements wochTagUsw = doc.select(".bgbrownblack");
		Elements ort= doc.select("td:has(.bggrey:contains(Raum))");
		//ort=doc.select(".bggrey:contains(Raum)");
		System.out.println(ort.size());
		System.out.println(wochTagUsw.size());
		for(int i =0;i< wochTagUsw.size();i++){
			Element current = wochTagUsw.get(i);
			System.out.println(current.after("<span>"));
			if(current.select("span").size() > 0){ //ist ein pruefungstermin
				continue;
			}
			System.out.println(ort.get(i).getElementsByAttributeValueContaining("href", "raum.rgid").select(".nav")+ " "+ wochTagUsw.get(i));
		}
		
	
	}
}
