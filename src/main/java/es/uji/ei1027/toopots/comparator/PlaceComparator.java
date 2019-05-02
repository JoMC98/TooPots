package es.uji.ei1027.toopots.comparator;

import es.uji.ei1027.toopots.model.Activity;

import java.util.Comparator;

public class PlaceComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity a, Activity b) {
        return a.getPlace().compareTo(b.getPlace());
    }
}

