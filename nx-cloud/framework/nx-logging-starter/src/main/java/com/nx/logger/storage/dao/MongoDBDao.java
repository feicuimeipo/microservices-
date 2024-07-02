package com.nx.logger.storage.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nx.logger.storage.NxLoggerModel;

public interface MongoDBDao {


    /**
     *
     * 方法名：getCollection
     * 作者：lky
     * 描述：获取指定mongodb数据库的collection集合
     * @param collectionName    数据库集合名
     * @return
     */
    public MongoCollection getCollection( String collectionName);

    MongoDatabase getDatabase();

    /**
     *
     * 方法名：inSert
     * 作者：lky
     * 描述：向指定的数据库中添加给定的keys和相应的values
     * @param collectionName
     * @param values
     * @return
     */
    public void insert(String collectionName,  NxLoggerModel values);

    void insert(String collectionName, String jsonMessage);
}
