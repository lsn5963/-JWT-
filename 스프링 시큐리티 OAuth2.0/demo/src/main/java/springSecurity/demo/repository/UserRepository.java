package springSecurity.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springSecurity.demo.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // select * from user where username = ?
    public User findByUsername(String username);
}
