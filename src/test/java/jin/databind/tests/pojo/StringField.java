package jin.databind.tests.pojo;

public class StringField extends Field {

    private String value;

    public StringField() {

    }

    public StringField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
