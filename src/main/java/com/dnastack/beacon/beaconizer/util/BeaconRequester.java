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
package com.dnastack.beacon.beaconizer.util;

import com.dnastack.beacon.beaconizer.dao.dto.BeaconDTO;
import com.dnastack.beacon.beaconizer.exceptions.BeaconException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.ga4gh.beacon.Beacon;
import org.ga4gh.beacon.BeaconAlleleRequest;
import org.ga4gh.beacon.BeaconAlleleResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URISyntaxException;
import java.util.List;

/**
 * BeaconRequester object handles querying individual beacons with REST request and returning the appropriate response.
 * Each BeaconRequester object is configured for a single beacon which is Beacon V.0.3 compliant based on the
 * BeaconDTO object
 *
 * @author patmagee
 */
public class BeaconRequester {

    private static final String BEACON_PATH = "/";
    private static final String BEACON_QUERY = BEACON_PATH + "query";

    private BeaconDTO beaconDTO;

    private Gson gson;

    /**
     * Create a new instance of a BeaconRequester based on the beaconDTO
     * @param beaconDTO information on a single beacon
     */
    public BeaconRequester(BeaconDTO beaconDTO) {
        this.beaconDTO = beaconDTO;
        gson = new GsonBuilder().create();
    }

    public BeaconDTO getBeaconDTO() {
        return beaconDTO;
    }

    /**
     * Submit a request to a remote beacon server to receive a Beacon Definition objects as defined
     * by the Beacon API, describing the becon.
     * @return Beacon object
     * @throws BeaconException
     */
    public Beacon getBeacon() throws BeaconException {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet get = new HttpGet(getBaseUri(BEACON_PATH).build());
            HttpResponse httpresponse = httpclient.execute(get);

            String content = EntityUtils.toString(httpresponse.getEntity());

            if (httpresponse.getStatusLine().getStatusCode() > 300) {
                throw new BeaconException(content);

            } else {
                return gson.fromJson(content, Beacon.class);
            }
        } catch (Exception e) {
            throw new BeaconException(e.getMessage());
        }
    }

    /**
     * Query the remote beacon and test for the presence a single variant
     * @param referenceName name of the reference
     * @param start start position
     * @param referenceBases reference bases
     * @param alternateBases alternate bases
     * @param assemblyId genome assembly
     * @param datasetIds list of datasetIds
     * @param includeDatasetResponses include
     * @return BeaconAlleleResponse from the remote server
     * @throws BeaconException
     */
    public BeaconAlleleResponse getBeaconResponse(String referenceName, Long start, String referenceBases, String alternateBases, String assemblyId, List<String> datasetIds, Boolean includeDatasetResponses) throws BeaconException {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            UriBuilder builder = getBaseUri(BEACON_QUERY);
            builder
                    .queryParam("referenceName", referenceName)
                    .queryParam("start", start)
                    .queryParam("referenceBases", referenceBases)
                    .queryParam("alternateBases", alternateBases)
                    .queryParam("assemblyId", assemblyId);

            for (String dataset : datasetIds) {
                builder.queryParam("datasetIds", dataset);
            }

            if (includeDatasetResponses != null) {
                builder.queryParam("includeDatasetResponses", includeDatasetResponses);
            }

            HttpGet get = new HttpGet(builder.build());

            HttpResponse httpResponse = httpclient.execute(get);
            String content = EntityUtils.toString(httpResponse.getEntity());


            if (httpResponse.getStatusLine().getStatusCode() > 300) {
                throw new BeaconException(content);
            } else {
                return gson.fromJson(content, BeaconAlleleResponse.class);
            }

        } catch (Exception e) {
            throw new BeaconException(e.getMessage());
        }
    }

    /**
     * Query the remote beacon and test for the presence a single variant
     *
     * @param request request object
     * @return BeaconAlleleResponse object
     * @throws BeaconException
     */
    public BeaconAlleleResponse getBeaconResponse(BeaconAlleleRequest request) throws BeaconException {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post = new HttpPost(getBaseUri(BEACON_QUERY).build());
            post.addHeader("Accept", MediaType.APPLICATION_JSON);
            post.addHeader("Content-Type", MediaType.APPLICATION_JSON);


            HttpEntity entity = new StringEntity(requestToString(request));

            post.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(post);

            String content = EntityUtils.toString(httpResponse.getEntity());

            if (httpResponse.getStatusLine().getStatusCode() > 300) {
                throw new BeaconException(content);
            } else {
                return gson.fromJson(content, BeaconAlleleResponse.class);
            }
        } catch (Exception e) {
            throw new BeaconException(e.getMessage());
        }
    }

    /**
     * Compose the url to query and return a new Uri builder to add additional parameters.
     * @param path path to query
     * @return UriBuilder Object
     * @throws URISyntaxException
     */
    private UriBuilder getBaseUri(String path) throws URISyntaxException {
        String url = beaconDTO.getUrl();

        //Ensure the url has a web protocol
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        if (url.endsWith("/")) {
            url = new StringBuilder(url).replace(url.length() - 1, url.length(), "").toString();
        }

        UriBuilder builder = UriBuilder.fromPath(url + path);
        if (beaconDTO.getKey() != null) {
            builder.queryParam("key", beaconDTO.getKey());
        }
        return builder;
    }

    /**
     * Convert a beaconAlleleRequest to a string object
     * @param request request object
     * @return String
     */
    private String requestToString(BeaconAlleleRequest request) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(request);
    }

}
