package com.example.dongja94.sampletmap;

import com.skp.Tmap.TMapPOIItem;

/**
 * Created by dongja94 on 2015-10-26.
 */
public class POIItem {
    TMapPOIItem poi;

    @Override
    public String toString() {
        return poi.getPOIName();
    }
}
