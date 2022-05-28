package woowacourse.shoppingcart.dao;

import java.sql.PreparedStatement;
import java.util.Locale;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.exception.InvalidCustomerException;

@Repository
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Customer> memberRowMapper = (rs, rn) -> new Customer(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("username")
    );


    public CustomerDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Long save(final Customer customer) {
        final String sql = "INSERT INTO CUSTOMER(email, password, username) values(?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, customer.getEmail());
            preparedStatement.setString(2, customer.getPassword());
            preparedStatement.setString(3, customer.getUsername());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Optional<Customer> findById(final Long createdMemberId) {
        final String sql = "SELECT id, email, password, username FROM CUSTOMER WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, memberRowMapper, createdMemberId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByEmail(final String email) {
        final String query = "SELECT id, email, password, username FROM customer WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, memberRowMapper, email));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long findByUsername(final String username) {
        final String query = "SELECT id FROM customer WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(query, Long.class, username.toLowerCase(Locale.ROOT));
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidCustomerException();
        }
    }
}

