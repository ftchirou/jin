package jin.databind.tests.pojo;

public class NumericField extends Field {

    private int value;

    public NumericField() {

    }

    public NumericField(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
