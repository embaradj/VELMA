package com.embaradj.velma;

import com.embaradj.velma.results.SearchHit;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom MouseAdapter to use on JLists
 */
class CustomMouseAdapter extends MouseAdapter {
    JList list;

    CustomMouseAdapter(JList list) {
        this.list = list;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        int index = list.locationToIndex(e.getPoint());
        Object hit = list.getModel().getElementAt(index);
        SearchHit sh = (SearchHit)  hit;
        DetailsForm detailsForm = new DetailsForm(sh);
    }
}