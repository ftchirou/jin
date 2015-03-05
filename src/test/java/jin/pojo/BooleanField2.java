package jin.pojo;

public class BooleanField2 extends FieldWithCustomTypeInfo {

    private boolean value;

    public BooleanField2() {
        this.type = "boolean";
    }

    public BooleanField2(boolean value) {
        this.type = "boolean";
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
