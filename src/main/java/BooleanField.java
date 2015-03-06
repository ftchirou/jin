public class BooleanField extends Field {

    private boolean value;

    public BooleanField() {
        this.type = "boolean";

    }

    public BooleanField(boolean value) {
        this.type = "boolean";
        this.value = value;
    }
}
