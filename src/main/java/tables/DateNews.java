package tables;

import java.util.Date;

public class DateNews extends News {

    private Date clickDate;
    private Date sendDate;
    private String note;

    public Date getClickDate() {
        return clickDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public String getNote() {
        return note;
    }
}
