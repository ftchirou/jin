package jin.databind.tests.pojo;

import jin.annotations.JsonType;
import jin.annotations.JsonTypeInfo;
import jin.annotations.JsonTypes;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, property="type")
@JsonTypes(
        {
            @JsonType(id="string", value=StringField2.class),
            @JsonType(id="numeric", value=NumericField2.class),
            @JsonType(id="boolean", value=BooleanField2.class)
        }
)
public abstract class FieldWithCustomTypeInfo {

    protected String type;
}
