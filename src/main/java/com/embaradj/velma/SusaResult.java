package com.embaradj.velma;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents result from a search in the Susa API. The members are automatically populated by Gson when parsing the
 * json response from the server. Don't change member names..
 */
public class SusaResult {

    private List<SusaHit> content;

    protected List<String> getCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (SusaHit c : content) codes.add(c.getCode());
        return codes;
    }

    protected List<SusaHit> getResults() { return this.content; }

    class SusaHit {
        private Content content;
        private String pdfUrl = "";

        protected String getCode() { return content.getCode(); }
        protected String getTitle() { return content.getTitle(); }
        public String toString() { return this.getCode() + " : " + this.getTitle(); }
        protected String getPdfUrl() { return pdfUrl; }
        protected void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    }

    class Content {
        private EducationInfo educationInfo;
        protected String getCode() { return educationInfo.getCode(); }
        protected String getTitle() { return educationInfo.getTitle(); }
    }

    class EducationInfo {
        private String code;
        private Title title;
        protected String getCode() { return this.code; }
        protected String getTitle() { return title.getTitle(); }
    }

    class Title {
        private List<_String> string;
        protected String getTitle() { return string.get(0).getTitle(); }
    }

    class _String {
        private String lang, content;
        protected String getTitle() { return content; }
    }

}
