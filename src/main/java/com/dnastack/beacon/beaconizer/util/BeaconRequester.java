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
 * Created by patrickmagee on 2016-07-19.
 */
public class BeaconRequester {

    private static final String BEACON_PATH = "/";
    private static final String BEACON_QUERY = BEACON_PATH + "query";

    private BeaconDTO beaconDTO;

    private Gson gson;

    public BeaconRequester(BeaconDTO beaconDTO) {
        this.beaconDTO = beaconDTO;
        gson = new GsonBuilder().create();
    }

    public BeaconDTO getBeaconDTO() {
        return beaconDTO;
    }

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

    private UriBuilder getBaseUri(String path) throws URISyntaxException {
        String url = beaconDTO.getUrl();

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

    private String requestToString(BeaconAlleleRequest request) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(request);
    }

}
