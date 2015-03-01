package com.github.ftchirou.yajl.serializer.tests;

import com.github.ftchirou.yajl.serializer.JsonSerializer;
import com.github.ftchirou.yajl.io.JsonWriter;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

    public void serialize(DateTime dt, JsonWriter writer) throws IOException {
        writer.writeString(dt.toString());
    }
}
