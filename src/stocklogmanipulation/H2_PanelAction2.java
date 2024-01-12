package stocklogmanipulation;
import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import static stocklogmanipulation.Home2.stockName;


class H2_PanelAction2 extends JPanel {

    public H2_PanelAction2(DefaultCategoryDataset dataset) {
        String chartTitle = "Comparison Chart";
        JFreeChart chart = ChartFactory.createLineChart(
                chartTitle, // 차트 제목
                "Time", // x축 레이블
                "Value", // y축 레이블
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel);
    }

    public static void createChart(DefaultCategoryDataset dataset) {
        // 차트 생성 및 표시
        SwingUtilities.invokeLater(() -> {
            H2_PanelAction2 chartApp = new H2_PanelAction2(dataset);
            JFrame frame = new JFrame("Comparison Chart");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(chartApp);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static void addFunctionality(JPanel panel, String stockName) {
        // 패널에 차트를 추가하는 기능
        DefaultCategoryDataset dataset = fetchDataFromExcel(stockName);
        createChart(dataset);
        panel.add(new H2_PanelAction2(dataset));
    }

    private static DefaultCategoryDataset fetchDataFromExcel(String stockName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            // 엑셀 파일 경로 설정
            String excelFilePath = "C:/Users/user/Documents/카카오톡 받은 파일/주식시세.xls";


            // 엑셀 파일 열기
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            Workbook workbook = WorkbookFactory.create(inputStream);

            // 첫 번째 시트 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 열 인덱스
            int stockNameColumnIndex = 3;
            int timeColumnIndex = 0;
            int basePriceColumnIndex = 5; // 6행
            int lowPriceColumnIndex = 6; // 7행
            int highPriceColumnIndex = 7; // 8행

            // 각 행 반복
            for (Row row : sheet) {
                // 특정 열의 값 가져오기 (stockNameColumnIndex에 해당하는 열)
                Cell stockNameCell = row.getCell(stockNameColumnIndex);
                if (stockNameCell != null && stockNameCell.getCellType() == CellType.STRING) {
                    String cellValue = stockNameCell.getStringCellValue();

                    // 주어진 stockName과 일치하는 경우에만 데이터셋에 추가
                    if (cellValue.equals(stockName)) {
                        // 시간과 기준가, 저가, 고가 데이터를 추출하여 데이터셋에 추가
                        Cell timeCell = row.getCell(timeColumnIndex);
                        Cell basePriceCell = row.getCell(basePriceColumnIndex);
                        Cell lowPriceCell = row.getCell(lowPriceColumnIndex);
                        Cell highPriceCell = row.getCell(highPriceColumnIndex);

                        if (timeCell != null && basePriceCell != null && lowPriceCell != null && highPriceCell != null) {
                            String time = timeCell.getStringCellValue();
                            double basePrice = basePriceCell.getNumericCellValue();
                            double lowPrice = lowPriceCell.getNumericCellValue();
                            double highPrice = highPriceCell.getNumericCellValue();

                            // 데이터셋에 추가
                            dataset.addValue(basePrice, "Base Price", time);
                            dataset.addValue(lowPrice, "Low Price", time);
                            dataset.addValue(highPrice, "High Price", time);
                        }
                    }
                }
            }

            // 엑셀 파일 닫기
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            JPanel panel = new JPanel();
            H2_PanelAction2.addFunctionality(panel, stockName); // stockName을 "삼성전자"로 지정한 예시
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}