package stocklogmanipulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;

public class SI_Panel2Action {

    public static void addFunctionality(JPanel panel, String stockName) {
        createChartForPanel(panel, stockName);
    }

    private static void createChartForPanel(JPanel panel, String stockName) {
        // 차트 데이터셋 생성
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 데이터 추가
        addDataToDataset(dataset, stockName);

        try {
            // 차트 생성
            JFreeChart chart = ChartFactory.createLineChart(
                    "Comparison Chart 1",   // 차트 제목
                    "Category",             // X축 레이블
                    "Price",                // Y축 레이블
                    dataset                 // 데이터셋
            );

            // 차트 선 색상 변경 (초록색)
            chart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GREEN);

            // 차트 배경색 변경 (하얀색)
            chart.getPlot().setBackgroundPaint(Color.WHITE);

            // 차트를 패널에 추가
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
            panel.removeAll();
            panel.add(chartPanel);
            panel.validate();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addDataToDataset(DefaultCategoryDataset dataset, String stockName) {
        // 엑셀 파일 경로
        String excelFilePath = "C:\\Users\\user\\Documents\\카카오톡 받은 파일\\주식시세.xlsx";

        try (FileInputStream file = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 첫 번째 시트 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 주식 종목명(stockName)을 찾아 해당 행 번호 가져오기
            int targetRow = findRowForStock(sheet, stockName);

            if (targetRow != -1) {
                // 찾은 행에서 원하는 열의 데이터 읽어오기
                double value5 = getNumericCellValue(sheet, targetRow, 5);
                double value7 = getNumericCellValue(sheet, targetRow, 7);
                double value8 = getNumericCellValue(sheet, targetRow, 8);

                // 데이터셋에 추가
                dataset.addValue(value5, "Price", "Standard Price");
                dataset.addValue(value7, "Price", "High Price");
                dataset.addValue(value8, "Price", "Low Price");
            } else {
                System.out.println("Stock not found in the Excel file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + excelFilePath);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int findRowForStock(Sheet sheet, String stockName) {
        for (Row row : sheet) {
            // 4열에 해당하는 셀의 값을 읽어서 stockName과 비교 (4열이 주식 종목명)
            Cell cell = row.getCell(3); // 4열(0-based index)
            if (cell != null && cell.getStringCellValue().contains(stockName)) {
                return row.getRowNum();
            }
        }
        // 찾지 못한 경우 -1 반환
        return -1;
    }

    private static double getNumericCellValue(Sheet sheet, int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(colNum);

        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        // 셀이 숫자 형식이 아니면 0.0 반환
        return 0.0;
    }
}