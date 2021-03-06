package parsers;

public class Item {
    private String title;
    private String description;
    private String link;

    public Item() {
        this.title = "";
        this.description = "";
        this.link = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
