package uz.example.crawler.service.impl;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.example.crawler.service.ContentCrawlService;
import uz.example.crawler.service.http.ContentCrawlHttpClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ContentCrawlServiceImpl implements ContentCrawlService {
    private final ContentCrawlHttpClient httpClient;
    private static final String URL = "https://www.cochranelibrary.com/en/search";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ContentCrawlServiceImpl(ContentCrawlHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Sends request to remote server for specific topic.
     * Receives the ClosableHttpResponse from server and
     * extracts the responseBody using InputStream.
     *
     * @param topic - The term to be searched in library
     */
    @Override
    public void crawlLibraryByTopic(String topic) {
        log.info("Crawl page...");
        try (CloseableHttpResponse response = httpClient.sendRequest(URL, topic, false, "0")) {
            HttpEntity entity = response.getEntity();
            InputStream responseBody = entity.getContent();
            String html = readStreamData(responseBody);
            int totalReviews = parseHtml(html, topic);
            crawlLibraryPages(totalReviews, topic);
        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
    }

    /**
     * Visits each page for specified topic.
     *
     * @param totalReviews - Total number of reviews
     * @param topic        - The term to be searched in library
     * @throws IOException -
     */
    private void crawlLibraryPages(int totalReviews, String topic) throws IOException {
        log.info("Crawl pages...");
        int pageSize = 25;
        if (totalReviews >= pageSize) {
            int iterations = totalReviews / pageSize;
            for (int i = 0; i < iterations; i++) {
                CloseableHttpResponse r = httpClient.sendRequest(URL, topic, true, String.valueOf(i + 2));
                HttpEntity e = r.getEntity();
                InputStream in = e.getContent();
                String html = readStreamData(in);
                parseHtml(html, topic);
            }
        }
    }

    /**
     * Reads HttpEntity using InputStream
     * and returns the String data.
     *
     * @param inputStream - Reading stream data from response
     * @return String     - Returns html data extracted from stream
     */
    private String readStreamData(InputStream inputStream) {
        log.info("Read chunked response data...");
        if (inputStream == null)
            return "";

        StringBuilder sb;
        String htmlData = null;
        try (inputStream; BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            htmlData = sb.toString();
        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
        }
        return htmlData;
    }

    /**
     * Parses the html data to extract necessary meta data
     * such as : link, title, author, date.
     * Provides above data to writeFile() method to write them to file.
     *
     * @param documentString - HTML data to be parsed
     * @param topic          - The topic
     * @return int           - Total number of reviews related to topic
     * @throws IOException
     */
    private int parseHtml(String documentString, String topic) throws IOException {
        log.info("Parsing html document...");
        if (documentString == null || documentString.isEmpty())
            return 0;

        Document document = Jsoup.parse(documentString);
        Elements blocks = document.select("div[class=search-results-item-body]");
        Elements totalCount = document.select("span[class=results-number]");
        String counts = totalCount.toString().replaceAll("\\<.*?\\>", "");

        for (int i = 0; i < blocks.size(); i++) {
            Element element = blocks.get(i);
            Elements title = element.select("h3[class=result-title]");
            Elements author = element.select("div[class=search-result-authors]");
            Elements date = element.select("div[class=search-result-date]");
            Elements link = title.select("a");

            String reviewLink = link.attr("href");
            String t = title.toString().replaceAll("\\<.*?\\>", "").trim();
            String a = author.toString().replaceAll("\\<.*?\\>", "").trim();
            String d = date.toString().replaceAll("\\<.*?\\>", "").trim();

            String string = d.trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
            LocalDate date1 = LocalDate.parse(string, formatter);

            writeFile("https://www.cochranelibrary.com" + reviewLink, topic, t, a, date1.toString());
        }
        return Integer.parseInt(counts.trim());
    }

    /**
     * Writes the review information to file using BufferedWriter
     *
     * @param url    - The link of review
     * @param topic  - The topic
     * @param title  - The title of review
     * @param author - The author of review
     * @param date   - The created date of review
     * @throws IOException
     */
    private void writeFile(String url, String topic, String title, String author, String date) throws IOException {
        log.info("Writing review datas to file...");
        String fileName = "src/main/resources/cochrane_reviews.txt";
        String data = String.format("%s|%s|%s|%s|%s%n%n", url, topic, title, author, date);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.write(data);
        writer.close();
    }
}
