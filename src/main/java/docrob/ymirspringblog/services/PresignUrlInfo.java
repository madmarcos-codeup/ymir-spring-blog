package docrob.ymirspringblog.services;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PresignUrlInfo {
    private String url;
    private String key;

}
