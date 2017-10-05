package org.test.db.dao;

class BatchCounter {
    private int value;
    int incrementAndGet() {
        return ++value;
    }

    int getValue() {
        return value;
    }

    void reset() {
        value = 0;
    }
}
