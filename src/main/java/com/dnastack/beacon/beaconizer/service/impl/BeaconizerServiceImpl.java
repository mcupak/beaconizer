package com.dnastack.beacon.beaconizer.service.impl;

import com.dnastack.beacon.beaconizer.dao.api.BeaconizerDao;
import com.dnastack.beacon.beaconizer.exceptions.BeaconException;
import com.dnastack.beacon.beaconizer.service.api.BeaconizerService;
import com.dnastack.beacon.beaconizer.util.BeaconRequester;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by patrickmagee on 2016-07-19.
 */
@Singleton
public class BeaconizerServiceImpl implements BeaconizerService {

    @Inject
    BeaconizerDao dao;

    @Override
    public List<Beacon> getBeacons() throws BeaconException {
        List<Beacon> beaconList = new ArrayList<>();

        for (BeaconRequester beacon : dao.list()) {
            beaconList.add(beacon.getBeacon());
        }
        return beaconList;
    }


    @Override
    public Beacon getBeacon(String name) throws BeaconException {
        return dao.find(name).getBeacon();

    }


    @Override
    public List<BeaconAlleleResponse> getAllBeaconAlleleResponses(String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException {
        List<BeaconAlleleResponse> beaconResponseList = new ArrayList<>();
        for (BeaconRequester beacon : dao.list()) {

            BeaconAlleleResponse response = getBeaconAlleleResponse(beacon
                    .getBeaconDTO()
                    .getName(), referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses);

            beaconResponseList.add(response);
        }
        return beaconResponseList;
    }

    @Override
    public List<BeaconAlleleResponse> getAllBeaconAlleleResponse(BeaconAlleleRequest request) throws BeaconException {
        List<BeaconAlleleResponse> beaconResponseList = new ArrayList<>();
        for (BeaconRequester beacon : dao.list()) {

            BeaconAlleleResponse response = getBeaconAlleleResponse(beacon.getBeaconDTO().getName(), request);
            beaconResponseList.add(response);
        }
        return beaconResponseList;
    }

    @Override
    public BeaconAlleleResponse getBeaconAlleleResponse(String name, String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException {
        validateRequest(referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds);
        return dao.find(name)
                  .getBeaconResponse(referenceName, start, referenceBases, alternateBases, assemblyId, datasetIds, includeDatasetResponses);
    }

    @Override
    public BeaconAlleleResponse getBeaconAlleleResponse(String name, BeaconAlleleRequest request) throws BeaconException {
        validateRequest(request.getReferenceName(), request.getStart(), request.getReferenceBases(), request.getAlternateBases(), request
                .getAssemblyId(), request.getDatasetIds());
        return dao.find(name).getBeaconResponse(request);
    }


    /**
     * Validate the beacon fields according to the 0.3.0 beacon specifications
     *
     * @param referenceName
     * @param start
     * @param referenceBases
     * @param alternateBases
     * @param assemblyId
     * @throws BeaconException
     */
    private void validateRequest(String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds) throws BeaconException {
        if (referenceName == null) {
            throw new BeaconException("Reference cannot be null. Please provide an appropriate reference name");
        } else if (start == null) {
            throw new BeaconException("Start position cannot be null. Please provide a 0-based start position");
        } else if (referenceBases == null) {
            throw new BeaconException("Reference bases cannot be null");
        } else if (alternateBases == null) {
            throw new BeaconException("Alternate bases cannot be null");
        } else if (assemblyId == null) {
            throw new BeaconException("AssemblyId cannot be null. Please defined a valid GRCh assembly Id");
        } else if (!assemblyId.startsWith("GRCh")) {
            throw new BeaconException("Invalid assemblyId. Assemblies must be from GRCh builds");
        } else if (datasetIds == null || datasetIds.size() == 0 ) {
            throw new BeaconException("Missing DatasetId. At least 1 dataset id must be provided");
        }
    }


}
