package com.giri.batchpractice.configuration.reader;

import org.springframework.batch.item.ItemReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleItemReader implements ItemReader<String> {

    private final Iterator<String> iterator;

    public SimpleItemReader(){
        List<String> dataSet = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            dataSet.add(String.valueOf(i));
        }

        iterator = dataSet.iterator();
    }

    @Override
    public String read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
