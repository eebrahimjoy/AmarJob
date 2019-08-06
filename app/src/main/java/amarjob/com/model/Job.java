package amarjob.com.model;

public class Job {

    String id;
    private String type;
    private String date;
    private String time;
    private String title;
    private String availableJobAmount;

    public Job(){

    }

    public Job(String id, String type, String date, String time, String title,String availableJobAmount) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.time = time;
        this.title = title;
        this.availableJobAmount = availableJobAmount;
    }

    public String getAvailableJobAmount() {
        return availableJobAmount;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
