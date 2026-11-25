package com.ayanlab.hrm.backend.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateShiftInstancesRequest {
    private List<String> dates = new ArrayList<>();
    private String from;
    private String to;
}
