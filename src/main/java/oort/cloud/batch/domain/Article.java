package oort.cloud.batch.domain;

import java.time.LocalDateTime;

public class Article {
    private String title;
    private String desc;
    private LocalDateTime createdAt;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
