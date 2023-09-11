package com.min.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true) //부모 클래스의 ToString도 볼 수 있다
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;    // 유저 정보(ID)

    @Setter @Column(nullable = false)
    private String title;       //제목
    @Setter @Column(nullable = false,length = 10000)
    private String content;     //내용
    @Setter private String hashtag;     //해시태그로 검색하기


    @ToString.Exclude
    @OrderBy("createdAt DESC")      //시간순 정렬로 댓글리스트를 뽑는것
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();      //중복을 허용하지 않고 모아서 컬렉션으로 보겠다는 의도의 Set<>


    protected Article() {

    }

    private Article(UserAccount userAccount, String title, String content, String hashtag) {         //접근 제어자를 private으로 둠으로써 팩토리 메서드로 받아서 사용하게 설정
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {       //동일성 검사를 실행함.
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


