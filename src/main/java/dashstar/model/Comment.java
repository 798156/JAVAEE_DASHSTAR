package dashstar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private Article article;

    @Column(name = "article_id", insertable = false, updatable = false)
    @JsonProperty("article_id")
    private Integer articleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    @JsonProperty("user_id")
    private Integer userId;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Long createdAt;

}
