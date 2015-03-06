
public class StringField extends Field {

    private String value;

    public StringField() {
        this.type = "string";
    }

    public StringField(String value) {
        this.type = "string";
        this.value = value;
    }
}
