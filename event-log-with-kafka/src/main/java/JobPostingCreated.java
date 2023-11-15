import lombok.Data;

@Data
public class JobPostingCreated {
    private String userId;
    private String jobTitle;
    private String jobDescription;
    private Integer salary;
}
