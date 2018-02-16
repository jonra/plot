import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;


/**
 * Created by jon on 15/02/2018.
 */
public class Service {
    BufferedWriter bw = null;
    FileWriter fw = null;
    File file = new File("coords.csv");


    public void run() throws IOException, InterruptedException {
        List<String> coords = new ArrayList<>();

        for (int i=100; i<10000; i++) {
            List<String> coord = getData(i);
            writeToFile(coord);
            Thread.sleep(2000);
        }



    }
    public List<String> getData(int index) throws IOException {
        String url = String.format("https://www.plotfind.co.bw/server_request/get_plot.php?plot=%s", index);

        final ResponseEntity<String> forEntity = getRestTemplate().exchange(url, GET, getHttpEntity(), String.class);
        String json =  forEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();


        RemoteJson obj = mapper.readValue(json, RemoteJson.class);

        List<String> results = new ArrayList<>();
        for (Feature feature : obj.getFeatures()) {
            results.add(feature.getProperties().getName() + "," +
            feature.getProperties().getArea() + ", " +
            feature.getProperties().getTown() + "," +
            feature.getGeometry().getCoordinates().get(0) + "," +
            feature.getGeometry().getCoordinates().get(1) + "\n");

        }


        for (String result : results) {
            System.out.println(result);
        }

        return results;
    }


    private void writeToFile(List<String> lines) {

        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Create file " + file.getAbsoluteFile());

            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            for (String s : lines) {
                bw.write(s);
            }

            closeFile();


        } catch (IOException e) {

            e.printStackTrace();

        }
    }


    private void closeFile() {
        try {

            if (bw != null)
                bw.close();

            if (fw != null)
                fw.close();

        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }


    private RestTemplate getRestTemplate() {
        final CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }

    private HttpEntity<Void> getHttpEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}
