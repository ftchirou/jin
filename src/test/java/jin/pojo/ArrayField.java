package jin.pojo;

import java.util.ArrayList;
import java.util.List;

public class ArrayField extends Field {

    private List<Field> fields;

    public ArrayField() {
        this.fields = new ArrayList<>();
    }

    public ArrayField(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
