/*
 * The MIT License
 *
 * Copyright 2014 Patrick Magee (patrickmageee@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dnastack.beacon.beaconizer.rest.api;

import org.ga4gh.beacon.BeaconAlleleRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Beaconizer Rest-API
 *
 * @author patmagee
 */
public interface Beaconizer {

    /**
     * Return a list of Beacon objects for each beacon that is supported by the current beaconizer. The Beacon objects
     * are returned by each individual beacon and collated into a single collection which is added to the response
     * object.
     *
     * @return Response object
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response listBeacons();


    /**
     * Query all of the supported beacons for the existence of a specific variant. Query each beacon individually then
     * collate all responses into a single list.
     *
     * @param referenceName name of the reference
     * @param start start position
     * @param referenceBases reference bases
     * @param alternateBases alternate bases
     * @param assemblyId genome assembly
     * @param datasetIds list of datasetIds
     * @param includeDatasetResponses include
     * @return Response object
     */
    @GET
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    Response searchAllBeacons(@QueryParam("referenceName") String referenceName, @QueryParam("start") Long start, @QueryParam("referenceBases") String referenceBases, @QueryParam("alternateBases") String alternateBases, @QueryParam("assemblyId") String assemblyId, @QueryParam("datasetIds") List<String> datasetIds, @QueryParam("includeDatasetResponses") Boolean includeDatasetResponses);


    /**
     * Query all of the supported beacons for the existence of a specific variant. Query each beacon individually then
     * collate all responses into a single list.
     *
     * @param request BeaconAlleleRequest object
     * @return Response object
     */
    @POST
    @Path("query")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    Response searchAllBeacons(BeaconAlleleRequest request);

    /**
     * Get information from a specific beacon given the name it was registered under.
     *
     * @param name name of beacon
     * @return Response Object
     */
    @GET
    @Path("{name}")
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeacon(@PathParam("name") String name);

    /**
     * Query a single Beacon for the existence of a variant
     *
     * @param name name of beacon
     * @param referenceName name of the reference
     * @param start start position
     * @param referenceBases reference bases
     * @param alternateBases alternate bases
     * @param assemblyId genome assembly
     * @param datasetIds list of datasetIds
     * @param includeDatasetResponses include
     * @return Response Object
     */
    @GET
    @Path("{name}/query")
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeaconResponse(@PathParam("name") String name, @QueryParam("referenceName") String referenceName, @QueryParam("start") Long start, @QueryParam("referenceBases") String referenceBases, @QueryParam("alternateBases") String alternateBases, @QueryParam("assemblyId") String assemblyId, @QueryParam("datasetIds") List<String> datasetIds, @QueryParam("includeDatasetResponses") Boolean includeDatasetResponses);

    /**
     * Query a single Beacon for the existence of a variant
     *
     * @param name name of beacon
     * @param request request object
     * @return Response object
     */
    @POST
    @Path("{name}/query")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    Response getBeaconResponse(@PathParam("name") String name, BeaconAlleleRequest request);

}
