package dashstar.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "articles")
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "author_id", insertable = false, updatable = false)
    @JsonProperty("author_id")
    private Integer authorId;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Long createdAt;

}
