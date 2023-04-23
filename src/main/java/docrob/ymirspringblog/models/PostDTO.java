package docrob.ymirspringblog.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PostDTO {
    private Long id;
    private String title;
    private String body;
}
