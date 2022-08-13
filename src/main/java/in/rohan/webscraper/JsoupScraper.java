package in.rohan.webscraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class JsoupScraper {

    public static Post scrape() {
        Post result = null;
        String url = "https://freshershunt.in/";
        Document document = request(url);

        Element body = document.select("div.inside-article").first();
        String postTitle = body.select("h2.entry-title").text();
        String outerLink = body.select("h2.entry-title a").attr("href");

        Document innerDocument = request(outerLink);
        Elements innerBody = innerDocument.select("div.entry-content");
        Elements applyLink = innerBody.select("p").select(":contains(Apply):contains(Click here)");
        for (Element link : applyLink.select("a[href]")) {
            String postLink = link.attr("href");
            String postText = link.text();
//            System.out.println(postTitle + "\n" + postLink + "\n" + postText + "\n---------------\n\n");
            result = new Post(postTitle, postLink, postText);
        }
        return result;
    }

//    public static ArrayList<Post> scrape() {
//        ArrayList<Post> result = new ArrayList<>();
//        String url = "https://freshershunt.in/";
//        Document document = request(url);
//
//        Element body = document.select("div.inside-article").first();
//        String postTitle = body.select("h2.entry-title").text();
//        String outerLink = body.select("h2.entry-title a").attr("href");
//
//        Document innerDocument = request(outerLink);
//        Elements innerBody = innerDocument.select("div.entry-content");
//        Elements applyLink = innerBody.select("p").select(":contains(Apply):contains(Click here)");
//        for (Element link : applyLink.select("a[href]")) {
//            String postLink = link.attr("href");
//            String postText = link.text();
////            System.out.println(postTitle + "\n" + postLink + "\n" + postText + "\n---------------\n\n");
//            result.add(new Post(postTitle, postLink, postText));
//        }
//        return result;
//    }

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
            return null;
        }
    }
}
