package stocklogmanipulation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Panel6Action {

    public static void addFunctionality(JPanel panel) {
        panel.setBackground(Color.GRAY);
        panel.setBorder(BorderFactory.createEtchedBorder());
    }

    private static long convertDateToTimestamp(String dateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = dateFormat.parse(dateStr);
        return date.getTime() / 1000;
    }

    private static void displayResponseInConsole(String response) {
        System.out.println(response);
    }

    public static void executeApiRequestAndDisplayInPanel(JPanel panel) {
        try {
            long 시작날짜 = convertDateToTimestamp("20240101");
            long 종료날짜 = convertDateToTimestamp("20240107");

            String kospiUrl = "https://query1.finance.yahoo.com/v8/finance/chart/^KS11?period1=" + 시작날짜 +
                    "&period2=" + 종료날짜 + "&interval=1d";

            String kosdaqUrl = "https://query1.finance.yahoo.com/v8/finance/chart/^KQ11?period1=" + 시작날짜 +
                    "&period2=" + 종료날짜 + "&interval=1d";

            String kospiResponse = executeApiRequest(kospiUrl);
            String kosdaqResponse = executeApiRequest(kosdaqUrl);

            processJsonResponseAndDisplayInPanel(kospiResponse, panel, "KOSPI");
            processJsonResponseAndDisplayInPanel(kosdaqResponse, panel, "KOSDAQ");

            // Additional functionality to scrape and display Naver Finance data
            executeNaverFinanceScrapingAndDisplayInPanel(panel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String executeApiRequest(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int 응답코드 = connection.getResponseCode();

        if (응답코드 == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } else {
            System.out.println("HTTP request failed with response code: " + 응답코드);
            return null;
        }
    }

    private static void processJsonResponseAndDisplayInPanel(String jsonResponse, JPanel panel, String market) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray resultArray = json.getJSONObject("chart").getJSONArray("result");
            JSONObject firstResult = resultArray.getJSONObject(0);
            JSONObject meta = firstResult.getJSONObject("meta");
            double chartPreviousClose = meta.getDouble("chartPreviousClose");
            double regularMarketPrice = meta.getDouble("regularMarketPrice");
            int priceHint = meta.getInt("priceHint");
            double difference = chartPreviousClose - regularMarketPrice;

            displayValuesInPanel(panel, chartPreviousClose, regularMarketPrice, difference, priceHint, market);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeNaverFinanceScrapingAndDisplayInPanel(JPanel panel) {
        try {
            String naverFinanceUrl = "https://finance.naver.com/sise/investorDealTrendDay.nhn?bizdate=20240112";

            // Scrape 'rate_down3' data
            List<String> downList = scrapeRateData(naverFinanceUrl, "rate_down3");

            // Scrape 'rate_up3' data
            List<String> upList = scrapeRateData(naverFinanceUrl, "rate_up3");

            // Extract the first two elements from 'rate_down3' and the first element from 'rate_up3'
            List<String> selectedDownList = downList.subList(0, Math.min(downList.size(), 2));
            List<String> selectedUpList = upList.subList(0, Math.min(upList.size(), 1));

            // Display the extracted elements in the panel
            displayDataInPanel(panel, "개인, 외국인", selectedDownList, Color.BLUE);
            displayDataInPanel(panel, "기관계", selectedUpList, Color.RED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> scrapeRateData(String url, String className) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements elements = document.select("td." + className);

        List<String> dataList = new ArrayList<>();
        for (Element element : elements) {
            dataList.add(element.text());
        }

        return dataList;
    }

    private static void displayDataInPanel(JPanel panel, String labelTitle, List<String> dataList, Color labelColor) {
        JLabel titleLabel = new JLabel(labelTitle);
        titleLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);

        for (String data : dataList) {
            JLabel label = new JLabel(data);
            label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
            label.setForeground(labelColor);
            panel.add(label);
        }

        panel.revalidate();
        panel.repaint();
    }

    private static void displayValuesInPanel(JPanel panel, double chartPreviousClose, double regularMarketPrice,
                                             double difference, int priceHint, String market) {
        JLabel label;

        if (difference < 0) {
            label = new JLabel(
                    market + " " +
                            "▼ " +
                            chartPreviousClose + " " +
                            regularMarketPrice + " " +
                            String.format("%.2f", difference) + " "
            );
        } else {
            label = new JLabel(
                    market + " " +
                            "▲ " +
                            chartPreviousClose + " " +
                            regularMarketPrice + " " +
                            String.format("%.2f", difference) + " "
            );
        }

        label.setFont(new Font("Arial", Font.PLAIN, 14));

        if (difference < 0) {
            label.setForeground(Color.BLUE);
        } else {
            label.setForeground(Color.RED);
        }

        panel.add(label);

        double percentageChange = (difference / chartPreviousClose) * 100;

        JLabel percentageLabel;

        if (difference < 0) {
            percentageLabel = new JLabel(String.format("%.2f", percentageChange) + "%");
        } else {
            percentageLabel = new JLabel(String.format("%.2f", percentageChange) + "%");
        }

        percentageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        if (difference < 0) {
            percentageLabel.setForeground(Color.BLUE);
        } else {
            percentageLabel.setForeground(Color.RED);
        }

        panel.add(percentageLabel);

        panel.revalidate();
        panel.repaint();
    }
}