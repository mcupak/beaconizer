package com.dnastack.beacon.beaconizer.service.api;

import com.dnastack.beacon.beaconizer.exceptions.BeaconException;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;

import java.util.List;

/**
 * Created by patrickmagee on 2016-07-19.
 */
public interface BeaconizerService {

    List<Beacon> getBeacons() throws BeaconException;

    Beacon getBeacon(String name) throws BeaconException;

    List<BeaconAlleleResponse> getAllBeaconAlleleResponse(BeaconAlleleRequest request) throws BeaconException;

    List<BeaconAlleleResponse> getAllBeaconAlleleResponses(String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException;

    BeaconAlleleResponse getBeaconAlleleResponse(String name, BeaconAlleleRequest request) throws BeaconException;

    BeaconAlleleResponse getBeaconAlleleResponse(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException;
}
