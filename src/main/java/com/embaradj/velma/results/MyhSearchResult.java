package com.embaradj.velma.results;

import java.util.List;

/**
 * Represents a searchresult in the MYH database.
 * The members are populated automatically by Gson when parsing Json response from the API.
 */
public class MyhSearchResult {

    private List<Hit> result;
    public List<Hit> getResult() { return result; }

    public class Handling {
        private List<Dokument> dokument;
        private String namn, typ;

        public Boolean isDecision() {
            return this.typ.toLowerCase().contains("beslut");
        }

        public boolean isApproved() {
            if (namn.toLowerCase().contains("beviljande")) return true;
            if (namn.toLowerCase().contains("avslag")) return false;

            Exception approvedException = new Exception("Could not determine whether request was approved or not");
            approvedException.printStackTrace();
            return false;
        }

        protected boolean isRequest() {
            return (this.typ.toLowerCase().contains("ansökan") && this.namn.toLowerCase().contains("ansökningshandlingar"));
        }

        public String toString() {
            return "\tHandling: " + this.namn;
        }

    }

    public class Dokument {
        private String namn, typ, url;

        public boolean isRequest() {
            // todo: problem! Många dokument innnehåller inte texten ansökan i namnet,, och det kan finnas flera
            // kompletterande dokument  i samma "handling".
//            return (this.namn.toLowerCase().contains("ansökan"));
            return true;
        }
        public String getPdfUrl() { return this.url; }
    }

    public class Hit {
        private String id;
        private String katalog, registreringsdatum, typ, anordnare;
        private List<Handling> handlingar;

        public List<Handling> getHandlingar() { return this.handlingar; }
        public String getAnordnare() { return this.anordnare; }
        public String getId() { return this.id; }

        public int getYear() {
            return Integer.parseInt(this.registreringsdatum.split("-")[0]);
        }

        /**
         * Kontrollerar om ansökan i denna sökträff är godkänd
         * @return
         */
        public Boolean isApproved() {
            for (Handling handling : handlingar) {
                if (handling.isDecision()) {
                    return (handling.isApproved());
                }
            }
//            System.out.println("Could not determine whether approved or not: \n" + getId());
            // Considered not approved if there is no "beviljande" dokument in this "Handling".
            return false;
        }

        public String getInstitution() { return this.anordnare; }

        public String getSyllabusUrl() {

            // Hitta ansökninghandling
            for (Handling handling : handlingar) {
                if (handling.isRequest()) {
                    // Hitta dokument
                    for (Dokument dokument : handling.dokument) {
                        if (dokument.isRequest()) {
                            // Hitta URL till PDF
                            return dokument.getPdfUrl();
                        }
                    }
                }
            }

            System.out.println("Could not find a syllabus");

            return null;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("-------\nSökträff id: " + id + ", antal handlingar: " + handlingar.size() + "\n");

            for (Handling handling : handlingar) {
                sb.append(handling.toString() + "\n");
            }

            return sb.toString();
        }

    }

}
