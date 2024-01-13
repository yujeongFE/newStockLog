package stocklogmanipulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.text.NumberFormat;
import java.sql.*;

import static stocklogmanipulation.Panel5Action.tableModel;

public class SellBuy {

    private JPanel sellbuylog;
    private SellBuyPanel sp;
    private CardLayout card;
    private String selectedStockName;

    /*public static void main(String[] args) {
        SellBuy sb = new SellBuy();
        sb.setFrame(sb);
        System.out.println(userid);
    }
*/
    public void setSelectedStockName(String stockName) {
        selectedStockName = stockName;
    }

    public void setFrame(SellBuy sb, String userid, String stockcode) {
        JFrame jf = new JFrame();
        sp = new SellBuyPanel(this, userid, stockcode);

        card = new CardLayout();
        sellbuylog = new JPanel(card);
        sellbuylog.add(sp.mainPanel);

        jf.add(sellbuylog);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(500, 500);
        jf.setVisible(true);
    }

    public void openFrame(String selectedStockName) {
        card.show(sellbuylog, sp.getName());
        sp.setStockNameTextField(selectedStockName);
    }
}

class SellBuyPanel extends JPanel {

    private JTextField itemname, stockfirm, returnprice;
    private JRadioButton sellButton, buyButton;
    private JComboBox<String> yearComboBox, monthComboBox, dayComboBox;
    private JComboBox<Integer> quantity;
    private JTextField price;
    private JTextArea memo;
    private JButton addButton;

    public void setStockNameTextField(String stockName) {
        itemname.setText(stockName);
    }

    public JPanel mainPanel;

    public SellBuyPanel(SellBuy sp, String userid, String stockcode) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        itemname = new JTextField(15);
        stockfirm = new JTextField(15);
        returnprice = new JTextField(15);
        sellButton = new JRadioButton("매도");
        buyButton = new JRadioButton("매수");
        ButtonGroup sellbuyGroup = new ButtonGroup();
        sellbuyGroup.add(sellButton);
        sellbuyGroup.add(buyButton);

        yearComboBox = new JComboBox<>(new String[]{"2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017",
                "2016", "2015", "2014"});
        monthComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12"});
        dayComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                "29", "30", "31"});

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        price = new JTextField(15);

        quantity = new JComboBox<>();
        for (int i = 1; i <= 100; i++) {
            quantity.addItem(i);
        }

        memo = new JTextArea(2, 15);
        memo.setLineWrap(true);

        addButton = new JButton("추가");

        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("종목명 : "), c);
        c.gridx = 1;
        c.gridy = 0;
        add(itemname, c);

        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("증권사 : "), c);
        c.gridx = 1;
        c.gridy = 1;
        add(stockfirm, c);

        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("매도/매수 : "), c);
        c.gridx = 1;
        c.gridy = 2;
        add(sellButton, c);
        c.gridx = 2;
        c.gridy = 2;
        add(buyButton, c);

        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("날짜 : "), c);
        c.gridx = 1;
        c.gridy = 3;
        add(yearComboBox, c);
        c.gridx = 2;
        c.gridy = 3;
        add(monthComboBox, c);
        c.gridx = 3;
        c.gridy = 3;
        add(dayComboBox, c);

        c.gridx = 0;
        c.gridy = 4;
        add(new JLabel("수량 : "), c);
        c.gridx = 1;
        c.gridy = 4;
        add(quantity, c);

        c.gridx = 0;
        c.gridy = 5;
        add(new JLabel("주식단가 : "), c);
        c.gridx = 1;
        c.gridy = 5;
        add(price, c);

        c.gridx = 0;
        c.gridy = 6;
        add(new JLabel("매매비용(세금, 수수료) : "), c);
        c.gridx = 1;
        c.gridy = 6;
        add(returnprice, c);

        c.gridx = 0;
        c.gridy = 7;
        add(new JLabel("메모 : "), c);
        c.gridx = 1;
        c.gridy = 7;
        add(memo, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 3;
        add(addButton, c);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDate = yearComboBox.getSelectedItem() + "-"
                        + monthComboBox.getSelectedItem() + "-"
                        + dayComboBox.getSelectedItem();
                int selectedQuantity = (int) quantity.getSelectedItem();


                Number selectedPrice = 0;
                try {
                    String formattedValue = price.getText();
                    String numericValue = formattedValue.replaceAll("[^0-9.]", "");
                    selectedPrice = Double.parseDouble(numericValue);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

                Number selectedReturnPrice = 0;
                try {
                    String returnPriceValue = returnprice.getText();
                    String numericReturnPrice = returnPriceValue.replaceAll("[^0-9.]", "");
                    selectedReturnPrice = Double.parseDouble(numericReturnPrice);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

                String selectedStockName = itemname.getText();
                String selectedStockFirm = stockfirm.getText();
                String selectedBuyOrSell = sellButton.isSelected() ? "매도" : "매수";
                String selectedMemo = memo.getText();

                Object[] rowData = new Object[]{
                        selectedStockName,
                        selectedStockFirm,
                        selectedBuyOrSell,
                        selectedDate,
                        selectedPrice,
                        selectedQuantity,
                        selectedReturnPrice,
                        selectedMemo
                };
                tableModel.addRow(rowData);

                // stock table에 넣기
                DBconnection dbConnector = new DBconnection();
                Connection connection = dbConnector.getConnection();

                String query = "SELECT * FROM stock WHERE NAME = '" + selectedStockName + "'";
                try {
                    Statement selectStatement = connection.createStatement();
                    ResultSet resultSet = selectStatement.executeQuery(query);

                    // 결과가 비어 있는지 확인
                    if (!resultSet.next()) {
                        // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
                        String insertQuery = "INSERT INTO stock (CODE, NAME) VALUES ('" + stockcode + "', '" + selectedStockName + "')";
                        Statement insertStatement = connection.createStatement();
                        insertStatement.executeUpdate(insertQuery);
                    }
                } catch (SQLException a) {
                    a.printStackTrace();
                } finally {
                    // 연결 닫기 등의 마무리 작업
                    dbConnector.closeConnection();
                }

                // 매수일 때
                if(selectedBuyOrSell == "매수") {
                    DBconnection dbConnector1 = new DBconnection();
                    Connection connection1 = dbConnector1.getConnection();
                    try {
                        // PreparedStatement 생성
                        String insertQuery = "INSERT INTO buystock (U_ID, CODE, COMPANY, B_DATE, B_PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection1.prepareStatement(insertQuery);

                        // 값을 설정합니다. 여기서 1, 2, 3은 각각 ?에 해당하는 순서입니다.
                        preparedStatement.setString(1, userid);
                        preparedStatement.setString(2, stockcode);
                        preparedStatement.setString(3, selectedStockFirm);
                        preparedStatement.setString(4, selectedDate);
                        preparedStatement.setString(5, selectedPrice.toString());
                        preparedStatement.setInt(6, selectedQuantity);
                        preparedStatement.setString(7, selectedMemo);

                        // 쿼리 실행
                        preparedStatement.executeUpdate();

                    } catch (SQLException b) {
                        b.printStackTrace();
                    }

                    try {
                        // PreparedStatement 생성
                        String insertQuery = "INSERT INTO log (U_ID, CODE, COMPANY, BUYORSELL, DATE, PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection1.prepareStatement(insertQuery);

                        preparedStatement.setString(1, userid);
                        preparedStatement.setString(2, stockcode);
                        preparedStatement.setString(3, selectedStockFirm);
                        preparedStatement.setString(4, "매수");
                        preparedStatement.setString(5, selectedDate);
                        preparedStatement.setString(6, selectedPrice.toString());
                        preparedStatement.setInt(7, selectedQuantity);
                        preparedStatement.setString(8, selectedMemo);

                        // 쿼리 실행
                        preparedStatement.executeUpdate();

                    } catch (SQLException c) {
                        c.printStackTrace();
                    }

                    // have table에 insert(null일 경우에만, 아닐 때는 update)
                    try {

                        String query1 = "SELECT * FROM have WHERE U_ID = '" + userid + "' AND CODE = '" + stockcode + "' AND COMPANY = '" + selectedStockFirm + "'";
                        Statement selectStatement = connection1.createStatement();
                        ResultSet resultSet = selectStatement.executeQuery(query1);

                        String adate = null;
                        String aprice = null;
                        String aqty = null;
                        // 첫 번째 호출에서 데이터 읽기
                        if (resultSet.next()) {
                            adate = resultSet.getObject(4).toString();
                            aprice = resultSet.getObject(5).toString();
                            aqty = resultSet.getObject(6).toString();
                        }

                        // 결과가 비어 있는지 확인
                        if (adate == null || aprice == null || aqty == null) {
                            // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
                            String insertQuery = "INSERT INTO have (U_ID, CODE, COMPANY, DATE, E_PRICE, QTY) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement insertStatement = connection1.prepareStatement(insertQuery);

                            insertStatement.setString(1, userid);
                            insertStatement.setString(2, stockcode);
                            insertStatement.setString(3, selectedStockFirm);
                            insertStatement.setString(4, selectedDate);
                            insertStatement.setString(5, selectedPrice.toString());
                            insertStatement.setInt(6, selectedQuantity);

                            // 쿼리 실행
                            insertStatement.executeUpdate();
                        } else {
                            Double resultprice = (Double.parseDouble(aprice) * Double.parseDouble(aqty) + selectedPrice.doubleValue() * selectedQuantity) / (Double.parseDouble(aqty) + selectedQuantity );
                            String rprice = String.format("%.2f", resultprice);

                            Integer rqty = Integer.parseInt(aqty) + selectedQuantity;

                            // 결과가 있으면 UPDATE 실행
                            String updateQuery = "UPDATE have SET DATE = ?, E_PRICE = ?, QTY = ? WHERE U_ID = ? AND CODE = ? AND COMPANY = ?";
                            PreparedStatement updateStatement = connection1.prepareStatement(updateQuery);
                            updateStatement.setString(1, adate);
                            updateStatement.setString(2, rprice);
                            updateStatement.setInt(3, rqty);
                            updateStatement.setString(4, userid);
                            updateStatement.setString(5, stockcode);
                            updateStatement.setString(6, selectedStockFirm);
                            updateStatement.executeUpdate();

                        }
                    } catch (SQLException d) {
                        d.printStackTrace();
                    } finally {
                        // 연결 닫기 등의 마무리 작업
                        dbConnector1.closeConnection();
                    }
                }

                // 매도일 때
                if(selectedBuyOrSell == "매도") {
                    DBconnection dbConnector1 = new DBconnection();
                    Connection connection1 = dbConnector1.getConnection();
                    // have table이 있을 경우에만 매도 가능
                    try {

                        String query1 = "SELECT * FROM have WHERE U_ID = '" + userid + "' AND CODE = '" + stockcode + "' AND COMPANY = '" + selectedStockFirm + "'";
                        Statement selectStatement = connection1.createStatement();
                        ResultSet resultSet = selectStatement.executeQuery(query1);

                        String adate = null;
                        String aprice = null;
                        String aqty = null;
                        // 첫 번째 호출에서 데이터 읽기
                        if (resultSet.next()) {
                            adate = resultSet.getObject(4).toString();
                            aprice = resultSet.getObject(5).toString();
                            aqty = resultSet.getObject(6).toString();
                        }

                        // 결과가 비어 있는지 확인
                        if (adate == null || aprice == null || aqty == null) {
                            // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
                            JOptionPane.showMessageDialog(null, "해당 주식을 보유하고 있지 않습니다. 다시 입력하여 주세요.");
                        } else {
                            Integer rqty = Integer.parseInt(aqty) - selectedQuantity;

                            /*수익률 : ((PRICE - aPRICE) * QTY - TAX) / aPRICE * 100
                            총 수익값 : (PRICE - aPRICE) * QTY - TAX*/

                            // (Double.parseDouble(aprice) * Double.parseDouble(aqty) + selectedPrice.doubleValue() * selectedQuantity) / (Double.parseDouble(aqty) + selectedQuantity );

                            Double resultrratio = ((selectedPrice.doubleValue() - (Double.parseDouble(aprice)) * selectedQuantity) - selectedReturnPrice.doubleValue()) / Double.parseDouble(aprice) * 100;
                            Double resultall = (selectedPrice.doubleValue() - (Double.parseDouble(aprice)) * selectedQuantity) - selectedReturnPrice.doubleValue();
                            String rratio = String.format("%.2f", resultrratio);
                            String rall = String.format("%.2f", resultall);

                            // 결과가 있으면 UPDATE 실행
                            // PreparedStatement 생성
                            String insertQuery = "INSERT INTO sellstock (U_ID, CODE, COMPANY, S_DATE, S_PRICE, QTY, RRATIO, ALLB, B_DATE, E_PRICE, TAX, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement preparedStatement = connection1.prepareStatement(insertQuery);

                            // 값을 설정합니다. 여기서 1, 2, 3은 각각 ?에 해당하는 순서입니다.
                            preparedStatement.setString(1, userid);
                            preparedStatement.setString(2, stockcode);
                            preparedStatement.setString(3, selectedStockFirm);
                            preparedStatement.setString(4, selectedDate);
                            preparedStatement.setString(5, selectedPrice.toString());
                            preparedStatement.setInt(6, selectedQuantity);
                            preparedStatement.setString(7, rratio);
                            preparedStatement.setString(8, rall);
                            preparedStatement.setString(9, adate);
                            preparedStatement.setString(10, aprice);
                            preparedStatement.setString(11, selectedReturnPrice.toString());
                            preparedStatement.setString(12, selectedMemo);

                            // 쿼리 실행
                            preparedStatement.executeUpdate();

                            // PreparedStatement 생성, log에 삽입
                            String insertQuery1 = "INSERT INTO log (U_ID, CODE, COMPANY, BUYORSELL, DATE, PRICE, QTY, RRATIO, TAX, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            PreparedStatement preparedStatement1 = connection1.prepareStatement(insertQuery1);

                            preparedStatement1.setString(1, userid);
                            preparedStatement1.setString(2, stockcode);
                            preparedStatement1.setString(3, selectedStockFirm);
                            preparedStatement1.setString(4, "매도");
                            preparedStatement1.setString(5, selectedDate);
                            preparedStatement1.setString(6, selectedPrice.toString());
                            preparedStatement1.setInt(7, selectedQuantity);
                            preparedStatement1.setString(8, rratio);
                            preparedStatement1.setString(9, selectedReturnPrice.toString());
                            preparedStatement1.setString(10, selectedMemo);

                            // 쿼리 실행
                            preparedStatement1.executeUpdate();

                            if(Double.parseDouble(aqty) - selectedQuantity == 0) {
                                String deleteQuery = "DELETE FROM have WHERE U_ID = ? AND CODE = ? AND COMPANY = ?";
                                PreparedStatement deleteStatement = connection1.prepareStatement(deleteQuery);

                                // 조건에 해당하는 값을 설정
                                deleteStatement.setString(1, userid);
                                deleteStatement.setString(2, stockcode);
                                deleteStatement.setString(3, selectedStockFirm);
                            }
                            else{
                                // 결과가 있으면 UPDATE 실행
                                String updateQuery = "UPDATE have SET DATE = ?, E_PRICE = ?, QTY = ? WHERE U_ID = ? AND CODE = ? AND COMPANY = ?";
                                PreparedStatement updateStatement = connection1.prepareStatement(updateQuery);
                                updateStatement.setString(1, adate);
                                updateStatement.setString(2, aprice);
                                updateStatement.setInt(3, rqty);
                                updateStatement.setString(4, userid);
                                updateStatement.setString(5, stockcode);
                                updateStatement.setString(6, selectedStockFirm);
                                updateStatement.executeUpdate();
                            }
                        }
                    } catch (SQLException d) {
                        d.printStackTrace();
                    }finally {
                        // 연결 닫기 등의 마무리 작업
                        dbConnector1.closeConnection();
                    }
                }


                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                frame.dispose();
            }
        });

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JLabel signupLabel = new JLabel("매도/매수 기록 추가");
        signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(signupLabel);
        mainPanel.add(this);
        mainPanel.add(addButton);
    }
}
/* 1. stock table 검색 후 없으면 삽입

String userid;
String stockName = stock;
String stockcode;

// SQL 쿼리 실행
String query = "SELECT * FROM stock WHERE NAME = '" + stock + "'";
try {
    Statement selectStatement = connection.createStatement();
    ResultSet resultSet = selectStatement.executeQuery(query);

    // 결과가 비어 있는지 확인
    if (!resultSet.next()) {
        // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
        String insertQuery = "INSERT INTO stock (CODE, NAME) VALUES ('" + stock + "', '" + code + "')";
        Statement insertStatement = connection.createStatement();
        insertStatement.executeUpdate(insertQuery);
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

1) 매수일 경우
2. buystock table에 insert

try {
    // PreparedStatement 생성
    String insertQuery = "INSERT INTO buystock (U_ID, CODE, COMPANY, B_DATE, B_PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

    // 값을 설정합니다. 여기서 1, 2, 3은 각각 ?에 해당하는 순서입니다.
    preparedStatement.setString(1, id);
    preparedStatement.setString(2, code);
    preparedStatement.setString(3, "value3");
    preparedStatement.setString(4, "value3");
    preparedStatement.setString(5, "value3");
    preparedStatement.setString(6, "value3");
    preparedStatement.setString(7, "value3");

    // 쿼리 실행
    preparedStatement.executeUpdate();

    System.out.println("데이터가 성공적으로 삽입되었습니다.");
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

3. log table에 insert
try {
    // PreparedStatement 생성
    String insertQuery = "INSERT INTO log (U_ID, CODE, COMPANY, BUYORSELL, DATE, PRICE, QTY, MEMO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

    preparedStatement.setString(1, id);
    preparedStatement.setString(2, code);
    preparedStatement.setString(3, "value3");
    preparedStatement.setString(4, "매수");
    preparedStatement.setString(5, "value3");
    preparedStatement.setString(6, "value3");
    preparedStatement.setString(7, "value3");
    preparedStatement.setString(8, "value3");

    // 쿼리 실행
    preparedStatement.executeUpdate();

    System.out.println("데이터가 성공적으로 삽입되었습니다.");
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}

4. have table에 insert(null일 경우에만, 아닐 때는 update)
// SQL 쿼리 실행
String query = "SELECT * FROM have WHERE U_ID = '" + stock + "' AND CODE = '" + code + "' AND COMPANY = '" + ? + "'";
try {
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, "company"); // company

    ResultSet resultSet = preparedStatement.executeQuery();

    while (resultSet.next()) {
        id = resultSet.getObject(1);
        stockName = resultSet.getObject(1).toString(); // Object를 String으로 변환하여 stockName에 저장
        code = resultSet.getObject(2);
        company = resultSet.getObject(3);
        Date = resultSet.getObject(4);
        e_price
        qty
    }

    // 결과가 비어 있는지 확인
    if (!resultSet.next()) {
        // 여기에 쿼리 결과가 비어있을 때 실행할 INSERT 쿼리를 작성하고 실행합니다.
        String insertQuery = "INSERT INTO have (U_ID, CODE, COMPANY, DATE, E_PRICE, QTY) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

        insertStatement.setString(1, "value1");
        insertStatement.setString(2, "value2");
        insertStatement.setString(3, "value3");
        insertStatement.setString(4, "value4");
        insertStatement.setString(5, "value4");
        insertStatement.setString(6, "value4");

        // 쿼리 실행
        insertStatement.executeUpdate();
    }
    else {
        // 결과가 있으면 UPDATE 실행
        String updateQuery = "UPDATE have SET column1 = ?, column2 = ?, column3 = ?, column4 = ? WHERE U_ID = ? AND CODE = ? AND COMPANY = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setString(1, "newValue1");
        updateStatement.setString(2, "newValue2");
        updateStatement.setString(3, "newValue3");
        updateStatement.setString(4, "newValue4");
        updateStatement.setString(5, id);
        updateStatement.setString(6, code);
        updateStatement.setString(7, "newValue5");
        updateStatement.executeUpdate();
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    // 연결 닫기 등의 마무리 작업
    dbConnector.closeConnection();
}
* */