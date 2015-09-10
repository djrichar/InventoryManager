package com.djrichar;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * TODO move Objects to JPA
 */
public class DataStore {
    private static final Logger log = LoggerFactory.getLogger(DataStore.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){return sessionFactory;}
    /**
     * Start the Hibernate ORM and generate the tables in the H2 DB
     */
    public static void initialize() {
        StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();

        Metadata metadata = new MetadataSources(standardServiceRegistry)
                .getMetadataBuilder()
                .build();

        sessionFactory = metadata.buildSessionFactory();
    }

    /**
     * close the sessionFactory
     */
    public static void destory() {
        sessionFactory.close();
    }

    /**
     * query the DB for the Inventory Item with the given Name
     *
     * @param key
     * @return Item found or null if not found
     * @throws DataStoreException an error against the DataStore
     */
    public <T> T lookUpItem(Class<T> tClass, Serializable key){
        try (Session session = sessionFactory.openSession();) {
            return session.byId(tClass).load(key);
        }
    }

    public <T> T save(T o){
        try (Session session = sessionFactory.openSession();) {
            return (T)session.merge(o);
        }
    }

}
