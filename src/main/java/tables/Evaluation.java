package tables;

import java.util.Date;

public class Evaluation {
    private int id;
    private int id_news;
    private int id_user;
    private int user_note;
    private Date click_date;
    private Date send_date;

    public Evaluation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdNews() {
        return id_news;
    }

    public void setIdNews(int id_news) {
        this.id_news = id_news;
    }

    public int getIdUser() {
        return id_user;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }

    public int getUserNote() {
        return user_note;
    }

    public void setUserNote(int user_note) {
        this.user_note = user_note;
    }

    public Date getClickDate() {
        return click_date;
    }

    public void setClickDate(Date click_date) {
        this.click_date = click_date;
    }

    public Date getSendDate() {
        return send_date;
    }

    public void setSendDate(Date send_date) {
        this.send_date = send_date;
    }
}
