package stocklogmanipulation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;


public class Panel5Action { // 주식 매매 기록
    static Object[] row = new Object[8];
    static String[] columnNames = {"종목명", "증권사", "매도/매수", "날짜", "주식단가", "수량", "매매비용(세금, 수수료)", "메모"};
    static DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);

    // Declare searchList as a class field
    private static JList<String> searchList;
    static String id;
    public static void addFunctionality(JPanel panel, String userId) {
        DBconnection dbConnector = new DBconnection();
        Connection connection = dbConnector.getConnection();

        id = userId;
        String query = "SELECT s.NAME, l.COMPANY, l.BUYORSELL, l.DATE, l.PRICE, l.QTY, l.TAX, l.MEMO FROM stock s, log l WHERE s.CODE = l.CODE AND U_ID = '" + id + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                row[0] = resultSet.getObject(1);
                row[1] = resultSet.getObject(2);
                row[2] = resultSet.getObject(3);
                row[3] = resultSet.getObject(4);
                row[4] = resultSet.getObject(5);
                row[5] = resultSet.getObject(6);
                row[6] = resultSet.getObject(7);
                row[7] = resultSet.getObject(8);
                tableModel.addRow(row);
            }

            // 전체 화면 size 가져오기
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JButton searchButton = new JButton("매도/매수 기록 추가");
            searchButton.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
            searchButton.setPreferredSize(new Dimension( screenSize.width,34));

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SellBuyFrame();
                }
            });
            panel.add(searchButton, BorderLayout.SOUTH);

            JLabel label = new JLabel("주식 매매 일지", SwingConstants.CENTER);
            panel.add(label, BorderLayout.NORTH);

            JTable table = new JTable(tableModel);
            JTableHeader header = table.getTableHeader();
            header.setFont(header.getFont().deriveFont(Font.BOLD, 17));

            table.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

            // 주식 클릭하면 Stock_Info 화면으로 이동
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();

                        // 여기서 선택된 행의 데이터
                        String stockName = (String) tableModel.getValueAt(row, 0);
                        new StockInfo_new(userId, stockName);
                    }
                }
            });

            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            JScrollPane scrollPane = new JScrollPane(table);

            Dimension panelSize = panel.getPreferredSize();
            int newScrollPaneHeight = (int) (panelSize.height * 0.66);
            scrollPane.setPreferredSize(new Dimension(0, newScrollPaneHeight));

            panel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dbConnector.closeConnection();
        }
    }

    public static void SellBuyFrame() {
        JFrame SellBuyFrame = new JFrame("매도/매수 기록 추가");
        SellBuyFrame.setLocation(800, 400);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel l1 = new JLabel();
        JTextField text = new JTextField(15);

        DefaultListModel<String> listModel = new DefaultListModel<>();

        searchList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(searchList);
        panel.add(scrollPane, BorderLayout.CENTER);
        searchList.setVisible(true);
        scrollPane.setVisible(true);

        JButton searchButton = new JButton("검색");
        inputPanel.add(l1);
        inputPanel.add(text);
        inputPanel.add(searchButton);

        panel.add(BorderLayout.NORTH, inputPanel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = text.getText();
                if (!searchTerm.isEmpty()) {
                    listModel.clear();
                    performSearch(searchTerm, listModel);
                    searchList.setModel(listModel);
                } else {
                    JOptionPane.showMessageDialog(SellBuyFrame, "검색어를 입력해주세요.");
                }
            }
        });

        searchList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = searchList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedItem = searchList.getModel().getElementAt(index);
                        String selectedStockName;
                        String selectedCode;

                        // extractItemInfo 메서드를 사용하여 itemName과 code를 추출
                        Pair<String, String> itemInfo = extractItemInfo(selectedItem);
                        selectedStockName = itemInfo.getLeft();
                        selectedCode = itemInfo.getRight();

                        handleResultLabelClick(selectedStockName, selectedCode);
                    }
                }
            }
        });

        SellBuyFrame.getContentPane().add(panel);
        SellBuyFrame.setSize(400, 400);
        SellBuyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SellBuyFrame.setVisible(true);
        SellBuyFrame.setLayout(new BoxLayout(SellBuyFrame.getContentPane(), BoxLayout.Y_AXIS));
    }

    private static void performSearch(String searchTerm, DefaultListModel<String> listModel) {
        try {
            String[] dateRange = getLastBusinessDayRange();
            String frdt = dateRange[0];
            String todt = dateRange[1];

            String urlStr = "https://api.odcloud.kr/api/GetStockSecuritiesInfoService/v1/getStockPriceInfo?";
            urlStr += "serviceKey=" + "1%2FWP%2BVc3M5kGU2bikqOuBl9hAtMQ7OeqB24EL0llGF9zC75kdgM1jbsTy90LiI9hmDwU7jeFjW8P%2B1VPFtc%2BDg%3D%3D";
            urlStr += "&beginBasDt=" + frdt;
            urlStr += "&endBasDt=" + todt;
            urlStr += "&likeItmsNm=" + URLEncoder.encode(searchTerm, "UTF-8");

            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(response.toString().getBytes("UTF-8")));

            NodeList itemList = document.getElementsByTagName("item");

            if (itemList.getLength() == 0) {
                JOptionPane.showMessageDialog(null, "검색 결과가 없습니다.");
                return;
            }

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    String itemName = itemElement.getElementsByTagName("itmsNm").item(0).getTextContent();
                    String scode = itemElement.getElementsByTagName("srtnCd").item(0).getTextContent();
                    // listModel.addElement(itemName);
                    listModel.addElement(itemName + " (" + scode + ")");
                    // listModel.addElement(new SearchResult(itemName, scode));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    private static String[] getLastBusinessDayRange() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -2);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String todt = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH, -1);
        String frdt = dateFormat.format(calendar.getTime());

        return new String[]{frdt, todt};
    }

    private static void handleResultLabelClick(String selectedStockName, String selectedCode) {
        SellBuy sb = new SellBuy();
        sb.setFrame(sb, id, selectedCode);
        sb.setSelectedStockName(selectedStockName);
        sb.openFrame(selectedStockName);
    }
    // itemName과 code를 함께 추출하는 메서드
    private static Pair<String, String> extractItemInfo(String selectedItem) {
        int indexOfParenthesis = selectedItem.indexOf(" (");
        if (indexOfParenthesis != -1) {
            String itemName = selectedItem.substring(0, indexOfParenthesis);
            String code = selectedItem.substring(indexOfParenthesis + 2, selectedItem.length() - 1);
            return Pair.of(itemName, code);
        } else {
            // 괄호가 없는 경우
            return Pair.of(selectedItem, "");
        }
    }
}