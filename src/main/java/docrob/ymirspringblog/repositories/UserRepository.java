package docrob.ymirspringblog.repositories;

import docrob.ymirspringblog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    @Query(value = "delete from posts where id = :postId", nativeQuery = true)
    void deletePostById(@Param("postId") Long postId);

}
