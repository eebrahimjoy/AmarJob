package amarjob.com.otherClasses;

public class JobCategory {
    private String catId;
    private String catName;

    public JobCategory(){

    }

    public JobCategory(String catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }

    public String getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }
}
