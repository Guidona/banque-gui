/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.douwe.banque.dao.mongo;

import com.douwe.banque.dao.DataAccessException;
import com.douwe.banque.dao.ICustomerDao;
import com.douwe.banque.data.Account;
import com.douwe.banque.data.Customer;
import com.douwe.banque.data.RoleType;
import com.douwe.banque.data.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author guidona
 */
public class CustomerDaoMongo implements ICustomerDao{

    @Override
    public Customer save(Customer customer) throws DataAccessException {
        DBCollection collection = MongoConnection.getConnection("Customer");
        customer.setId((int) (long) collection.count() + 1);
        DBObject obj = new BasicDBObject("_id", customer.getId())
                                        .append("name", customer.getName())
                                        .append("email", customer.getEmailAddress())
                                        .append("phone", customer.getPhoneNumber())
                                        .append("status", customer.getStatus())
                                        .append("userId", customer.getUser().getId());
        collection.insert(obj);
        return customer;
    }

    @Override
    public void delete(Customer customer) throws DataAccessException {
        DBObject query = new BasicDBObject("_id", customer.getId());
        MongoConnection.getConnection("Customer").remove(query);
    }

    @Override
    public Customer update(Customer customer) throws DataAccessException {
        DBObject obj = new BasicDBObject("name", customer.getName())
                                        .append("email", customer.getEmailAddress())
                                        .append("phone", customer.getPhoneNumber())
                                        .append("status", customer.getStatus())
                                        .append("userId", customer.getUser().getId());
        MongoConnection.getConnection("Customer").update(new BasicDBObject("_id", customer.getId()), new BasicDBObject("$set", obj));
        return customer;
    }

    @Override
    public Customer findById(Integer id) throws DataAccessException {
        return find(MongoConnection.getConnection("Customer").find(new BasicDBObject("_id", id)));
    }

    @Override
    public List<Customer> findAll() throws DataAccessException {
        List<Customer> result = new ArrayList<>();
        DBCursor cursor = MongoConnection.getConnection("Customer").find();
        for(DBObject dBObject : cursor){
            Customer customer = new Customer();
            customer.setId((Integer) dBObject.get("_id"));
            customer.setName((String) dBObject.get("namer"));
            customer.setEmailAddress((String) dBObject.get("email"));
            customer.setPhoneNumber((String) dBObject.get("phone"));
            customer.setStatus((Integer) dBObject.get("status"));
            
            DBObject query = new BasicDBObject("_id", (ObjectId) dBObject.get("userId"));
            DBCursor cursor1 = MongoConnection.getConnection("User").find(query);
            for (DBObject dBObject1 : cursor1) {
                User user = new User();
                user.setId((Integer) dBObject1.get("_id"));
                user.setLogin((String) dBObject1.get("login"));
                user.setPassword((String) dBObject1.get("password"));
                user.setRole(intToRoleType((int) dBObject1.get("type")));
                user.setStatus((int) dBObject1.get("status"));
            }
            result.add(customer);
        }
        return result;
    }

    @Override
    public Customer findByUser(User usr) throws DataAccessException {
        return find(MongoConnection.getConnection("Customer").find(new BasicDBObject("_id", usr.getId())));
    }

    @Override
    public Customer findByName(String name) throws DataAccessException {
        return find(MongoConnection.getConnection("Customer").find(new BasicDBObject("name", name)));
    }
    
    private RoleType intToRoleType(int i){
        return (i == 0) ? RoleType.customer : (i == 1) ? RoleType.employee : RoleType.admin;
    }
    
    private int roleTypeToInt(RoleType role){
        return (role == RoleType.customer) ? 0 : (role == RoleType.employee) ? 1 : 2;
    }
    
    private Customer find(DBCursor cursor){
        Customer customer = new Customer();
        for(DBObject dBObject : cursor){
            customer.setId((Integer) dBObject.get("_id"));
            customer.setName((String) dBObject.get("namer"));
            customer.setEmailAddress((String) dBObject.get("email"));
            customer.setPhoneNumber((String) dBObject.get("phone"));
            customer.setStatus((Integer) dBObject.get("status"));
            
            DBObject query = new BasicDBObject("_id", (ObjectId) dBObject.get("userId"));
            DBCursor cursor1 = MongoConnection.getConnection("User").find(query);
            for (DBObject dBObject1 : cursor1) {
                User user = new User();
                user.setId((Integer) dBObject1.get("_id"));
                user.setLogin((String) dBObject1.get("login"));
                user.setPassword((String) dBObject1.get("password"));
                user.setRole(intToRoleType((int) dBObject1.get("type")));
                user.setStatus((int) dBObject1.get("status"));
            }
        }
        return customer;
    }
}
