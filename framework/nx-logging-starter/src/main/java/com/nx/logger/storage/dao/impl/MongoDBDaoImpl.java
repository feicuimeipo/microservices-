package com.nx.logger.storage.dao.impl;

import com.alibaba.fastjson2.JSONObject;
import com.mongodb.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nx.logger.storage.NxLoggerModel;
import com.nx.logger.storage.dao.MongoDBDao;
import org.bson.Document;

/*
 * mongodb数据库链接池
 */
public class MongoDBDaoImpl implements MongoDBDao {
    private MongoClient mongoClient = null;
    private String databaseName;

    public MongoDBDaoImpl(String host, int port, String username, String password, String dbname)
    {

        if(mongoClient==null){

            this.databaseName = dbname;

            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            //设置每个连接地址的最大连接数
            builder.connectionsPerHost(10);
            //设置连接的超时时间
            builder.connectTimeout(5000);
            //设置读写的超时时间
            builder.socketTimeout(5000);
            //创建一个用户认证信息
            MongoCredential credential = MongoCredential.createCredential(username,password,dbname.toCharArray());
            //封装MongoDB的地址和端口

            ServerAddress address = new ServerAddress(host,port);
            mongoClient = new MongoClient(address,credential,builder.build());
        }
    }


    /**
     * 连接默认的数据库
     * @return
     */
    @Override
    public MongoDatabase getDatabase(){
        return mongoClient.getDatabase(databaseName);
    }

    //获取Mongo集合
    @Override
    public MongoCollection<Document> getCollection(String collectionName){
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        if (collection==null){
            getDatabase().createCollection(collectionName);
            return getDatabase().getCollection(collectionName);
        }
        return collection;
    }


    @Override
    public void insert(String collectionName,  NxLoggerModel values)
    {
        //String message = LogJsonUtil.toJsonString(values);
        String message = JSONObject.toJSONString(values);

        insert(collectionName,message);

    }


    @Override
    public void insert(String collectionName,  String jsonMessage)
    {
        Document document =Document.parse(jsonMessage);

        MongoCollection<Document> collection = getCollection(collectionName);

        collection.insertOne(document);

    }




//    @Override
//    public boolean isExit(String dbName, String collectionName, String key, Object value)
//    {
//        DB db = mongoClient.getDB(dbName);
//        DBCollection dbCollection = db.getCollection(collectionName);
//        BasicDBObject doc = new BasicDBObject();
//        doc.put(key, value);
//        if (dbCollection.count(doc) > 0)
//        {
//            return true;
//        }
//
//        return false;
//    }

//    public static void main(String args[])
//    {
//        MongoDBDaoImpl mongoDBDaoImpl=MongoDBDaoImpl.getMongoDBDaoImpl();
//        ArrayList<DBObject> list=new ArrayList<DBObject>();
//        list=mongoDBDaoImpl.find("jd", "phone",-1);
//        System.out.println(list.size());
//    }




}