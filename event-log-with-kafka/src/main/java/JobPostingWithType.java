import lombok.Data;

@Data
public class JobPostingWithType {
    private String userId;
    private String jobTitle;
    private String jobDescription;
    private Integer salary;
    private String type;

}
