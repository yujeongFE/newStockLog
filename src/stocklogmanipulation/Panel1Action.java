package stocklogmanipulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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

class Panel1Action { // 종목 지수
    public static void addFunctionality(JPanel panel) {
        try {
            // 시작 및 종료 날짜를 Unix 타임스탬프로 변환
            long 시작날짜 = convertDateToTimestamp("20240101");
            long 종료날짜 = convertDateToTimestamp("20240111");

            // 업데이트된 날짜 범위로 Yahoo Finance API URL 작성
            String apiUrl = "https://query1.finance.yahoo.com/v8/finance/chart/^KS11?period1=" + 시작날짜 +
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
                plotStockChart(response.toString(), panel);
            } else {
                System.out.println("HTTP request failed with response code: " + 응답코드);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void plotStockChart(String jsonResponse, JPanel panel) {
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

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "KOSPI",
                    "날짜",
                    "종가",
                    dataset,
                    false,
                    false,
                    false
            );

            XYPlot plot = (XYPlot) chart.getPlot();
            Font font = new Font("맑은 고딕", Font.PLAIN, 8);

            // 한국어 폰트 설정
            DateAxis dateAxis = new DateAxis("날짜");
            dateAxis.setAutoTickUnitSelection(true);
            dateAxis.setTickLabelsVisible(true);
            dateAxis.setTickLabelFont(font);
            dateAxis.setTickLabelPaint(Color.black);
            dateAxis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));
            dateAxis.setTickLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            dateAxis.setLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));

            plot.setDomainAxis(dateAxis);

            NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
            rangeAxis.setLabelFont(new Font("맑은 고딕", Font.PLAIN, 9));
            rangeAxis.setTickLabelFont(new Font("맑은 고딕", Font.PLAIN, 8));
            rangeAxis.setAutoRange(true);
            dateAxis.setTickLabelPaint(Color.black);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // 폰트 및 인코딩 설정

            chart.getTitle().setFont(font);
            chart.getXYPlot().getDomainAxis().setLabelFont(font);
            chart.getXYPlot().getRangeAxis().setLabelFont(font);
            chart.getTitle().setFont(new Font("맑은 고딕", Font.BOLD, 18));

            rangeAxis.setRange(2200, 3000);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
            plot.getRenderer().setSeriesPaint(0, new Color(0, 255, 0));
            plot.setBackgroundPaint(Color.white);
            plot.setDomainGridlinePaint(Color.lightGray);
            plot.setRangeGridlinePaint(Color.lightGray);

            // XYPlot의 렌더러 가져오기
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

            // 차트 라인 색상 설정 (형광 초록색)
            renderer.setSeriesPaint(0, new Color(0, 255, 0));

            // 차트 라인 굵기 설정 (굵게)
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));



            panel.removeAll();
            panel.add(chartPanel);
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