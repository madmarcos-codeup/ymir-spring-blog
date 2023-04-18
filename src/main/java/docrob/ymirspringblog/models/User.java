package docrob.ymirspringblog.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator")
    @ToString.Exclude
    @JsonIgnore
    private List<Post> posts;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name="follows",
            joinColumns={@JoinColumn(name="followee_id")},
            inverseJoinColumns={@JoinColumn(name="follower_id")}
    )
    @JsonIgnore
    private List<User> followers;

    public User(User copy) {
        id = copy.id;
        email = copy.email;
        name = copy.name;
        password = copy.password;
    }

    public String getUsername() {
        return name;
    }

}
