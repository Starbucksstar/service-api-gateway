package com.scene.serviceapigateway.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenErrorOutputDTO {
    private int errorCode;
    private String errorMsg;
}
