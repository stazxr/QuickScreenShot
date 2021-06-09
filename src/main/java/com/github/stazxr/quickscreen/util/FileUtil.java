package com.github.stazxr.quickscreen.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static void openFile(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists() && !file.mkdirs()) {
                throw new IOException("目录不存在，且创建失败");
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}
