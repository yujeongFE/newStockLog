import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class NewsApiExample {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // NewsAPI에서 발급받은 API 키
            String apiKey = "1277dcdf93f8462a96f2efd5778607ae";

            System.out.print("Enter the country code (e.g., us, kr, jp): ");
            String country = reader.readLine();

            System.out.print("Enter the category (e.g., business, entertainment, general, health, science, sports, technology, sources): ");
            String category = reader.readLine();

            //국가별 인기 헤드라인 출력
            getTopHeadlines(apiKey, country);

            //특정 카테고리의 인기 헤드라인 출력
            getTopHeadlines(apiKey, category);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getTopHeadlines(String apiKey, String parameter) {
        try {
            String apiUrl = "https://newsapi.org/v2/top-headlines";
            String urlParameters = "apiKey=" + apiKey + "&" + parameter;
            URL url = new URL(apiUrl + "?" + urlParameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // 콘솔에 출력
            System.out.println("Response: " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
