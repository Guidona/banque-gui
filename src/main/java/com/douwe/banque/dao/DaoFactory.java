package com.douwe.banque.dao;

//import com.douwe.banque.dao.jdbc.AccountDaoJDBC;
//import com.douwe.banque.dao.jdbc.CustomerDaoJDBC;
//import com.douwe.banque.dao.jdbc.OperationDaoJDBC;
//import com.douwe.banque.dao.jdbc.UserDaoJDBC;
import com.douwe.banque.dao.mongo.AccountDaoMongo;
import com.douwe.banque.dao.mongo.CustomerDaoMongo;
import com.douwe.banque.dao.mongo.OperationDaoMongo;
import com.douwe.banque.dao.mongo.UserDaoMongo;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
public class DaoFactory {
    private IAccountDao accountDao;
    
    private ICustomerDao customerDao;
    
    private IOperationDao operationDao;
    
    private IUserDao userDao;
    
    public DaoFactory(){
        accountDao = new AccountDaoMongo();
        customerDao = new CustomerDaoMongo();
        operationDao = new OperationDaoMongo();
        userDao = new UserDaoMongo();
    }

    public IAccountDao getAccountDao() {
        return accountDao;
    }

    public ICustomerDao getCustomerDao() {
        return customerDao;
    }

    public IOperationDao getOperationDao() {
        return operationDao;
    }

    public IUserDao getUserDao() {
        return userDao;
    }
}
