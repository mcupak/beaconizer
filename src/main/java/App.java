import com.dnastack.ga4gh.api.GABeacon;
import com.dnastack.ga4gh.impl.BeaconizeVariantImpl;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * App that demonstrates use of Beaconizer
 *
 * @author mfiume
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException {

        // this key is obtained from Google and is IP specific
        // follow instructions here: https://cloud.google.com/genomics/auth
        String key = "AIzaSyCdHZRfij_l5YMiunu7oLoY-tXSPNzHuhk";

        String rootURL = "https://www.googleapis.com/genomics/v1beta";

        GABeacon thousandGenomesBeacon = new BeaconizeVariantImpl("Google", rootURL, key, new String[]{"4252737135923902652"});
        GABeacon curoverse = new BeaconizeVariantImpl("curoverse", "http://lightning-dev4.curoverse.com/api", null, new String[]{"hu"});

        for (GABeacon b : new GABeacon[]{thousandGenomesBeacon, curoverse}) {
            System.out.println(b + " result: " + b.exists(null, "1", 10177, "AC"));
            System.out.println(b + " result: " + b.exists(null, "1", 10177, "A"));
        }
    }
}
