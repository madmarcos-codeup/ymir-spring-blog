package docrob.ymirspringblog.repositories;

import docrob.ymirspringblog.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
