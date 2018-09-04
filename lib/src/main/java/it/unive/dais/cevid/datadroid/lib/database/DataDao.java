package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.lang.reflect.ParameterizedType;

import java.util.List;

@Dao
public abstract class DataDao<T extends MapEntity> {
    @Insert
    /*Insert a Element data of type T on the table T*/
     public abstract void insert(T data);

    @Insert
    /*Insert all the elements of type T of the array data on the table T*/
     public abstract void insert(T[] data);

    @Delete
    /*Delete data from the table T*/
     public abstract void delete(T data);
     /*open the database
    @Query("SELECT * from mapentity where 1 = 0")
    public abstract void open();
    //@RawQuery
    //protected abstract List<T> doFindAll(SupportSQLiteQuery query);
    /**
     * @return String
     * * return the name of the table.
     *   For convention, table name = class name of Generics T
     */



}
