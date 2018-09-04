package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;
import java.util.Map;
@Dao
public abstract class BaseDataDao extends DataDao<MapEntity> {
    @Query("SELECT * FROM MapEntity")
    public abstract List<MapEntity> findAll();

}
