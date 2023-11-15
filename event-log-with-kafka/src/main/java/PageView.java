import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PageView implements Serializable {

    private String userName;
    private String page;
    private String browser;
    private Date viewDate;
}
