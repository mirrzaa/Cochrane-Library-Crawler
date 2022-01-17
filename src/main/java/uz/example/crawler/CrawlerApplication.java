package uz.example.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uz.example.crawler.service.ContentCrawlService;
import uz.example.crawler.service.http.ContentCrawlHttpClient;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner {
    private final ContentCrawlService contentCrawlService;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CrawlerApplication(ContentCrawlService contentCrawlService) {
        this.contentCrawlService = contentCrawlService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

    /**
     * Retrieve sessionId from server in order to
     * set it header cookie of each request send to server.
     *
     * @throws IOException
     */
    private void setCookie() throws IOException {
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            URL url = new URL("https://www.cochranelibrary.com/");
            URLConnection connection = url.openConnection();
            connection.getContent();

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies =
                    cookieJar.getCookies();
            String session = "";
            for (HttpCookie cookie : cookies) {
                session += cookie + ";";
            }
            ContentCrawlHttpClient.sessionId = session;
        } catch (Exception e) {
            log.error("Error caught: {}", e.getMessage());
        }
    }

    /**
     * Reads the input from command line
     * Provides contentCrawlService with topic
     *
     * @param args - array of arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        long startTime = System.currentTimeMillis();
        setCookie();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\t\tEnter topic for finding reviews or enter exit: ");
                String topic = scanner.nextLine();
                if (topic.trim().toLowerCase().equals("exit")) {
                    System.exit(0);
                } else {
                    contentCrawlService.crawlLibraryByTopic(topic);
                }
            }
        } catch (Exception e) {
            log.error("Crawl Error: {}", e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis() - startTime;
            log.info("Process duration: {} seconds", endTime / 1000);
        }
    }
}
