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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;


/**
 * Created by jon on 15/02/2018.
 */
public class Service {
    BufferedWriter bw = null;
    FileWriter fw = null;
    File file = new File("result.csv");
    File missing = new File("missing");


    public void run() throws IOException, InterruptedException {
        List<String> coords = new ArrayList<>();

        invokeMissingNumbers();
        //List<Integer> missingNumbers = getMissingNumbers();

//        for (int i=100; i<1000; i++) {
//            List<String> coord = getData(i);
//            writeToFile(coord);
//
//        }

        List missing = new ArrayList();



    }


    public List<Integer> invokeMissingNumbers() throws IOException {
        List<Integer> plots = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(missing))) {
            String line;

            int buffer = 1000;
            int index = 0;
            List<String> missing = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // process the line.
                System.out.println(line);

                List<String> data = getData(Integer.parseInt(line));
                missing.addAll(data);
                index++;

                if (index==buffer) {
                    index=0;
                    writeToFile(missing);
                    missing.clear();
                }
            }
        }

        return plots;
    }


    public List<Integer> getMissingNumbers() throws IOException {
        List<Integer> plots = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                System.out.println(line);
                String[] split = line.split(",");
                String n = split[0];
                if (isInteger(n)) {
                    Integer integer = new Integer(n);
                    plots.add(integer);
                }
            }
        }

        return plots;
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

    public static boolean isInteger(String number) {


            try {
                Integer.parseInt(number);
            } catch(Exception e) {
                return false;
            }


        return true;
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
