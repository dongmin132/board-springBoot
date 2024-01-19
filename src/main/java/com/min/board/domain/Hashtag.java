package com.min.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(columnList = "hashtagName"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
}
)
@ToString(callSuper = true)     //auditingFields까지 나오도록
@Getter
public class Hashtag extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false,unique = true)
    private String hashtagName;     //해시태그 이름

    @ToString.Exclude       //jpa 순환참조 문제를 해결하기 위해서 붙인다.
    @ManyToMany(mappedBy = "hashtags")
    private Set<Article> articles = new LinkedHashSet<>();

    public Hashtag() {
    }

    private Hashtag(String hashtagName) {       //factory method 패턴을 사용하기 위해서 외부에서 보지 못하도록 감춘다
        this.hashtagName = hashtagName;
    }

    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return getId().equals(hashtag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
