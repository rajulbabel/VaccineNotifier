package com.vaccinenotifier.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder
@FieldNameConstants
public class VaccineConstraints {

    private Integer age;
    private String vaccine;
    private String feeType;
    private String dose;
    private Integer districtId;
    private String districtName;
}
