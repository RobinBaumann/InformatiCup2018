package com.github.robinbaumann.informaticup2018.model;

import java.util.HashSet;
import java.util.Set;

public class StationNotFoundException extends Exception {
    private Set<Integer> ids;

    public StationNotFoundException(int id) {
        this.ids = new HashSet<>(1);
        this.ids.add(id);
    }

    public StationNotFoundException(Set<Integer> ids) {
        this.ids = ids;
    }

    public Set<Integer> getId() {
        return ids;
    }
}
