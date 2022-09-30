package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    Long sendMoney(BigDecimal amount, Long sendId, Long receiveId);

    Transfer getTransferById(Long id);

    List<Transfer> listTransfersByAccountId(Long accountId);

    String getTransferTypeById(int id);

    String getTransferStatusById(int id);
}
