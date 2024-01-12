<dependencies>
>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.9</version> 
        <groupId>org.jfree</groupId>
        <artifactId>jfreechart</artifactId>
        <version>1.5.3</version>
    </dependency>
</dependencies>

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DailyChartApp extends JFrame {

    public DailyChartApp(String title, String symbol, DefaultCategoryDataset dataset) {
        super(title);
        JFreeChart chart = ChartFactory.createLineChart(
                "Daily Chart - " + symbol, // 차트 제목
                "Date", // x축 레이블
                "Closing Price", // y축 레이블
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        // 발급받은 API 키
        String apiKey = "ZM0OCCQ902KM00LJ";

        // 사용자로부터 주식 종목 입력 받기
        Scanner scanner = new Scanner(System.in);
        System.out.print("주식 종목을 입력하세요 (예: AAPL): ");
        String symbol = scanner.nextLine();

        // 데이터셋 생성
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            // API 엔드포인트 및 요청 URL 생성
            String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + apiKey;
            URL url = new URL(apiUrl);

            // HTTP 요청 보내기
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // 응답을 JSON으로 파싱
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            // 시계열 데이터에 해당하는 부분 가져오기
            JsonObject timeSeriesData = jsonResponse.getAsJsonObject("Time Series (Daily)");

            // 가져온 데이터를 데이터셋에 추가
            for (String date : timeSeriesData.keySet()) {
                String closingPrice = timeSeriesData.getAsJsonObject(date).get("4. close").getAsString();
                dataset.addValue(Double.parseDouble(closingPrice), "Closing Price", date);
            }

            // 차트 생성 및 표시
            SwingUtilities.invokeLater(() -> {
                DailyChartApp chartApp = new DailyChartApp("Daily Chart Example", symbol, dataset);
                chartApp.setSize(800, 600);
                chartApp.setLocationRelativeTo(null);
                chartApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                chartApp.setVisible(true);
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Scanner 닫기
            scanner.close();
        }
    }
}
