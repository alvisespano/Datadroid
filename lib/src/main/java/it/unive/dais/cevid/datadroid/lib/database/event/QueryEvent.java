package it.unive.dais.cevid.datadroid.lib.database.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QueryEvent implements IOEvent {
    private Method m;
    private Object returningValue;
    private Object obj;
    private Boolean processed = false;
    public QueryEvent(String methodName, Object obj, Class ... args){
        try {
            this.m = obj.getClass().getMethod(methodName, args);
            this.obj = obj;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void execute(Object... params) {
        try {
            returningValue = m.invoke(obj, params);
            processed = true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getResult(){
        return returningValue;
    }

    public Boolean getProcessed() {
        return processed;
    }
}
