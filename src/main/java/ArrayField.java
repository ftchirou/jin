import java.util.ArrayList;
import java.util.List;

public class ArrayField extends Field {

    private List<Field> fields;

    public ArrayField() {
        this.type = "array";
        this.fields = new ArrayList<Field>();
    }

    public ArrayField(List<Field> fields) {
        this.type = "array";
        this.fields = fields;
    }
}
