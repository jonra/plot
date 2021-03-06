import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data

public class Crs {

    @JsonProperty("type")
    public String type;
    @JsonProperty("properties")
    public Properties properties;

}