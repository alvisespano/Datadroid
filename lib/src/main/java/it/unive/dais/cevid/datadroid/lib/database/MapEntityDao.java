package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

@Dao
public interface MapEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntities(MapEntity... entities);


}
