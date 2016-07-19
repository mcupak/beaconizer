package com.dnastack.beacon.beaconizer.dao.api;

import com.dnastack.beacon.beaconizer.exceptions.BeaconNotFoundException;
import com.dnastack.beacon.beaconizer.util.BeaconRequester;

/**
 * Created by patrickmagee on 2016-07-20.
 */
public interface BeaconizerDao {

    BeaconRequester find(String name) throws BeaconNotFoundException;

    java.util.List<BeaconRequester> list();


}
