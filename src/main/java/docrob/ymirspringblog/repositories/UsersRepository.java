package docrob.ymirspringblog.repositories;

import docrob.ymirspringblog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Long> {
}
