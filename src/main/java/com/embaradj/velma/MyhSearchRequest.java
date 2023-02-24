package com.embaradj.velma;

import java.util.Arrays;
import java.util.List;

/**
 * Used for creating a json request with Gson
 */
public class MyhSearchRequest {

    private int count = 1000;
    private List<?> facets = Arrays.asList("beslutsår","katalog");
    private String katalog = "Ansökan/beslut om att bedriva yrkeshögskoleutbildning";
    private String query = "Systemutvecklare inriktning säkerhet";
    private int skip = 0;

    protected MyhSearchRequest(String query) {
        this.query = query;
    }
}
