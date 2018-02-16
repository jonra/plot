import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data

public class Properties_ {

    @JsonProperty("name")
    public String name;
    @JsonProperty("u_name")
    public String uName;
    @JsonProperty("area")
    public String area;
    @JsonProperty("town")
    public String town;

}