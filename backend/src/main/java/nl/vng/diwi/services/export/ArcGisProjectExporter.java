package nl.vng.diwi.services.export;

import jakarta.ws.rs.core.StreamingOutput;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.rest.VngServerErrorException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Log4j2
public class ArcGisProjectExporter {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String TYPE = "type";
    private static final String EXTENSION = "extension";
    private static final String ARCGIS_SPECIFIC_FLAG = "f";
    private static final String ASYNC = "async";
    private static final String TITLE = "title";
    private static final String TOKEN = "token";
    private static final String FILE = "file";
    private static final String GEO_JSON_TYPE = "GeoJson";
    private static final String GEO_JSON_EXTENSION = "geojson";
    private static final String JSON = "json";
    private static final String TRUE = "true";
    private static final String ARCGIS_URL_ENV_KEY = "ARCGIS_URL";
    private static final String ARCGIS_BASE_URL_DEFAULT_VALUE = "https://zuid-holland-hub.maps.arcgis.com";


    public void exportProject(StreamingOutput output, String token, String filename) {
        String username = "erombouts_prw"; //TODO dinamicaly get this value
        byte[] fileBytes = convertStreamingOutputToByteArray(output);

        if (fileBytes.length == 0) {
            throw new VngServerErrorException("StreamingOutput is empty");
        }

        RequestBody fileStreamBody = RequestBody.create(fileBytes, MediaType.parse(APPLICATION_OCTET_STREAM));
        MultipartBody requestBody = createMultipartBody(token, filename, fileStreamBody);
        Request request = createRequest(username, requestBody);

        executeRequest(request);
    }

    private MultipartBody createMultipartBody(String token, String filename, RequestBody fileStreamBody) {
        return new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(TYPE, GEO_JSON_TYPE)
            .addFormDataPart(EXTENSION, GEO_JSON_EXTENSION)
            .addFormDataPart(ARCGIS_SPECIFIC_FLAG, JSON)
            .addFormDataPart(ASYNC, TRUE)
            .addFormDataPart(TITLE, filename)
            .addFormDataPart(TOKEN, token)
            .addFormDataPart(FILE, filename + ".geojson", fileStreamBody)
            .build();
    }

    private Request createRequest(String username, MultipartBody requestBody) {
        Map<String, String> environment = System.getenv();
        String arcgisBaseUrl = environment.getOrDefault(ARCGIS_URL_ENV_KEY, ARCGIS_BASE_URL_DEFAULT_VALUE);
        String arcgisUploadUrl = arcgisBaseUrl + "/sharing/rest/content/users/" + username + "/addItem";
        return new Request.Builder()
            .url(arcgisUploadUrl)
            .post(requestBody)
            .build();
    }

    private void executeRequest(Request request) {
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                log.info("Upload successful: {}", response.body().string());
            } else {
                log.info("Upload failed: {}", response.message());
            }
        } catch (IOException e) {
            throw new VngServerErrorException("Error uploading GeoJSON file", e);
        }
    }

    private byte[] convertStreamingOutputToByteArray(StreamingOutput streamingOutput) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            streamingOutput.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new VngServerErrorException("Error converting StreamingOutput to byte array", e);
        }
    }
}
