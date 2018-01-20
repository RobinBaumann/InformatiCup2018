package com.github.robinbaumann.informaticup2018.model;

public class CapacityException extends Exception {
    private final int capacity;

    public CapacityException(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
