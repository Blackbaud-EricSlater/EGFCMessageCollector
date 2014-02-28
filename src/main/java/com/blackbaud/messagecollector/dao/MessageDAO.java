package com.blackbaud.messagecollector.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface MessageDAO {

    @SqlQuery("SELECT count(1) FROM Message WHERE twilioId=:twilioId")
    public int doesMessageExist(@Bind("twilioId") String twilioId);

    @SqlUpdate("INSERT INTO Message (twilioId) VALUES (:twilioId)")
    public void insertIntoMessage(@Bind("twilioId") String twilioId);

}