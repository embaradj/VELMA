package com.embaradj.velma.results;

/**
 * Interface for a search Hit (either a HVE or a Job ad)
 */

public interface SearchHit {

    public String getTitle();
    public String toString();

    public String getType();

    public String getDescription();

}
