package com.giri.batchpractice.configuration.writer;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Log4j2
public class SimpleItemWriter implements ItemWriter<String> {

    @Override
    public void write(List<? extends String> items) {
        log.info("Received list of size : {}", items.size());
        items.forEach(log::info);
    }
}
