package com.github.stazxr.quickscreen;

import com.github.stazxr.quickscreen.client.ScreenClient;

/**
 * see more at https://github.com/stazxr/QuickScreenShot
 *
 * @author SunTao
 * @since 0.0.1
 */
public class QuickScreen {
    public static void main(String[] args) {
        ScreenClient.getInstance().setFrameVisible(true);
    }
}
