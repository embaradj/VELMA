package com.embaradj.velma.results;

/**
 * Interface for a search Hit (either a HVE or a Job ad)
 */

public interface SearchHit {
    String title();
    String toString();
    String getType();
    String getDescription();

}
