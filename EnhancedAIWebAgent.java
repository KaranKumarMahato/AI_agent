import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.SheetsScopes;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import static com.google.auth.oauth2.GoogleCredentials.*;


public class EnhancedAIWebAgent {


    private static final Logger logger = Logger.getLogger(EnhancedAIWebAgent.class.getName());
    private static final String BUCKET_NAME = "ai-web-agent-project";
    private static final String GOOGLE_SHEETS_API_SCOPES = SheetsScopes.SPREADSHEETS;
    private static final String GOOGLE_CREDENTIALS_FILE = "Downloads";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private static final String OPENAI_API_KEY = "here the key should be provided as it gives a complete exposure thatswhy i have not given it";

    private final OkHttpClient client = new OkHttpClient();

    public List<List<Object>> readGoogleSheet(String spreadsheetId, String range) throws IOException {
        GoogleCredentials credentials;
        credentials = GoogleCredentials.fromStream(new FileInputStream(GOOGLE_CREDENTIALS_FILE))
                .createScoped(Collections.singleton(GOOGLE_SHEETS_API_SCOPES));
        Sheets sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName("Google Sheets Integration")
                .build();

        com.google.api.client.http.HttpResponse response;
        response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .executeUsingHead();
        return response.getValues();
    }

    public String performWebSearch(String query) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "text-davinci-003");
        jsonObject.put("prompt", query);
        jsonObject.put("max_tokens", 50);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                return choices.getJSONObject(0).getString("text").trim();
            } else {
                throw new IOException("Web search failed: " + response);
            }
        }
    }

    public List<String> processEntities(List<List<Object>> records, int columnIndex) {
        List<String> results = new ArrayList<>();
        for (List<Object> record : records) {
            if (record.size() > columnIndex) {
                String entity = record.get(columnIndex).toString();
                try {
                    String result = performWebSearch(entity);
                    results.add(result);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error processing entity: " + entity, e);
                    results.add("Error processing entity: " + entity);
                }
            }
        }
        return results;
    }

    public void uploadToCloudStorage(String outputPath, List<String> results) throws IOException {
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(fromStream(new FileInputStream(GOOGLE_CREDENTIALS_FILE)))
                .build()
                .getService();

        StringBuilder csvContent = new StringBuilder("Results\n");
        for (String result : results) {
            csvContent.append(result).append("\n");
        }

        BlobId blobId = BlobId.of(BUCKET_NAME, outputPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/csv").build();
        storage.create(blobInfo, csvContent.toString().getBytes());
    }

    public static void main(String[] args) {
        EnhancedAIWebAgent agent = new EnhancedAIWebAgent();

        try {
            String spreadsheetId = "your-spreadsheet-id";
            String range = "Sheet1!A:B";
            int columnIndex = 1;
            List<List<Object>> records = agent.readGoogleSheet(spreadsheetId, range);
            List<String> results = agent.processEntities(records, columnIndex);
            String outputPath = "results/output.csv";
            agent.uploadToCloudStorage(outputPath, results);

            System.out.println("Results uploaded to Cloud Storage: " + outputPath);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        }
    }
}



