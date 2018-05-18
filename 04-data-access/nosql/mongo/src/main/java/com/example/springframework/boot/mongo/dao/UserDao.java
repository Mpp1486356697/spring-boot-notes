package com.example.springframework.boot.mongo.dao;

import com.example.springframework.boot.mongo.config.page.PageInfo;
import com.example.springframework.boot.mongo.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /* insert */

    public String save(User user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        mongoTemplate.save(user);
        return id;
    }

    public List<String> batchSave(List<User> users) {
        List<String> ids = new ArrayList<>();
        users.forEach(user -> {
            String id = UUID.randomUUID().toString();
            user.setId(id);
            ids.add(id);
        });
        mongoTemplate.insert(users, User.class);
        return ids;
    }

    /* select */

    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    public User findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    public List<User> findByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.find(query, User.class);
    }

    public PageInfo<User> pageQuery(Integer pageNum, Integer pageSize) {
        Query query = new Query();
        //注意：页码从0开始
        query.with(PageRequest.of(pageNum - 1, pageSize));
        List<User> users = mongoTemplate.find(query, User.class);
        long count = mongoTemplate.count(query, User.class);
        return new PageInfo<>(pageNum, pageSize, users, (int) count);
    }

    public List<User> findSort(Sort.Order... orders) {
        Query query = new Query();
        query.with(Sort.by(orders));
        return mongoTemplate.find(query, User.class);
    }

    /* update */

    public Long update(User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(user.getId()));
        Update update = new Update();
        update.set("username", user.getUsername());
        update.set("password", user.getPassword());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
        return updateResult.getModifiedCount();
    }

    public Integer batchUpdate(List<User> users) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, User.class);
        List<Pair<Query, Update>> pairs = new ArrayList<>();
        for (User user : users) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(user.getId()));
            Update update = new Update();
            update.set("username", user.getUsername());
            update.set("password", user.getPassword());
            Pair<Query, Update> pair = Pair.of(query, update);
            pairs.add(pair);
        }
        bulkOps.updateMulti(pairs);
        BulkWriteResult execute = bulkOps.execute();
        return execute.getModifiedCount();
    }


    /* delete */

    public long delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        DeleteResult deleteResult = mongoTemplate.remove(query, User.class);
        return deleteResult.getDeletedCount();
    }

    public long batchDelete(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        DeleteResult deleteResult = mongoTemplate.remove(query, User.class);
        return deleteResult.getDeletedCount();
    }

}
