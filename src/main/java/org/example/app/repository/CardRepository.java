package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.exception.CardNotFoundException;
import org.example.app.util.Numbers;
import org.example.app.util.random.Random4NumberGen;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Random4NumberGen numberGenerator;
    private final RowMapper<Card> cardRowMapper = resultSet -> new Card(
            resultSet.getLong("id"),
            resultSet.getString("number"),
            resultSet.getLong("balance")
    );

    public List<Card> getAllByOwnerId(long ownerId) {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
                cardRowMapper,
                ownerId
        );
    }

    public Optional<Card> getCardById(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT c.id, c.number, c.balance FROM cards c WHERE c.id = ? AND active = TRUE",
                cardRowMapper,
                cardId
        );
    }

    public Optional<Card> getCardByNumber(String cardNumber) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT c.id, c.number, c.balance FROM cards c WHERE c.number = ? AND active = TRUE",
                cardRowMapper,
                cardNumber
        );
    }

    public Card doOrder(Card from, Card to, long money) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                           UPDATE cards SET balance = ? WHERE number = ?;
                           UPDATE cards SET balance = ? WHERE number = ?;
                        """,
                from.getBalance() - money, from.getNumber(),
                to.getBalance() + money, to.getNumber()
        );
        // orElseThrow() has never been happen
        return getCardByNumber(from.getNumber()).orElseThrow(CardNotFoundException::new);
    }

    public Optional<Card> createNewCard(User currentUser, long balance) {
        String number = numberGenerator.getInt() + " " + numberGenerator.getInt();
        while (getCardByNumber(number).isPresent()) {
            number = numberGenerator.getInt() + " " + numberGenerator.getInt();
        }
        // language=PostgreSQL
        return jdbcTemplate.queryOne("""
                INSERT INTO cards ("ownerId", number, balance) VALUES (?, ?) RETURNING id, number, balance
                """, cardRowMapper, currentUser.getId(), number, balance);
    }

    public int blockCardByNumber(String cardNumber) {
        // language=PostgreSQL
        return jdbcTemplate.update("""
                        UPDATE cards SET active = ? WHERE number = ?
                        """,
                false, cardNumber
        );
    }
}
