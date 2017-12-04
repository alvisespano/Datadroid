package it.unive.dais.cevid.aac.util;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by fbusolin on 04/12/17.
 */

public class ListItem {
    private MapItem item;

    public <I extends MapItem> ListItem(I item) {
        this.item = item;
    }

    public MapItem getItem() {
        return item;
    }

    public String toString(){
        return item.getTitle();
    }
}
