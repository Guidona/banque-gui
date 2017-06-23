/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.douwe.banque.dao.mongo;

import com.douwe.banque.dao.DataAccessException;
import com.douwe.banque.dao.IAccountDao;
import com.douwe.banque.data.Account;
import com.douwe.banque.data.AccountType;
import com.douwe.banque.data.Customer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author guidona
 */
public class AccountDaoMongo implements IAccountDao{
    @Override
    public Account save(Account account){
        DBCollection collection = MongoConnection.getConnection("Account");
        account.setId(MongoConnection.getIdentifier(collection));
        DBObject obj = new BasicDBObject("_id", account.getId())
                                        .append("accountNumber", account.getAccountNumber())
                                        .append("balance", account.getBalance())
                                        .append("date", new Date())
                                        .append("type", AccountTypeToInt(account.getType()))
                                        .append("status", account.getStatus())
                                        .append("customerId", account.getCustomer().getId());
        collection.insert(obj);
        return account;
    }

    @Override
    public void delete(Account account) throws DataAccessException {
        DBObject query = new BasicDBObject("_id", account.getId());
        MongoConnection.getConnection("Account").remove(query);
    }

    @Override
    public Account update(Account account) throws DataAccessException {
        DBObject obj = new BasicDBObject("accountNumber", account.getAccountNumber())
                                        .append("balance", account.getBalance())
                                        .append("date", new Date())
                                        .append("type", AccountTypeToInt(account.getType()))
                                        .append("customerId", account.getCustomer().getId());
        MongoConnection.getConnection("Account").findAndModify(new BasicDBObject("_id", account.getId()), new BasicDBObject("$set", obj));
        return account;
    }

    @Override
    public List<Account> findAll() throws DataAccessException {
        List<Account> result = new ArrayList<>();
        DBCursor cursor = MongoConnection.getConnection("Account").find();
        if(cursor.count() == 0){
            return result;
        }
        for(DBObject dBObject : cursor){
            Account account = new Account();
            account.setId((Integer) dBObject.get("_id"));
            account.setAccountNumber((String) dBObject.get("accountNumber"));
            account.setBalance((double) dBObject.get("balance"));
            account.setDateDeCreation((Date) dBObject.get("date"));
            account.setType(intToAccountType((Integer) dBObject.get("type")));
            
            DBObject query = new BasicDBObject("_id", (Integer) dBObject.get("customerId"));
            DBCursor cursor1 = MongoConnection.getConnection("Customer").find(query);
            for (DBObject dBObject1 : cursor1) {
                Customer customer = new Customer();
                customer.setId((Integer) dBObject1.get("_id"));
                customer.setName((String) dBObject1.get("name"));
                customer.setEmailAddress((String) dBObject1.get("email"));
                customer.setPhoneNumber((String) dBObject1.get("phoneNumber"));
                customer.setStatus((Integer) dBObject1.get("status"));
                account.setCustomer(customer);
            }
            result.add(account);
        }
        return result;
    }

    @Override
    public Account findById(Integer id) throws DataAccessException {
        return find(MongoConnection.getConnection("Account").find(new BasicDBObject("_id", id)));
    }

    @Override
    public Account findByAccountNumber(String accountNumber) throws DataAccessException {
        return find(MongoConnection.getConnection("Account").find(new BasicDBObject("accountNumber", accountNumber)));
    }

    @Override
    public List<Account> findByCustomer(Customer cust) throws DataAccessException {
        List<Account> result = new ArrayList<>();
        DBObject connection = new BasicDBObject("customerId", cust.getId());
        DBCursor cursor = MongoConnection.getConnection("Account").find(connection);
        if(cursor.count() == 0){
            return result;
        }
        for(DBObject dBObject : cursor){
            Account account = new Account();
            account.setId((Integer) dBObject.get("_id"));
            account.setAccountNumber((String) dBObject.get("accountNumber"));
            account.setBalance((double) dBObject.get("balance"));
            account.setType(intToAccountType((Integer) dBObject.get("type")));
            
            DBObject query = new BasicDBObject("_id", (Integer) dBObject.get("customerId"));
            DBCursor cursor1 = MongoConnection.getConnection("Customer").find(query);
            for (DBObject dBObject1 : cursor1) {
                Customer customer = new Customer();
                customer.setId((Integer) dBObject1.get("_id"));
                customer.setName((String) dBObject1.get("name"));
                customer.setEmailAddress((String) dBObject1.get("email"));
                customer.setPhoneNumber((String) dBObject1.get("phoneNumber"));
                customer.setStatus((Integer) dBObject1.get("status"));
                account.setCustomer(customer);
            }
            result.add(account);
        }
        return result;
    }
    
    //Two utility functions for some abstraction of the account type class
    private AccountType intToAccountType(int i){
        return (i == 0)? AccountType.deposit : AccountType.saving;
    }
    
    private int AccountTypeToInt(AccountType acc){
        return (acc == AccountType.deposit)? 0 : 1;
    }
    
    //Find utility to avoid redundance
    private Account find(DBCursor cursor){
        if(cursor.count() == 0){
            return null;
        }
        Account account = new Account();
        for(DBObject dBObject : cursor){
            account.setId((Integer) dBObject.get("_id"));
            account.setAccountNumber((String) dBObject.get("accountNumber"));
            account.setBalance((double) dBObject.get("balance"));
            account.setType(intToAccountType((Integer) dBObject.get("type")));
            
            DBObject query = new BasicDBObject("_id", (Integer) dBObject.get("customerId"));
            DBCursor cursor1 = MongoConnection.getConnection("Customer").find(query);
            for (DBObject dBObject1 : cursor1) {
                Customer customer = new Customer();
                customer.setId((Integer) dBObject1.get("_id"));
                customer.setName((String) dBObject1.get("name"));
                customer.setEmailAddress((String) dBObject1.get("email"));
                customer.setPhoneNumber((String) dBObject1.get("phone"));
                customer.setStatus((Integer) dBObject1.get("status"));
                account.setCustomer(customer);
            }
        }
        return account;
    }
}
