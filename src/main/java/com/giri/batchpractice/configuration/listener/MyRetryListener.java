package com.giri.batchpractice.configuration.listener;


import lombok.extern.log4j.Log4j2;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@Log4j2
public class MyRetryListener implements RetryListener {
    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        if (context.getRetryCount() > 0) {
            log.info("Attempting retry");
        }

        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() > 0){
            log.info("Failure occurred requiring retry");
        }
    }
}
