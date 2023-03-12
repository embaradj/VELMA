package com.embaradj.velma.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents result from a search in the Susa API. The members are automatically populated by Gson when parsing the
 * json response from the server. Don't change member names..
 */
public class SusaResult {

    private List<SusaHit> content;

    public List<String> getCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (SusaHit c : content) codes.add(c.getCode());
        return codes;
    }

    public List<SusaHit> getResults() { return this.content; }

    public class SusaHit {
        private Content content;
        private String pdfUrl = "";
        public String getCode() { return content.getCode(); }
        public String getTitle() { return content.getTitle(); }
        public String toString() { return this.getCode() + " : " + this.getTitle(); }
        public String getPdfUrl() { return pdfUrl; }
        public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    }

    public class Content {
        private EducationInfo educationInfo;
        public String getCode() { return educationInfo.getCode(); }
        public String getTitle() { return educationInfo.getTitle(); }
    }

    public class EducationInfo {
        private String code;
        private Title title;
        public String getCode() { return this.code; }
        public String getTitle() { return title.getTitle(); }
    }

    public class Title {
        private List<_String> string;
        public String getTitle() { return string.get(0).getTitle(); }
    }

    public class _String {
        private String lang, content;
        public String getTitle() { return content; }
    }

}
