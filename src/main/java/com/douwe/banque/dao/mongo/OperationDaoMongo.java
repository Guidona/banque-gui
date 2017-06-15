/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.douwe.banque.dao.mongo;

import com.douwe.banque.dao.DataAccessException;
import com.douwe.banque.dao.IOperationDao;
import com.douwe.banque.data.Customer;
import com.douwe.banque.data.Operation;
import com.douwe.banque.data.OperationType;
import com.douwe.banque.data.RoleType;
import com.douwe.banque.data.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author guidona
 */
public class OperationDaoMongo implements IOperationDao{

    @Override
    public Operation save(Operation operation) throws DataAccessException {
        DBCollection collection = MongoConnection.getConnection("Operation");
        operation.setId((int)(long)collection.count() + 1);
        DBObject dbo = new BasicDBObject("_id", operation.getId())
                                        .append("date_operation", operation.getDateOperation())
                                        .append("description", operation.getDescription())
                                        .append("type", operationTypeToInt(operation.getType()))
                                        .append("accountId", operation.getAccount().getId())
                                        .append("userId", operation.getUser().getId());
        collection.insert(dbo);
        return operation;
    }

    @Override
    public void delete(Operation operation) throws DataAccessException {
        DBObject dbo = new BasicDBObject("_id", operation.getId());
        MongoConnection.getConnection("Operation").remove(dbo);
    }

    @Override
    public Operation update(Operation operation) throws DataAccessException {
        DBObject dbo = new BasicDBObject("date_operation", operation.getDateOperation())
                                        .append("description", operation.getDescription())
                                        .append("type", operationTypeToInt(operation.getType()))
                                        .append("accountId", operation.getAccount().getId())
                                        .append("userId", operation.getUser().getId());
        MongoConnection.getConnection("Operation").update(new BasicDBObject("_id", operation.getId()), new BasicDBObject("$set", dbo));
        return operation;
    }

    @Override
    public Operation findById(Integer id) throws DataAccessException {
        DBCursor cursor = MongoConnection.getConnection("Operation").find(new BasicDBObject("_id", id));
        Operation operation = new Operation();
        for(DBObject dBObject : cursor){
            operation.setId((Integer) dBObject.get("_id"));
            operation.setDateOperation((Date) dBObject.get("date_operation"));
            operation.setDescription((String) dBObject.get("description"));
            operation.setType(intToOperationType((int) dBObject.get("type")));
            
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
        return operation;
    }

    @Override
    public List<Operation> findAll() throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Operation> findForCustomer(Customer customer) throws DataAccessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private RoleType intToRoleType(int i){
        return (i == 0) ? RoleType.customer : (i == 1) ? RoleType.employee : RoleType.admin;
    }
    
    private int roleTypeToInt(RoleType role){
        return (role == RoleType.customer) ? 0 : (role == RoleType.employee) ? 1 : 2;
    }
    
    private int operationTypeToInt(OperationType operation){
        if(operation == OperationType.credit){
            return 0;
        }
        if(operation == OperationType.debit){
            return 1;
        }
        return (operation == OperationType.transfer)? 2 : 3;
    }
    
    private OperationType intToOperationType(int i){
        switch(i){
            case 0:
                return OperationType.credit;
            case 1:
                return OperationType.debit;
            case 2:
                return OperationType.transfer;
            default:
                return OperationType.cloture;
        }
    }
}