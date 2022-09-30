package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getTransferTypeById(int id) {
        String sql = "select transfer_type_desc from transfer_type where transfer_type_id = ?;";

        return jdbcTemplate.queryForObject(sql, String.class, id);
    }

    @Override
    public String getTransferStatusById(int id) {
        String sql = "select transfer_status_desc from transfer_status where transfer_status_id = ?;";

        return jdbcTemplate.queryForObject(sql, String.class, id);
    }


    @Override
    public List<Transfer> listTransfersByAccountId(Long accountId) {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "select * from transfer where account_from = ? OR account_to = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

        while(result.next()) {
            transfers.add(mapRowToTransfer(result));
        }

        return transfers;
    }

    @Override
    public Transfer getTransferById(Long id) {
        Transfer transfer = null;

        String sql = "select * from transfer where transfer_id = ?;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }

        return transfer;
    }

    @Override
    @Transactional
    public Long sendMoney(BigDecimal amount, Long sendId, Long receiveId) {
        try {
            String firstAccountsql = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";

            jdbcTemplate.update(firstAccountsql, amount, sendId);

            String secondAccountSql = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";

            jdbcTemplate.update(secondAccountSql, amount, receiveId);

            String transferSql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?) RETURNING transfer_id;";

            return jdbcTemplate.queryForObject(transferSql, Long.class, sendId, receiveId, amount);

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setId(result.getLong("transfer_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setAccountFrom(result.getLong("account_from"));
        transfer.setAccountTo(result.getLong("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));
        return transfer;
    }



}
