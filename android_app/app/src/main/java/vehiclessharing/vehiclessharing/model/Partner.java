package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/14/2017.
 */

public class Partner {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("avatar_link")
    @Expose
    private String avartarLink;

    public String getAvartarLink() {
        return avartarLink;
    }

    public void setAvartarLink(String avartarLink) {
        this.avartarLink = avartarLink;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
