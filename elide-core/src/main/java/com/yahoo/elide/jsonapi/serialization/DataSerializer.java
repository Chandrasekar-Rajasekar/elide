/*
 * Copyright 2015, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide.jsonapi.serialization;

import com.yahoo.elide.jsonapi.models.Data;
import com.yahoo.elide.jsonapi.models.Resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Custom serializer for top-level data
 */
public class DataSerializer extends JsonSerializer<Data<Resource>> {

    @Override
    public void serialize(Data<Resource> data, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        Collection<Resource> list = data.get();
        if (data.isToOne()) {
            if (list == null || list.isEmpty()) {
                jsonGenerator.writeObject(null);
                return;
            }
            jsonGenerator.writeObject(list.iterator().next());
            return;
        }
        jsonGenerator.writeObject((list == null) ? Collections.emptyList() : list);
    }
}
