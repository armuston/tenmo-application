package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal getBalance(int id);

    boolean transferTo(Transfer newTransfer);

    List<Transfer> getHistory(int id);

    boolean requestBucks(Transfer newTransfer);

    List<Transfer> getPending(int id);

    boolean updatePending(Transfer transfer);
}
