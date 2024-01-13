package hhg0104.codereview.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseTemplate {

    private String message;

    private String errorMessage;

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
