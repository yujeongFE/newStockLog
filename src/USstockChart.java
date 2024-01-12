import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class USstockChart {

    public static void main(String[] args) {
        try {
            // API 매개변수 설정
            String function = "TIME_SERIES_INTRADAY";
            String symbol = "TSL"; // 미국 주요 주식 차트 조회 가능
            String interval = "5min";
            boolean adjusted = true;
            boolean extendedHours = true;
            String apiKey = ""; // API 키 넣는 곳

            // API 요청 URL 생성
            String apiUrl = buildApiUrl(function, symbol, interval, adjusted, extendedHours, apiKey);

            // HttpClient 생성
            HttpClient httpClient = HttpClient.newHttpClient();

            // API 요청 보내고 응답 받기
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 응답 출력
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String buildApiUrl(String function, String symbol, String interval, boolean adjusted, boolean extendedHours, String apiKey) {
        // ^ 문자를 인코딩
        String encodedSymbol = URLEncoder.encode(symbol, StandardCharsets.UTF_8);

        // API 요청 URL 생성
        return String.format("https://www.alphavantage.co/query?function=%s&symbol=%s&interval=%s&adjusted=%s&extended_hours=%s&apikey=%s",
                function, encodedSymbol, interval, adjusted, extendedHours, apiKey);
    }
}
