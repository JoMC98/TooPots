package es.uji.ei1027.toopots.comparator;

import es.uji.ei1027.toopots.model.Activity;

import java.util.Comparator;

public class TypeComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity a, Activity b) {
        String aName = a.getActivityTypeName().toLowerCase();
        String bName = b.getActivityTypeName().toLowerCase();
        return aName.compareTo(bName);
    }
}

