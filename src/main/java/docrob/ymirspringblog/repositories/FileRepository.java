package docrob.ymirspringblog.repositories;

import docrob.ymirspringblog.models.MyFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;

public interface FileRepository extends JpaRepository<MyFile, Long> {

}
