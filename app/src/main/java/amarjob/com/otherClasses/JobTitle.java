package amarjob.com.otherClasses;

public class JobTitle {

    private String titleId;
    private String titleName;

    public JobTitle(){

    }

    public JobTitle(String titleId, String titleName) {
        this.titleId = titleId;
        this.titleName = titleName;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getTitleName() {
        return titleName;
    }
}
