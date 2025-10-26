package org.example;

public interface DaoProvider extends AutoCloseable{

    UserDao getUserDao();

    MessageDao getMessageDao();

    void commit();

    @Override
    void close();

    
}
