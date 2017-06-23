/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.douwe.banque.dao.mongo;

import com.douwe.banque.dao.DataAccessException;
import com.douwe.banque.dao.IUserDao;
import com.douwe.banque.data.RoleType;
import com.douwe.banque.data.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author guidona
 */
public class UserDaoMongo implements IUserDao{
    
    static{
        DBCollection collection = MongoConnection.getConnection("User");
        if(collection.count() == 0){
            User user = new User();
            user.setId(1);
            user.setLogin("admin");
            user.setPassword("admin");
            user.setRole(RoleType.admin);
            user.setStatus(0);
            DBObject object = new BasicDBObject("_id", user.getId())
                                               .append("login", user.getLogin())
                                               .append("password", user.getPassword())
                                               .append("role", 2)
                                               .append("status", user.getStatus());
            collection.insert(object);
        }
    }

    @Override
    public User save(User user) throws DataAccessException {
        DBCollection collection = MongoConnection.getConnection("User");
        user.setId(MongoConnection.getIdentifier(collection));
        DBObject object = new BasicDBObject("_id", user.getId())
                                           .append("login", user.getLogin())
                                           .append("password", user.getPassword())
                                           .append("role", roleTypeToInt(user.getRole()))
                                           .append("status", user.getStatus());
        collection.insert(object);
        return user;
    }

    @Override
    public void delete(User user) throws DataAccessException {
        DBObject query = new BasicDBObject("_id", user.getId());
        MongoConnection.getConnection("User").remove(query);
    }

    @Override
    public User update(User user) throws DataAccessException {
        DBObject object = new BasicDBObject("login", user.getLogin())
                                           .append("password", user.getPassword())
                                           .append("role", roleTypeToInt(user.getRole()))
                                           .append("status", user.getStatus());
        MongoConnection.getConnection("User").update(new BasicDBObject("_id", user.getId()), new BasicDBObject("$set", object));
        return user;
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        return find(MongoConnection.getConnection("User").find(new BasicDBObject("_id", id)));
    }

    @Override
    public List<User> findAll() throws DataAccessException {
        List<User> result = new ArrayList<>();
        DBCursor cursor = MongoConnection.getConnection("User").find();
        if(cursor.count() > 0){
            for (DBObject dBObject : cursor) {
                User user = new User();
                user.setId((Integer) dBObject.get("_id"));
                user.setLogin((String) dBObject.get("login"));
                user.setPassword((String) dBObject.get("password"));
                user.setRole(intToRoleType((int) dBObject.get("role")));
                user.setStatus((int) dBObject.get("status"));
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public User findByLogin(String login) throws DataAccessException {
        return find(MongoConnection.getConnection("User").find(new BasicDBObject("login", login)));
    }

    @Override
    public List<User> findByStatus(int status) throws DataAccessException {
        List<User> result = new ArrayList<>();
        DBCursor cursor = MongoConnection.getConnection("User").find(new BasicDBObject("status", status));
        if(cursor.count() > 0){
            for (DBObject dBObject : cursor) {
                User user = new User();
                user.setId((Integer) dBObject.get("_id"));
                user.setLogin((String) dBObject.get("login"));
                user.setPassword((String) dBObject.get("password"));
                user.setRole(intToRoleType((int) dBObject.get("role")));
                user.setStatus((int) dBObject.get("status"));
                result.add(user);
            }
        }
        return result;
    }
    
    private RoleType intToRoleType(int i){
        return (i == 0) ? RoleType.customer : (i == 1) ? RoleType.employee : RoleType.admin;
    }
    
    private int roleTypeToInt(RoleType role){
        return (role == RoleType.customer) ? 0 : (role == RoleType.employee) ? 1 : 2;
    }
    
    private User find(DBCursor cursor){
        if(cursor.count() == 0){
            return null;
        }
        User user = new User();
        for (DBObject dBObject : cursor) {
            user.setId((int) dBObject.get("_id"));
            user.setLogin((String) dBObject.get("login"));
            user.setPassword((String) dBObject.get("password"));
            user.setRole(intToRoleType((int) dBObject.get("role")));
            user.setStatus((int) dBObject.get("status"));
        }
        return user;
    }
}
