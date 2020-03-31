package com.dragons.aurora.playstoreapiv2;

import java.io.IOException;
import java.util.*;

/**
 * Iterates through search result pages
 * Each next() call gets you a next page of search results for the provided query
 */
public class AppStreamIterator implements Iterator {

    private GooglePlayAPI googlePlayAPI;
    private DocV2 docV2;
    private boolean isFirstPage = true;

    public AppStreamIterator(GooglePlayAPI googlePlayApi, GooglePlayAPI.APP_STREAM_TAB streamTab) throws IOException {
        this.googlePlayAPI = googlePlayApi;

        final String url = GooglePlayAPI.APP_STREAM_URL;
        final Map<String, String> params = new HashMap<>();
        params.put("tab", streamTab.name());
        params.put("n", "15");

        final Payload payload = googlePlayApi.genericGet(url, params);
        docV2 = payload.getListResponse().getDoc(0);
    }

    @Override
    public boolean hasNext() {
        return docV2.getChild(0).hasContainerMetadata() && docV2.getChild(0).getContainerMetadata().hasNextPageUrl();
    }

    @Override
    public List<DocV2> next() {
        if (isFirstPage) {
            isFirstPage = false;
            return docV2.getChildList();
        }

        final List<DocV2> docV2List = new ArrayList<>();
        try {
            if (docV2.getChild(0).hasContainerMetadata() && docV2.getChild(0).getContainerMetadata().hasNextPageUrl()) {
                final String nextPageUrl = docV2.getChild(0).getContainerMetadata().getNextPageUrl();
                final Payload payload = googlePlayAPI.genericGet(GooglePlayAPI.FDFE_URL + nextPageUrl, new HashMap<>());
                docV2 = payload.getListResponse().getDoc(0);

                if (docV2.getChildList().isEmpty())
                    docV2List.addAll(docV2.getChildList());
            }
            return docV2List;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
