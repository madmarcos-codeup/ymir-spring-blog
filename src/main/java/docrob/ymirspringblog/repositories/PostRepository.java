package docrob.ymirspringblog.repositories;

import docrob.ymirspringblog.models.Post;
import docrob.ymirspringblog.models.PostDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitle(String title);

    @Query(value = "select p.id, p.title, p.body " +
            "from posts p " +
            "inner join friends f on p.creator_id = f.friend2_id " +
            "where f.friend1_id = :userId", nativeQuery = true)
    List<PostDTO> findMyFriendsPosts(@Param("userId") Long userId);
}
