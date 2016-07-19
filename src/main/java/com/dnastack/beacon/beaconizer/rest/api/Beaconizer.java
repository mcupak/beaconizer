package com.dnastack.beacon.beaconizer.rest.api;

import org.ga4gh.beacon.BeaconAlleleRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Beaconizer API
 */
public interface Beaconizer {

    /**
     * List all the beacons that are currently sup
     *
     * @return
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response listBeacons();


    @GET
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    Response searchAllBeacons(@QueryParam("referenceName") String referenceName, @QueryParam("start") Long start, @QueryParam("referenceBases") String referenceBases, @QueryParam("alternateBases") String alternateBases, @QueryParam("assemblyId") String assemblyId, @QueryParam("datasetIds") List<String> datasetIds, @QueryParam("includeDatasetResponses") Boolean includeDatasetResponses);


    @POST
    @Path("query")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    Response searchAllBeacons(BeaconAlleleRequest request);

    @GET
    @Path("{name}")
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeacon(@PathParam("name") String name);


    @GET
    @Path("{name}/query")
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeaconResponse(@PathParam("name") String name, @QueryParam("referenceName") String referenceName, @QueryParam("start") Long start, @QueryParam("referenceBases") String referenceBases, @QueryParam("alternateBases") String alternateBases, @QueryParam("assemblyId") String assemblyId, @QueryParam("datasetIds") List<String> datasetIds, @QueryParam("includeDatasetResponses") Boolean includeDatasetResponses);


    @POST
    @Path("{name}/query")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeaconResponse(@PathParam("name") String name, BeaconAlleleRequest request);

}
