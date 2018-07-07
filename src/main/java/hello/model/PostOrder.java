package hello.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class PostOrder {
    private String size;
    private String price;
    private String side;
    @SerializedName("product_id")
    private String productId;
    private String type;
    @SerializedName("post_only")
    private Boolean postOnly;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("cancel_after")
    private String cancelAfter;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("time_in_force")
    private String timeInForce;
}
