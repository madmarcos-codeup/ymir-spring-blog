package docrob.ymirspringblog.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MyMultiPartUpload {
    private String uploadId;
    private String key;
}
