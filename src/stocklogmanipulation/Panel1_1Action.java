package stocklogmanipulation;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

public class Panel1_1Action extends Thread {
    private final JPanel panelToUpdate;

    public Panel1_1Action(JPanel panelToUpdate) {
        this.panelToUpdate = panelToUpdate;
    }

    @Override
    public void run() {
        long initialDelay = calculateInitialDelay();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> addFunctionality(panelToUpdate), initialDelay, 24, TimeUnit.HOURS);
    }

    private long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar nextMidnight = Calendar.getInstance();
        nextMidnight.set(Calendar.HOUR_OF_DAY, 24);
        nextMidnight.set(Calendar.MINUTE, 0);
        nextMidnight.set(Calendar.SECOND, 0);
        nextMidnight.set(Calendar.MILLISECOND, 0);

        long initialDelayMillis = nextMidnight.getTimeInMillis() - now.getTimeInMillis();
        if (initialDelayMillis < 0) {
            initialDelayMillis += TimeUnit.HOURS.toMillis(24);
        }

        return initialDelayMillis;
    }

    protected static void addFunctionality(JPanel panelToUpdate) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7); // endDate를 7일 전으로 설정
            long startDate = calendar.getTimeInMillis() / 1000;

            // endDate를 현재 날짜로 설정
            long endDate = System.currentTimeMillis() / 1000;

            String apiUrl = "https://query1.finance.yahoo.com/v8/finance/chart/^KQ11?period1=" + startDate +
                    "&period2=" + endDate + "&interval=1d";

            System.out.println(apiUrl);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                plotStockChart(response.toString(), panelToUpdate);
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
        } catch (java.net.ConnectException e) {
            // 네트워크 연결 오류 처리
            System.out.println("Network connection error: " + e.getMessage());
        } catch (java.io.IOException e) {
            // 502 에러가 뜨는 경우
            if (e.getMessage().contains("502")) {
                System.out.println("502 Bad Gateway error입니다. 다시 실행해주세요: " + e.getMessage());
                // 502 오류에 대한 처리를 추가할 수 있음
            } else {
                System.out.println("IOException: " + e.getMessage());
            }
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
        }
    }

    public static long convertDateToTimestamp(String dateStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse(dateStr);
        return date.getTime() / 1000;
    }

    private static void plotStockChart(String jsonResponse, JPanel panelToUpdate) {
        List<Long> timestampData = new ArrayList<>();
        List<Double> closeData = new ArrayList();

        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject chartObject = (JSONObject) jsonObject.get("chart");
            JSONArray resultArray = (JSONArray) chartObject.get("result");
            JSONObject resultObject = (JSONObject) resultArray.get(0);

            JSONObject indicatorsObject = (JSONObject) resultObject.get("indicators");
            JSONArray quoteArray = (JSONArray) indicatorsObject.get("quote");
            JSONObject quoteObject = (JSONObject) quoteArray.get(0);

            JSONArray timestampArray = (JSONArray) resultObject.get("timestamp");
            JSONArray closeArray = (JSONArray) quoteObject.get("close");

            System.out.println(timestampArray);

            if (timestampArray != null) {
                for (Object timestamp : timestampArray) {
                    timestampData.add((Long) timestamp);
                }
            } else {
                System.out.println("Timestamp data is null or empty.");
                return;
            }

            for (Object close : closeArray) {
                closeData.add((Double) close);
            }

            TimeSeries series = new TimeSeries("Close Price");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            for (int i = 0; i < timestampData.size(); i++) {
                Date xDate = new Date(timestampData.get(i) * 1000);
                series.addOrUpdate(new Day(xDate), closeData.get(i));
            }

            TimeSeriesCollection dataset = new TimeSeriesCollection(series);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "KOSDAQ",
                    "일자",
                    "종가",
                    dataset,
                    false,
                    false,
                    false
            );


            XYPlot plot = (XYPlot) chart.getPlot();
            Font font = new Font("맑은 고딕", Font.PLAIN, 8);

            DateAxis dateAxis = new DateAxis("Date");
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

            chart.getTitle().setFont(font);
            chart.getXYPlot().getDomainAxis().setLabelFont(font);
            chart.getXYPlot().getRangeAxis().setLabelFont(font);
            chart.getTitle().setFont(new Font("맑은 고딕", Font.BOLD, 18));

            rangeAxis.setLabel("종가");
            rangeAxis.setRange(800, 900);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(panelToUpdate.getWidth(), panelToUpdate.getHeight()));
            plot.getRenderer().setSeriesPaint(0, new Color(0, 255, 0));
            plot.setBackgroundPaint(Color.white);
            plot.setDomainGridlinePaint(Color.lightGray);
            plot.setRangeGridlinePaint(Color.lightGray);

            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(0, 255, 0));
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));

            panelToUpdate.removeAll();
            panelToUpdate.add(chartPanel);
            panelToUpdate.revalidate();
            panelToUpdate.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
