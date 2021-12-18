package parsers;

public class Site {
    private String name;
    private String rss_feed;

    public Site(String name, String rss_feed) {
        this.name = name;
        this.rss_feed = rss_feed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssFeed() {
        return rss_feed;
    }

    public void setRssFeed(String rss_feed) {
        this.rss_feed = rss_feed;
    }
}
