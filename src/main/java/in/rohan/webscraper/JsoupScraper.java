package in.rohan.webscraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class JsoupScraper {

    public static ArrayList<Post> scrape() {
        ArrayList<Post> result = new ArrayList<>();
        String url = "https://freshershunt.in/";
        Document document = request(url);

        // Below code is null pointer exception safe

        Element body = null;
        if (document != null) {
            body = document.select("div.inside-article").first();
        } else {
            System.out.println("\ndocument element is null !!");
        }
        String postTitle = null;
        if (body != null) {
            postTitle = body.select("h2.entry-title").text();
        } else {
            System.out.println("\nbody element is null !!");
        }
        String outerLink = null;
        if (body != null) {
            outerLink = body.select("h2.entry-title a").attr("href");
        } else {
            System.out.println("\nbody element is null !!");
        }

        Document innerDocument = request(outerLink);
        Elements innerBody = null;
        if (innerDocument != null) {
            innerBody = innerDocument.select("div.entry-content");
        } else {
            System.out.println("\ninnerDocument is null !!");
        }
        Elements applyLink = null;
        if (innerBody != null) {
            applyLink = innerBody.select("p").select(":contains(Apply):contains(Click here)");
        } else {
            System.out.println("\ninnerBody is null !!");
        }
        if (applyLink != null) {
            for (Element link : applyLink.select("a[href]")) {
                String postLink = link.attr("href");
                String postText = link.text();
                result.add(new Post(postTitle, postLink, postText));
            }
        } else {
            System.out.println("\napplyLink is null !!");
        }
        return result;
    }

    public static Document request(String url) {
        try {
            Connection conn = Jsoup.connect(url);
            Document doc = conn.get();

            if (conn.response().statusCode() == 200) {
                return  doc;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\nNot able to Connect to the site !!");
            return null;
        }
    }
}
