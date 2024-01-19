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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true) //부모 클래스의 ToString도 볼 수 있다
@Table(indexes = {
        @Index(columnList = "title"),
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

    @ToString.Exclude
    @JoinTable(     //게시글과 해시태그에 연관된 테이블을 JPA에서 자동으로 만들어준다.
            name = "article_hashtag",
            joinColumns = @JoinColumn(name = "articleId"),          // 게시글 ID가 주인이고
            inverseJoinColumns = @JoinColumn(name = "hashtagId")    //상대 조인할 ID는 hashtagId가 된다.
    )
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})      //insert와 update가 Hashtag에도 관계를 미친다.
                                                                        // 다대다 관계이므로 1번 게시글이 삭제 되었을 때 다른 게시글에 Hashtag가 삭제될수도있다
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")      //시간순 정렬로 댓글리스트를 뽑는것
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();      //중복을 허용하지 않고 모아서 컬렉션으로 보겠다는 의도의 Set<>


    protected Article() {

    }

    private Article(UserAccount userAccount, String title, String content) {         //접근 제어자를 private으로 둠으로써 팩토리 메서드로 받아서 사용하게 설정
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Article of(UserAccount userAccount, String title, String content) {
        return new Article(userAccount, title, content);
    }

    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll((hashtags));
    }

    public void clearHashtags() {
        this.getHashtags().clear();
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


