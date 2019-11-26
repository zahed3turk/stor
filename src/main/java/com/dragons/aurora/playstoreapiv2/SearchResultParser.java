package com.dragons.aurora.playstoreapiv2;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

public class SearchResultParser {
    public static final int ALL = 0;
    public static final int SEARCH = 1;
    public static final int SIMILAR = 2;
    public static final int RELATED = 3;

    private ArrayList<DocV2> items;
    private String nextPageUrl;
    private String title;
    private int type;

    public SearchResultParser(int type) {
        this.items = new ArrayList<>();
        this.nextPageUrl = null;
        this.type = type;
    }

    public String getNextPageUrl() {
        return GooglePlayAPI.FDFE_URL + nextPageUrl;
    }

    public void append(ResponseWrapper rw) {

        append(ResponseUtil.searchResponse(rw).getDocList());
        append(ResponseUtil.listResponse(rw).getDocList());
        for (PreFetch pf : rw.getPreFetchList()) {
            try {
                append(ResponseWrapper.parseFrom(pf.getResponse().toByteString()));
            } catch (InvalidProtocolBufferException ignored) {
            }
        }
    }

    private void append(List<DocV2> list) {
        for (DocV2 doc : list) {
            append(doc);
        }
    }

    public void append(DocV2 doc) {
        switch (doc.getDocType()) {
            case 46: {
                for (DocV2 child : doc.getChildList()) {
                    if (accept(child)) {
                        append(child);
                    }
                }
                break;
            }
            case 45: {
                for (DocV2 docV2 : doc.getChildList()) {
                    if (docV2.getDocType() == 1) {
                        items.add(docV2);
                    }
                }
                nextPageUrl = null;
                if (doc.hasContainerMetadata()) {
                    nextPageUrl = doc.getContainerMetadata().getNextPageUrl();
                }
                if (title == null && doc.hasTitle()) {
                    title = doc.getTitle();
                }
                break;
            }
            default: {
                for (DocV2 child : doc.getChildList()) {
                    append(child);
                }
                break;
            }
        }
    }

    private boolean accept(DocV2 doc) {
        String pattern = doc.getBackendDocid();
        switch (type) {
            case ALL: {
                return true;
            }
            case SEARCH: {
                return (pattern != null && pattern.matches(".*search.*"));
            }
            case SIMILAR: {
                return (pattern != null && pattern.matches("similar_apps"));
            }
            case RELATED: {
                return (pattern != null && pattern
                        .matches("pre_install_users_also_installed"));
            }
            default: {
                return false;
            }
        }
    }

    public List<DocV2> getDocList() {
        return items;
    }
}
