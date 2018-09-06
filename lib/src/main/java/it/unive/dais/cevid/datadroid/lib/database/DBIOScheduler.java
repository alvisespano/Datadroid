package it.unive.dais.cevid.datadroid.lib.database;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.database.event.IOEvent;

class DBIOScheduler extends Thread {
    private List<Pair<IOEvent, Object[]>> queue = new ArrayList<>();
    private List<Pair<IOEvent, Object[]>> secondaryQueue = new ArrayList<>();
    private DBManager db;
    private final String TAG = "DBIOScheduler";


    enum Priority{
        HIGH,
        LOW
    }
    DBIOScheduler(DBManager db) {
        this.db = db;
    }

    synchronized void insert(IOEvent e, Object... params) {
            queue.add(new Pair<>(e, params));
            //Log.i(TAG, "Inserting Event...");
            notifyAll();
    }
    synchronized void insert(IOEvent e, Priority priority, Object... params) {
        if(priority == Priority.HIGH){
            queue.add(0, new Pair<>(e, params));
            //Log.i(TAG, "Inserting Event with HIGH PRIORITY... "+e.toString());
            notifyAll();
        }
        else
            insert(e, params);
    }
    private void process() {
        if (queue != null) {
            Pair<IOEvent, Object[]> nextEvent = queue.remove(0);
            synchronized(nextEvent.first){
                //Log.i(TAG, "Executing next event... "+nextEvent.first.toString());
                nextEvent.first.execute(nextEvent.second);
                nextEvent.first.notifyAll();
                //Log.i(TAG, "Executed...");
            }
        }
    }

    @Override
    public void run(){
        //Log.i(TAG, "DBIOScheduler started...");
        try {
            while(true) {
                while (queue.isEmpty()) {
                    synchronized (this) {
                        //Log.i(TAG, "Waiting for Events...");
                        wait();
                    }
                }
                //Log.i(TAG, "Processing event... ");
                process();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
