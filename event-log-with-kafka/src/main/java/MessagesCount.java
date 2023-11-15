import lombok.Data;

import java.util.Date;

@Data
public class MessagesCount {
    private String userId;
    private long count;
    private Date timestamp;
}
