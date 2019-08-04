package amarjob.com.model;

public class District {

    private String districtId;
    private String districtName;

    public District(){

    }

    public String getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public District(String districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
    }
}
