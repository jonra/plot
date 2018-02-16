import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Geometry {

    @JsonProperty("type")
    public String type;
    @JsonProperty("coordinates")
    public List<Double> coordinates = null;

}