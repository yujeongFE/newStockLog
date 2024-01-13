package stocklogmanipulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H2_PanelAction5_1 extends JPanel {

    private JTextArea responseTextArea;

    private H2_PanelAction5_1() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField categoryTextField = new JTextField(10);
        JButton fetchButton = new JButton("검색");

        // 첫 번째 레이블
        JLabel firstLabel = new JLabel("카테고리:");
        inputPanel.add(firstLabel);

        // 한 줄 띄우기
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 두 번째 레이블
        JLabel secondLabel = new JLabel("(예시 : category=general)");
        // 등 business, entertainment, general, health, science, sports, technology, sources):
        inputPanel.add(secondLabel);

        inputPanel.add(categoryTextField);
        inputPanel.add(fetchButton);

        responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(responseTextArea);

        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String apiKey = "1277dcdf93f8462a96f2efd5778607ae";
                String category = categoryTextField.getText();

                // 특정 카테고리의 인기 헤드라인 출력
                getTopHeadlines(apiKey, category);
            }
        });

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 뉴스 API로부터 헤드라인을 가져오는 메서드
    private void getTopHeadlines(String apiKey, String parameter) {
        try {
            String apiUrl = "https://newsapi.org/v2/top-headlines";
            String urlParameters = "apiKey=" + apiKey + "&" + parameter;
            URL url = new URL(apiUrl + "?" + urlParameters);

            System.out.println("Request URL: " + url); // 로깅 추가

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // JSON 파싱 후 표시
            showNews(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            // 사용자에게 알림
            JOptionPane.showMessageDialog(null, "Error: Unable to fetch news data. Please check your internet connection.");
        } catch (Exception e) {
            e.printStackTrace();
            // 사용자에게 알림
            JOptionPane.showMessageDialog(null, "An unexpected error occurred. Please try again later.");
        }
    }

    // JSON 데이터를 파싱하여 JTextArea에 표시하는 메서드
    private void showNews(String jsonString) {
        // 정규식을 사용하여 JSON 데이터 파싱
        String titlePattern = "\"title\":\"([^\"]*)\"";
        String descriptionPattern = "\"description\":\"([^\"]*)\"";
        String authorPattern = "\"author\":\"([^\"]*)\"";

        Pattern titleRegex = Pattern.compile(titlePattern);
        Pattern descriptionRegex = Pattern.compile(descriptionPattern);
        Pattern authorRegex = Pattern.compile(authorPattern);

        Matcher titleMatcher = titleRegex.matcher(jsonString);
        Matcher descriptionMatcher = descriptionRegex.matcher(jsonString);
        Matcher authorMatcher = authorRegex.matcher(jsonString);

        List<String> titles = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> authors = new ArrayList<>();

        while (titleMatcher.find()) {
            titles.add(titleMatcher.group(1));
        }

        while (descriptionMatcher.find()) {
            descriptions.add(descriptionMatcher.group(1));
        }

        while (authorMatcher.find()) {
            authors.add(authorMatcher.group(1));
        }

        // JTextArea에 표시
        StringBuilder displayText = new StringBuilder();
        displayText.append(String.format("%-50s%-100s%-30s%n", "Title", "Description", "Author"));

        int maxSize = Math.max(Math.max(titles.size(), descriptions.size()), authors.size());
        for (int i = 0; i < maxSize; i++) {
            String title = i < titles.size() ? titles.get(i) : "";
            String description = i < descriptions.size() ? descriptions.get(i) : "";
            String author = i < authors.size() ? authors.get(i) : "";

            displayText.append(String.format("%-70s%-120s%-45s%n", title, description, author));
        }

        responseTextArea.setText(displayText.toString());
    }

    public static void addFunctionality(JPanel panel) {
        SwingUtilities.invokeLater(() -> {
            H2_PanelAction5_1 h2PanelAction5_1 = new H2_PanelAction5_1();
            panel.add(h2PanelAction5_1);
        });
    }
}