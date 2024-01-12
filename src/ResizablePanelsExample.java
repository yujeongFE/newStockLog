import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResizablePanelsExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Resizable Panels - Border Dragging and Panel Dragging Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(2, 2));

        CustomResizablePanel panel1 = new CustomResizablePanel();
        CustomResizablePanel panel2 = new CustomResizablePanel();
        CustomResizablePanel panel3 = new CustomResizablePanel();
        CustomResizablePanel panel4 = new CustomResizablePanel();

        panel1.setBackground(Color.RED);
        panel2.setBackground(Color.GREEN);
        panel3.setBackground(Color.BLUE);
        panel4.setBackground(Color.YELLOW);

        frame.add(panel1);
        frame.add(panel2);
        frame.add(panel3);
        frame.add(panel4);

        frame.setVisible(true);
    }
}

class CustomResizablePanel extends JPanel {
    private int borderWidth = 5; // 테두리 두께 설정
    private Point initialClick;
    private boolean dragging = false;
    private boolean resize = false;

    public CustomResizablePanel() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                dragging = true;
                resize = (e.getX() > getWidth() - borderWidth || e.getY() > getHeight() - borderWidth);
            }

            public void mouseReleased(MouseEvent e) {
                dragging = false;
                resize = false;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;

                    if (resize) {
                        // 테두리를 드래그하여 크기 조절
                        int newWidth = getWidth() + dx;
                        int newHeight = getHeight() + dy;

                        // 크기가 0 이하로 줄어들지 않도록 설정
                        if (newWidth > 0 && newHeight > 0) {
                            setSize(newWidth, newHeight);
                        }
                    } else {
                        // 패널을 클릭하여 드래그하여 위치 조절
                        Point newLocation = new Point(getLocation().x + dx, getLocation().y + dy);
                        setLocation(newLocation);
                    }
                    revalidate();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    public Insets getInsets() {
        return new Insets(borderWidth, borderWidth, borderWidth, borderWidth);
    }
}