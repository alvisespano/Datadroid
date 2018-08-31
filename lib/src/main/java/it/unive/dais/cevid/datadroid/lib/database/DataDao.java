package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.lang.reflect.ParameterizedType;
import java.nio.channels.Selector;
import java.util.List;

@Dao
public abstract class DataDao<T extends MapEntity> {
    @Insert
    /*Insert a Element data of type T on the table T*/
     abstract void insert(T data);

    @Insert
    /*Insert all the elements of type T of the array data on the table T*/
     abstract void insert(T[] data);

    @Delete
    /*Delete data from the table T*/
     abstract void delete(T data);
    @RawQuery
    protected abstract List<T> doFindAll(SupportSQLiteQuery query);
    /**
     * @return String
     * * return the name of the table.
     *   For convention, table name = class name of Generics T
     */
    private String getTableName() {
        Class clas = (Class)
                ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        String tableName = clas.getSimpleName();
        return tableName;
    }

    public List<T> findAll() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "select * from " + getTableName()
        );
        return doFindAll(query);
    }


}
