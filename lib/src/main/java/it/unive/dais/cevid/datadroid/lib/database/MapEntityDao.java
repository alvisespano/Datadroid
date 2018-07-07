package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MapEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntities(MapEntity... entities);
    @Query("SELECT * FROM mapentities")
    MapEntity[] getAll();
    @Query("SELECT * FROM mapentities WHERE id = :id")
    MapEntity getById(String id);
    @Query("DELETE FROM mapentities")
    void deleteAll();
    @Query("SELECT count(*) FROM mapentities")
    int getSize();
}
