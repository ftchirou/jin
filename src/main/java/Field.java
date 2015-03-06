import jin.annotations.JsonType;
import jin.annotations.JsonTypeInfo;
import jin.annotations.JsonTypes;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, property="type")
@JsonTypes(
    {
        @JsonType(id="string", value=StringField.class),
        @JsonType(id="numeric", value=NumericField.class),
        @JsonType(id="boolean", value=BooleanField.class),
        @JsonType(id="array", value=ArrayField.class)
    }
)
public abstract class Field {

    protected String type;
}
