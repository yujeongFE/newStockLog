package stocklogmanipulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static stocklogmanipulation.Panel5Action.tableModel;

public class SellBuy {

    private JPanel sellbuylog;
    private SellBuyPanel sp;
    private CardLayout card;
    private String selectedStockName;

    public static void main(String[] args) {
        SellBuy sb = new SellBuy();
        sb.setFrame(sb);
    }

    public void setSelectedStockName(String stockName) {
        selectedStockName = stockName;
    }

    public void setFrame(SellBuy sb) {
        JFrame jf = new JFrame();
        sp = new SellBuyPanel(this);


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

    public SellBuyPanel(SellBuy sp) {
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


                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        System.out.print(tableModel.getValueAt(i, j) + " ");
                    }
                    System.out.println();
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
