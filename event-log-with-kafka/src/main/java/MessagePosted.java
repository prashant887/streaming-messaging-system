import lombok.Data;

import java.util.Date;

@Data
public class MessagePosted {
    private String userId;
    private String message;
    private Date createdAt;

}
