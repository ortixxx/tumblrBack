package main;

import org.jsoup.Connection.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

public class Crawler {
    static Vector<String> urls = new Vector<String>(0, 1);
    static Map<String, String> login_page_cookies;
    static String userId = null;
    static String imagePath;
    boolean flagPost = false;
    Document doc, subDoc;
    String site;

    public Crawler(String site){
        if ( userId == null ){
            try {
                userId = logIn();
                imagePath = createImagesDirectory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.site = site;
    }

    public void crawl() throws Exception{
        doc = Jsoup.connect(site)
                .cookies(login_page_cookies)
                .referrer("http://www.google.com")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36")
                .get();

        Elements links = doc.select("a[href]");
        for(Element link: links){
            if( link.attr("abs:href").toLowerCase().contains("/post") ){
                if ( link.attr("abs:href").toLowerCase().contains("/embed") || link.attr("abs:href").toLowerCase().contains("#")){
                    continue;
                }
                flagPost = true;
                processPage(link.attr("abs:href"));
            }
        }

        if (! flagPost) {
            throw new Exception("End of posts");
        }
    }

    private void processPage(String url) {
        if(!urls.contains(url)){
            urls.add(url);
            Interface.getjTextArea().append(url+"\n");
            extractImage(url);
        }
    }

    private void extractImage(String url) {
        try {
            subDoc = Jsoup.connect(url)
                    .cookies(login_page_cookies)
                    .referrer("http://www.google.com")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36")
                    .get();
        } catch (IOException e) {
            Interface.getjTextArea().append("Error without stop in "+url+"\n");
        }

        Elements links = subDoc.select("img[src]");
        for(Element link: links){
            if( ( link.attr("abs:src").contains("66.media") && link.attr("abs:src").contains("tumblr_") ) || ( link.attr("abs:src").contains("64.media") && link.attr("abs:src").contains(".png") )){
                saveImage(link.attr("abs:src"),imagePath + getShortName(link.attr("abs:src")));
            }
        }
    }

    public String getShortName(String longName) {
        int index = longName.lastIndexOf('/');
        return longName.substring(index+1, longName.length());
    }

    public String createImagesDirectory() {
        File dir = new File("images");
        if (! dir.exists() ) {
            dir.mkdir();
        }

        return dir.getAbsolutePath()+"/";
    }

    public void saveImage(String imageUrl, String destinationFile) {
        try {
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);

            byte[] b = new byte[4096];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }

            is.close();
            os.close();
        } catch (Exception e) {
            Interface.getjTextArea().append("Error without stop in "+imageUrl+"\n");
        }
    }

    public static void clearVector() {
        urls.clear();
    }

    private String logIn() throws Exception{
        String login_url = "https://www.tumblr.com/login";
        String bblog_url = "https://www.tumblr.com/services/bblog";
        String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36";
        String email = "leo_spider2@hotmail.com";
        String password = "luis020";

        Response login_page = Jsoup.connect(login_url).method(Method.GET).execute();

        Document login_document = login_page.parse();

        String random_username_suggestions = Jsoup.parse(login_document.select("input[name=random_username_suggestions]").attr("value")).text();
        String form_key = login_document.select("input[name=form_key]").attr("value");

        login_page_cookies = login_page.cookies();

        Jsoup.connect(bblog_url)
                .cookies(login_page_cookies)
                .referrer(login_url)
                .ignoreHttpErrors(true).followRedirects(true)
                .userAgent(user_agent)
                .method(Method.POST)
                .execute();

        Response login_response = Jsoup.connect(login_url)
                .data("determine_email", email)
                .data("user[email]",email)
                .data("user[password]",password)
                .data("tumblelog[name]","")
                .data("user[age]","")
                .data("context","other")
                .data("version","STANDARD")
                .data("follow","")
                .data("http_referer","https://www.tumblr.com/logout")
                .data("form_key", form_key)
                .data("seen_suggestion","0")
                .data("used_suggestion","0")
                .data("used_auto_suggestion","0")
                .data("about_tumblr_slide","")
                .data("random_username_suggestions", random_username_suggestions)
                .cookies(login_page_cookies)
                .userAgent(user_agent)
                .method(Method.POST)
                .execute();

        Document login_reponse_document = login_response.parse();
        login_page_cookies = login_response.cookies();

        return login_reponse_document.select("input[name=t]").attr("value");
    }
}
