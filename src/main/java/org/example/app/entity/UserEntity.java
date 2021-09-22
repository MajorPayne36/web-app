package org.example.app.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@NamedQueries(
    @NamedQuery(
        name = UserEntity.FIND_BY_USERNAME,
        query = "SELECT e FROM UserEntity e WHERE e.username = :username"
    )
)
public class UserEntity {
  public static final String FIND_BY_USERNAME = "UserEntity.findByUsername";
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(unique = true, columnDefinition = "TEXT")
  private String username;
  @Column(nullable = false, columnDefinition = "TEXT")
  private String password;
}
