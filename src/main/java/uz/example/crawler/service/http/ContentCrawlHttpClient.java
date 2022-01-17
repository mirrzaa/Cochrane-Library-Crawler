package uz.example.crawler.service.http;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.example.crawler.util.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContentCrawlHttpClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static String sessionId = "";

    /**
     * Sends request to https://www.cochranelibrary.com/ server
     *
     * @param url           - The address of remote server to
     * @param topic         - The topic
     * @param isPageRequest - Is page request or topic
     * @param pageNumber    - The page number
     * @return
     */
    public CloseableHttpResponse sendRequest(String url, String topic, boolean isPageRequest, String pageNumber) {
        log.info("Send request to remote server...");
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient;
        try {
            HttpPost request = new HttpPost(url);
            createHeaders(request);
            List<NameValuePair> urlParameters = createParams(topic, isPageRequest, pageNumber);
            request.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(request);
        } catch (IOException e) {
            log.error("IO Exception: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return response;
    }

    private List<NameValuePair> createParams(String topic, boolean isPageRequest, String pageNumber) {
        log.info("Set request params...");
        List<NameValuePair> urlParameters = new ArrayList<>();
        if (isPageRequest) {
            urlParameters.addAll(createPageRequestParams(topic, pageNumber));
        } else {
            urlParameters.addAll(createTopicRequestParams(topic));
        }
        urlParameters.add(new BasicNameValuePair("p_p_id", AppConstants.P_P_ID));
        urlParameters.add(new BasicNameValuePair("p_p_lifecycle", AppConstants.P_P_LIFECYCLE));
        urlParameters.add(new BasicNameValuePair("p_p_state", AppConstants.P_P_STATE));
        urlParameters.add(new BasicNameValuePair("p_p_mode", AppConstants.P_P_MODE));
        urlParameters.add(new BasicNameValuePair("p_p_col_id", AppConstants.P_P_COL_ID));
        urlParameters.add(new BasicNameValuePair("p_p_col_count", AppConstants.P_P_COL_COUNT));

        return urlParameters;
    }


    private List<NameValuePair> createTopicRequestParams(String topic) {
        log.info("Set params...");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_displayText", topic));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_searchText", topic));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_searchType", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_SEARCH_TYPE));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_facetQueryField", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_FACET_QUERY_FIELD));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_searchBy", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_SEARCH_BY));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_orderBy", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_ORDER_BY));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_facetDisplayName", topic));
        urlParameters.add(new BasicNameValuePair("_scolarissearchresultsportlet_WAR_scolarissearchresults_facetCategory", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_FACET_CATEGORY));

        return urlParameters;
    }

    private List<NameValuePair> createPageRequestParams(String topic, String pageNumber) {
        log.info("Set params...");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("min_year", null));
        urlParameters.add(new BasicNameValuePair("max_year", null));
        urlParameters.add(new BasicNameValuePair("custom_min_year", null));
        urlParameters.add(new BasicNameValuePair("custom_max_year", null));
        urlParameters.add(new BasicNameValuePair("searchBy", "13"));
        urlParameters.add(new BasicNameValuePair("searchText", topic));
        urlParameters.add(new BasicNameValuePair("selectedType", AppConstants.SELECTED_TYPE));
        urlParameters.add(new BasicNameValuePair("isWordVariations", null));
        urlParameters.add(new BasicNameValuePair("resultPerPage", "25"));
        urlParameters.add(new BasicNameValuePair("searchType", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_SEARCH_TYPE));
        urlParameters.add(new BasicNameValuePair("orderBy", AppConstants._SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_ORDER_BY));
        urlParameters.add(new BasicNameValuePair("publishDateTo", null));
        urlParameters.add(new BasicNameValuePair("publishDateFrom", null));
        urlParameters.add(new BasicNameValuePair("publishYearTo", null));
        urlParameters.add(new BasicNameValuePair("publishYearFrom", null));
        urlParameters.add(new BasicNameValuePair("displayText", topic));
        urlParameters.add(new BasicNameValuePair("forceTypeSelection", "true"));
        urlParameters.add(new BasicNameValuePair("cur", pageNumber));

        return urlParameters;
    }

    private void createHeaders(HttpPost request) {
        log.info("Set request headers...");
        request.addHeader(HttpHeaders.ACCEPT, AppConstants.ACCEPT);
        request.addHeader(HttpHeaders.ACCEPT_ENCODING, AppConstants.ACCEPT_ENCODING);
        request.addHeader("Cookie", sessionId);
        request.addHeader(HttpHeaders.REFERER, AppConstants.REFERER);
        request.addHeader("sec-ch-ua", AppConstants.SEC_CH_UA);
        request.addHeader("sec-ch-ua-mobile", AppConstants.SEC_CH_UA_MOBILE);
        request.addHeader("sec-ch-ua-platform", AppConstants.SEC_CH_UA_PLATFORM);
        request.addHeader("sec-fetch-dest", AppConstants.SEC_FETCH_DEST);
        request.addHeader("sec-fetch-mode", AppConstants.SEC_FETCH_MODE);
        request.addHeader("sec-fetch-user", AppConstants.SEC_FETCH_USER);
        request.addHeader("upgrade-insecure-requests", AppConstants.UPGRADE_INSECURE_REQUESTS);
        request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
    }
}
