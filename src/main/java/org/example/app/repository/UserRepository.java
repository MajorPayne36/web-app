package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.app.domain.UserWithRole;
import org.example.app.entity.UserEntity;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepository {
  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<User> rowMapper = resultSet -> new User(
      resultSet.getLong("id"),
      resultSet.getString("username")
  );
  private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
      resultSet.getLong("id"),
      resultSet.getString("username"),
      resultSet.getString("password")
  );
  private final RowMapper<UserWithRole> rowMapperWithRole = resultSet -> new UserWithRole(
      resultSet.getLong("id"),
      resultSet.getString("username"),
      resultSet.getString("role")
  );

  public Optional<User> getByUsername(String username) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
  }

  public Optional<UserWithPassword> getByUsernameWithPassword(EntityManager entityManager, EntityTransaction transaction, String username) {
    // em, emt - closeable
    return entityManager.createNamedQuery(UserEntity.FIND_BY_USERNAME, UserEntity.class)
        .setParameter("username", username)
        .setMaxResults(1)
        .getResultStream()
        .map(o -> new UserWithPassword(o.getId(), o.getUsername(), o.getPassword()))
        .findFirst();
    // language=PostgreSQL
    // return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
  }

  /**
   * saves user to db
   *
   * @param id       - user id, if 0 - insert, if not 0 - update
   * @param username
   * @param hash
   */
  // TODO: DuplicateKeyException <-
  public Optional<User> save(long id, String username, String hash) {
    // language=PostgreSQL
    return id == 0 ? jdbcTemplate.queryOne(
        """
            INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username
            """,
        rowMapper,
        username, hash
    ) : jdbcTemplate.queryOne(
        """
            UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
            """,
        rowMapper,
        username, hash, id
    );
  }

  public Optional<User> findByToken(String token) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
        """
            SELECT u.id, u.username FROM tokens t
            JOIN users u ON t."userId" = u.id
            WHERE t.token = ?
            """,
        rowMapper,
        token
    );
  }

  public void saveToken(long userId, String token) {
    // query - SELECT'ов (ResultSet)
    // update - ? int/long
    // language=PostgreSQL
    jdbcTemplate.update(
        """
            INSERT INTO tokens(token, "userId") VALUES (?, ?)
            """,
        token, userId
    );
  }

  /**
   * Find in Cards table the card owner
   * @param cardNumber the number of card which owner we need to return
   * @return owner of card
   */
  public Optional<User> getCardOwnerByNumber(String cardNumber){
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            """
            SELECT u.id, u.username FROM users u
            Join cards c on u.id = c."ownerId"
            WHERE c.number = ?
            """,
            rowMapper,
            cardNumber
    );
  }/**
   * Find in Cards table the card owner
   * @param id the ID of card which owner we need to return
   * @return owner of card
   */
  public Optional<User> getCardOwnerById(long id){
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            """
            SELECT u.id, u.username FROM users u
            Join cards c on u.id = c."ownerId"
            WHERE c.id = ?
            """,
            rowMapper,
            id
    );
  }

  public Optional<UserWithRole> findByTokenWithRole(String token) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            """
                SELECT u.id, u.username, u.role FROM tokens t
                JOIN users u ON t."userId" = u.id
                WHERE t.token = ?
                """,
            rowMapperWithRole,
            token
    );
  }
}
