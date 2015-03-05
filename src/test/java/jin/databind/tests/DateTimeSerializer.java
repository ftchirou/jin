package jin.databind.tests;

import jin.databind.JsonSerializer;
import jin.io.JsonWriter;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

    public void serialize(DateTime dt, JsonWriter writer) throws IOException {
        writer.writeString(dt.toString());
    }
}
