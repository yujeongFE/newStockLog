import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NewsApiSearch {

    public static void main(String[] args) {
      
        // NewsAPI에서 발급받은 API 키
        String apiKey = "1277dcdf93f8462a96f2efd5778607ae";

        // 사용자로부터 검색할 키워드를 입력 받음
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the keyword to search for news: ");
        String keyword = scanner.nextLine();

        // NewsAPI에서 뉴스를 검색하기 위한 API URL
        String apiUrl = "https://newsapi.org/v2/everything?q=" + keyword + "&apiKey=" + apiKey;

        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);
            
            // HTTP 연결 객체 생성
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 콘솔에 응답 데이터 출력
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
