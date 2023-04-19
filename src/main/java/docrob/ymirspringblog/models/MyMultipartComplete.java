package docrob.ymirspringblog.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MyMultipartComplete {
    private String key;
    private String uploadId;
    private MyFilePart [] fileParts;
}
