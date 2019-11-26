package com.dragons.aurora.playstoreapiv2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Iterates through search result pages
 * Each next() call gets you a next page of search results for the provided query
 */
public class SearchIterator extends AppListIterator {

    public SearchIterator(GooglePlayAPI googlePlayApi, String query) {
        super(googlePlayApi);
        String url = GooglePlayAPI.SEARCH_URL;
        Map<String, String> params = new HashMap<>();
        params.put("c", "3");
        params.put("q", query);
        firstPageUrl = googlePlayApi.getClient().buildUrl(url, params);
    }

    @Override
    public List<DocV2> next() {
        try {
            Payload payload = getPayload();
            DocV2 rootDoc = getRootDoc(payload);
            SearchResultParser searchEngineResultPage = new SearchResultParser(SearchResultParser.SEARCH);
            searchEngineResultPage.append(rootDoc);
            nextPageUrl = searchEngineResultPage.getNextPageUrl();
            firstQuery = false;
            return searchEngineResultPage.getDocList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    protected boolean isRootDoc(DocV2 doc) {
        return doc != null && doc.getBackendId() == 3;
    }
}
