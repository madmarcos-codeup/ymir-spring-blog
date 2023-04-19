package docrob.ymirspringblog.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MyFilePart {
    private int partNum;
    private String md5Hash;
}
