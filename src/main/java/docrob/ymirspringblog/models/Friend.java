package docrob.ymirspringblog.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="friends")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private User friend1;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private User friend2;

    private boolean bff;
}
