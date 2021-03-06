package booking.service.impl;

import booking.domain.Account;
import booking.repository.AccountDao;
import booking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static booking.exception.BookingExceptionFactory.accountNotFoundByUserId;
import static booking.exception.BookingExceptionFactory.notFoundById;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account create(Account account) {
        return accountDao.create(account);
    }

    @Override
    public Account getById(long accountId) {
        return accountDao.getById(accountId).orElseThrow(() -> notFoundById(Account.class, accountId));
    }

    @Override
    public Account getByUserId(long userId) {
        return accountDao.getByUserId(userId).orElseThrow(() -> accountNotFoundByUserId(userId));
    }

    @Override
    public Account withdrawal(Account account, BigDecimal withdrawalAmount) {
        BigDecimal newAmount = account.getAmount().subtract(withdrawalAmount);
        account.setAmount(newAmount);
        return accountDao.update(account);
    }

    @Override
    public Account refill(Account account, BigDecimal refillAmount) {
        BigDecimal newAmount = account.getAmount().add(refillAmount);
        account.setAmount(newAmount);
        return accountDao.update(account);
    }

    @Override
    public List<Account> getAll() {
        return accountDao.getAll();
    }

    @Override
    public void delete(long accountId) {
        accountDao.deleteById(accountId);
    }

}
