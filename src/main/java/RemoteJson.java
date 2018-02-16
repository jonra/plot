import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RemoteJson {

    @JsonProperty("type")
    public String type;
    @JsonProperty("crs")
    public Crs crs;
    @JsonProperty("features")
    public List<Feature> features = null;

}