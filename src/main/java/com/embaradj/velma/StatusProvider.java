package com.embaradj.velma;

import java.beans.PropertyChangeListener;

public interface StatusProvider {

    public void addListener(PropertyChangeListener listener);
    public void removeListener(PropertyChangeListener listener);
}
