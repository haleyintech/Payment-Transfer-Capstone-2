package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class TenmoController {

    private final AccountDao accountDao;
    private final UserDao userDao;
    private final TransferDao transferDao;

    public TenmoController(AccountDao accountDao, UserDao userDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    //url: http://localhost:8080/account?userid=1001
    @GetMapping(value = "/account")
    public Account getAccountByUserId(@RequestParam long userid) {
        return accountDao.findAccountByUserId(userid);
    }

    @GetMapping(value = "/users")
    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    //url: http://localhost:8080/user?userid=1001
    @GetMapping(value = "/user")
    public User findUserByID(@RequestParam long userid) {
        return userDao.findUserByID(userid);
    }

    //url: http://localhost:8080/username?accountid=2003
    @GetMapping(value = "/username")
    public String getUserNameByAccountId(@RequestParam long accountid) {
        return userDao.findUserNameByAccountId(accountid);
    }

    //url: http://localhost:8080/send?amount=250&sendid=2002&receiveid=2001
    @GetMapping(value = "/send")
    public Long sendMoney(@RequestParam BigDecimal amount, @RequestParam Long sendid, @RequestParam Long receiveid) {
        return transferDao.sendMoney(amount, sendid, receiveid);
    }

    //url: http://localhost:8080/transfers/2003
    @GetMapping(value = "transfers/{accountId}")
    public List<Transfer> getAllTransfers(@PathVariable Long accountId) {
        return transferDao.listTransfersByAccountId(accountId);
    }

    //url: http://localhost:8080/transfer/3002
    @GetMapping(value = "tansfer/{id}")
    public Transfer getTransferById(@PathVariable long id) {
        return transferDao.getTransferById(id);
    }

    @GetMapping(value = "/type/{id}")
    public String getTransferTypeById(@PathVariable int id) {
        return transferDao.getTransferTypeById(id);
    }

    @GetMapping(value = "/status/{id}")
    public String getTransferStatusById(@PathVariable int id) {
        return transferDao.getTransferStatusById(id);
    }

}
