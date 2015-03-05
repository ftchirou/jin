package com.github.ftchirou.yajl.pojo;

import com.github.ftchirou.yajl.annotations.JsonType;
import com.github.ftchirou.yajl.annotations.JsonTypeInfo;
import com.github.ftchirou.yajl.annotations.JsonTypes;

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
