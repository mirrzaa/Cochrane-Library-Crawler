package uz.example.crawler.util;

public interface AppConstants {
    //Request Headers
    String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
    String ACCEPT_ENCODING = "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6,zh;q=0.5";
    String REFERER = "https://www.cochranelibrary.com/cdsr/reviews/topics";
    String SEC_CH_UA = "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"";
    String SEC_CH_UA_MOBILE = "?0";
    String SEC_CH_UA_PLATFORM = System.getProperties().get("os.name").toString();
    String SEC_FETCH_DEST = "document";
    String SEC_FETCH_MODE = "same-origin";
    String SEC_FETCH_USER = "?1";
    String UPGRADE_INSECURE_REQUESTS = "1";

    //Request Params
    String P_P_ID = "scolarissearchresultsportlet_WAR_scolarissearchresults";
    String P_P_LIFECYCLE = "0";
    String P_P_STATE = "normal";
    String P_P_MODE = "view";
    String P_P_COL_ID = "column-1";
    String P_P_COL_COUNT = "1";
    String _SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_SEARCH_TYPE = "basic";
    String _SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_FACET_QUERY_FIELD = "topic_id";
    String _SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_SEARCH_BY = "13";
    String _SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_ORDER_BY = "displayDate-true";
    String _SCOLARISSEARCHRESULTSPORTLET_WAR_SCOLARISSEARCHRESULTS_FACET_CATEGORY = "Topics";
    String SELECTED_TYPE = "review";
}
