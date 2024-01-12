package stocklogmanipulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

class PanelAction1_1 { // 매도주식
    public static void addFunctionality(JPanel panel) {
        try {
            // 시작 및 종료 날짜를 Unix 타임스탬프로 변환
            long 시작날짜 = convertDateToTimestamp("20240101");
            long 종료날짜 = convertDateToTimestamp("20240107");

            // 업데이트된 날짜 범위로 Yahoo Finance API URL 작성 (코스닥 심볼: ^KQ11)
            String apiUrl = "https://query1.finance.yahoo.com/v8/finance/chart/^KQ11?period1=" + 시작날짜 +
                    "&period2=" + 종료날짜 + "&interval=1d";

            // URL 객체 생성
            URL url = new URL(apiUrl);

            // 연결 열기
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 요청 방법 설정
            connection.setRequestMethod("GET");

            // 응답 코드 가져오기
            int 응답코드 = connection.getResponseCode();

            if (응답코드 == HttpURLConnection.HTTP_OK) {
                // 응답 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // 응답 처리 및 JFreeChart를 사용하여 타임스탬프 및 종가 값 플로팅
                plotStockChart(response.toString(), "KOSDAQ", panel);
            } else {
                System.out.println("HTTP request failed with response code: " + 응답코드);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void plotStockChart(String jsonResponse, String chartTitle, JPanel panel) {
        // JSON 파싱 및 타임스탬프 및 종가 데이터 추출
        List<Long> 타임스탬프데이터 = new ArrayList<>();
        List<Double> 종가데이터 = new ArrayList<>();

        // simple.json 라이브러리를 사용하여 JSON 파싱
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject chartObject = (JSONObject) jsonObject.get("chart");
            JSONArray resultArray = (JSONArray) chartObject.get("result");
            JSONObject resultObject = (JSONObject) resultArray.get(0);

            // get 메서드를 사용하여 중첩된 JSON 객체 추출
            JSONObject indicatorsObject = (JSONObject) resultObject.get("indicators");
            JSONArray quoteArray = (JSONArray) indicatorsObject.get("quote");
            JSONObject quoteObject = (JSONObject) quoteArray.get(0);

            JSONArray timestampArray = (JSONArray) resultObject.get("timestamp");
            JSONArray closeArray = (JSONArray) quoteObject.get("close");

            // 타임스탬프 데이터 채우기
            for (Object timestamp : timestampArray) {
                타임스탬프데이터.add((Long) timestamp);
            }

            // 종가 데이터 채우기
            for (Object close : closeArray) {
                종가데이터.add((Double) close);
            }

            TimeSeries series = new TimeSeries("종가");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            for (int i = 0; i < 타임스탬프데이터.size(); i++) {
                Date xDate = new Date(타임스탬프데이터.get(i) * 1000);
                series.addOrUpdate(new Day(xDate), 종가데이터.get(i));
            }

            TimeSeriesCollection dataset = new TimeSeriesCollection(series);

            // 한글 폰트 설정
            Font font = new Font("맑은 고딕", Font.PLAIN, 8);
            UIManager.put("Button.font", font);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    chartTitle, // 차트 제목
                    "날짜", // X축 레이블
                    "종가", // Y축 레이블
                    dataset, // 데이터셋
                    false, // 범례 표시
                    false, // 툴팁 사용
                    false // URL 생성 설정
            );

            XYPlot plot = (XYPlot) chart.getPlot();

            chart.getTitle().setFont(new Font("맑은 고딕", Font.BOLD, 18)); // 차트 제목 폰트 크기 설정
            // 한국어 폰트 설정
            DateAxis dateAxis = new DateAxis("날짜");
            dateAxis.setLabel("날짜");
            dateAxis.setAutoTickUnitSelection(true); // Automatic tick unit selection
            dateAxis.setTickLabelsVisible(true);
            dateAxis.setTickLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            dateAxis.setLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            dateAxis.setTickLabelPaint(Color.black);
            dateAxis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));

            plot.setDomainAxis(dateAxis); // Set dateAxis as the domain axis of the XYPlot

            NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
            rangeAxis.setLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            rangeAxis.setTickLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            rangeAxis.setLabel("종가");
            rangeAxis.setRange(800, 900);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
            chartPanel.setBackground(new Color(0, 0, 0, 0));

            // 기존 차트 제거
            panel.removeAll();

            // 패널에 차트 추가
            panel.add(chartPanel);

            // 패널 다시 그리기
            panel.revalidate();
            panel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long convertDateToTimestamp(String dateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = dateFormat.parse(dateStr);
        return date.getTime() / 1000;
    }
}