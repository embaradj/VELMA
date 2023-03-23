package com.embaradj.velma.models;

import com.embaradj.velma.results.SearchHit;

/**
 * Class for wrapping info from the model to the view
 */
public class SearchHitWrapper {

    private SearchHit searchHit;
    private int total, processed;
    private String type = "";
    private boolean reset = false;

    public SearchHitWrapper(String type, boolean reset) {
        this.type = type;
        this.reset = reset;
    }

    public String getType() {
        return type;
    }

    public boolean isReset() {
        return reset;
    }

    public SearchHitWrapper(SearchHit searchHit, int total, int processed) {
        this.searchHit = searchHit;
        this.total = total;
        this.processed = processed;
    }

    public SearchHit getSearchHit() {
        return searchHit;
    }

    public int getTotal() {
        return total;
    }

     public int getProcessed() {
        return processed;
    }

}
