package org.example.exportservice.transaction;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<Transaction> {

    public static final String AMOUNT_COLUMN = "amount";

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setAmount(rs.getBigDecimal(AMOUNT_COLUMN));

        return transaction;
    }
}
