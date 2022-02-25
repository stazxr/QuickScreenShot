package com.github.stazxr.quickscreen.client;

import com.github.stazxr.quickscreen.util.CommonUtil;
import com.github.stazxr.quickscreen.util.FileUtil;
import com.github.stazxr.quickscreen.util.StringUtil;
import com.melloware.jintellitype.JIntellitype;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScreenClient {
    // 主窗体默认宽度
    private static final int DEFAULT_FRAME_WIDTH = 180;

    // 主窗体默认高度
    private static final int DEFAULT_FRAME_HEIGHT = 95;

    // 文件后缀
    private static final String DEFAULT_IMAGE_SUFFIX = ".png";

    /**
     * 文件时间戳格式
     */
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");

    // 主窗体
    private final JFrame frame = new JFrame();

    // 主面板
    private final JPanel mainPanel = new JPanel();

    // 截屏按钮
    private final JButton cutBtn = new JButton("Cut");

    // 打开文件夹按钮
    private final JButton openBtn = new JButton("Open");

    // 设置存储文件的关键字
    private final JTextField textField = new JTextField();

    // 截图保存路径
    private final String savePath = "D:\\ScreenSave\\";

    // 防止启动多个线程
    private boolean isCuttingByThread = false;

    // 是否终止当前运行线程
    private boolean stopThread = false;

    // 快捷键截图一个
    private static final int FUNC_KEY_CUT_ONE = 1;

    // 快捷键开启线程截图
    private static final int FUNC_KEY_START_THREAD = 2;

    // 快捷键停止线程截图
    private static final int FUNC_KEY_STOP_THREAD = 3;

    // 快捷键工具退出
    private static final int FUNC_KEY_EXIT_TOOL = 4;

    /**
     * 获取ScreenClient实例
     *
     * @return ScreenClient
     */
    public static ScreenClient getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 设置窗体是否可见
     * 
     * @param visible 是否可见
     */
    public void setFrameVisible(boolean visible) {
        frame.setVisible(visible);
    }

    private static class InstanceHolder {
        private static final ScreenClient instance = new ScreenClient();
    }

    private ScreenClient() {
        init();
    }

    private void init() {
        initEnv();

        initComponent();

        addListener();

        initFrame();
    }

    private void initEnv() {
        File file = new File(savePath);
        if (!file.exists() && !file.mkdirs()) {
            System.exit(1);
        }
    }

    private void initComponent() {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1, 2));
        addButton(btnPanel, cutBtn, event -> cutScreen());
        addButton(btnPanel, openBtn, event -> openDir());

        mainPanel.setLayout(new GridLayout(2, 1));
        mainPanel.add(textField);
        mainPanel.add(btnPanel);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void addButton(JComponent component, JButton button, ActionListener listener) {
        component.add(button);
        button.addActionListener(listener);
    }

    private void openDir() {
        FileUtil.openFile(savePath);
    }

    private void cutScreen() {
        Thread thread = new Thread(() -> {
            String timeStr = formatter.format(Calendar.getInstance().getTime());
            String saveFile = getSaveFileForFrame(timeStr);
            cutScreen1(saveFile, timeStr);
        });
        
        thread.start();
    }

    private void cutScreen1(String saveFile, String timeStr) {
        // 先隐藏窗口在截屏
        setFrameVisible(false);

        CommonUtil.sleep(5L); // 200张一次偏差
        
        openBtn.setText("Ing...");
        
        savePicture(saveFile);

        String btnTip = timeStr.substring("20210602_23_".length());
        openBtn.setText(btnTip);
        
        setFrameVisible(true);
    }

    private void savePicture(String saveFile) {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);

            //保存路径
            File screenFile = new File(saveFile);
            ImageIO.write(image, "png", screenFile);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private void addListener() {
        int fixMode = JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT;

        // 第一步: 注册热键,第一个参数表示该热键的标识;第二个参数标识组合键,如果没有,则为0;第三个参数为热键
        JIntellitype.getInstance().registerHotKey(FUNC_KEY_CUT_ONE, fixMode, 'Y');
        JIntellitype.getInstance().registerHotKey(FUNC_KEY_START_THREAD, fixMode, 'B');
        JIntellitype.getInstance().registerHotKey(FUNC_KEY_STOP_THREAD, fixMode, 'E');
        JIntellitype.getInstance().registerHotKey(FUNC_KEY_EXIT_TOOL, fixMode, 'Q');

        JIntellitype.getInstance().addHotKeyListener(markCode -> {
            try {
                switch (markCode) {
                    case FUNC_KEY_CUT_ONE:
                        String timeStr = formatter.format(Calendar.getInstance().getTime());
                        String savePath = getSaveFileForBoard(timeStr);
                        cutScreen1(savePath, timeStr);
                        break;
                    case FUNC_KEY_START_THREAD:
                        cutOneGroup();
                        break;
                    case FUNC_KEY_STOP_THREAD:
                        stopThread = true;
                        break;
                    case FUNC_KEY_EXIT_TOOL:
                        System.exit(0);
                        break;
                    default:
                        break;
                }

            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        });
    }

    private void cutOneGroup() {
        if (isCuttingByThread) {
            System.out.println("当前有线程正在抓取,请稍等");
            return;
        }

        Runnable runnable = () -> {
            int count = 0;

            try {
                isCuttingByThread = true;
                stopThread = false;

                while (true) {
                    if (stopThread) {
                        System.out.println("终止线程");
                        isCuttingByThread = false;
                        break;
                    }

                    CommonUtil.sleep(1500L);

                    String timeStr = formatter.format(Calendar.getInstance().getTime());
                    String savePath = getSaveFileForThread(timeStr);
                    cutScreen1(savePath, timeStr);

                    if (count++ > 10000) {
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        };

        Thread t = new Thread(runnable);
        t.start();
    }

    private void initFrame() {
        frame.setTitle("QuickScreen");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置还原的时候的大小
        Dimension dimension = new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        frame.setSize(dimension);

        // 设置位于右下脚
        setFrameLocation();

        // 总是显示在最前面，不需要人工切换窗口，能较大提升效率
        frame.setAlwaysOnTop(true);

        // 不允许改变大小
        frame.setResizable(false);

        frame.setIconImage(new ImageIcon("").getImage());
    }

    private void setFrameLocation() {
        //获取本地图形环境对象
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        //获取图形设备对象，图形设备可能存在多个，默认只取第一个
        GraphicsDevice[] gs = ge.getScreenDevices();

        //获取图形设备的图形设置对象，图形设置也可能存在多个，默认只取第一个
        GraphicsConfiguration[] gc = gs[0].getConfigurations();

        int xFix = 20;
        int yFix = 40;
        int width = gc[0].getBounds().width - DEFAULT_FRAME_WIDTH - xFix;
        int height = gc[0].getBounds().height - DEFAULT_FRAME_HEIGHT - yFix;
        frame.setLocation(width, height);
    }

    private String getSaveFileForThread(String timeStr) {
        String txt = textField.getText();
        if (StringUtil.isNullOrTrimmedEmpty(txt)) {
            txt = "_NoName";
        } else {
            txt = "_".concat(txt.replace(':', ' '));
        }

        String dir = savePath.concat("ThreadSave\\").concat(txt.trim()).concat("\\");
        String file = timeStr.concat("_Thread").concat(getSaveFileName());
        return dir.concat(file);
    }

    private String getSaveFileForBoard(String timeStr) {
        return savePath.concat(timeStr).concat("_KeyBoard").concat(getSaveFileName());
    }

    private String getSaveFileForFrame(String timeStr) {
        return savePath.concat(timeStr).concat(getSaveFileName());
    }

    private String getSaveFileName() {
        String txt = textField.getText();
        if (StringUtil.isNullOrTrimmedEmpty(txt)) {
            txt = "_NoName";
        } else {
            txt = "_".concat(txt.replace(':', ' '));
        }

        txt = txt.trim();
        txt += DEFAULT_IMAGE_SUFFIX;
        return txt;
    }
}
