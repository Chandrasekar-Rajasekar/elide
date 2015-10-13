/*
 * Copyright 2015, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide.audit;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Logger implementation which logs to SLF4J.
 */
@Slf4j
public class Slf4jLogger extends Logger {

    @Override
    public void commit() throws IOException {
        synchronized (messages) {
            for (LogMessage message : messages) {
                log.info("{} {} {}", System.currentTimeMillis(), message.getOperationCode(), message.getMessage());
            }
            messages.clear();
        }
    }
}
