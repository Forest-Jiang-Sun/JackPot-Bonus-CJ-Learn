package com.aspectgaming.util;

import static com.sun.jna.platform.win32.WinUser.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.W32APIOptions;

/**
 * @author ligang.yao
 */
public class WindowUtil {

    public static final int SWP_SHOWWINDOW = 0x0040;

    public static final int WS_EX_TOOLWINDOW = 0x00000080;
    public static final int WS_EX_APPWINDOW = 0x00040000;

    public static final int ASFW_ANY = -1;

    public static HWND HWND_TOPMOST = new HWND(Pointer.createConstant(-1));
    public static HWND HWND_NOTOPMOST = new HWND(Pointer.createConstant(-2));

    public interface User32Ex extends User32 {

        public static final User32Ex INSTANCE = (User32Ex) Native.loadLibrary("user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

        public abstract boolean AllowSetForegroundWindow(int dwProcessId);
    }

    public static HWND find(String windowTitle) {
        return User32Ex.INSTANCE.FindWindow(null, windowTitle);
    }

    public static void allowSetForeground() {
        User32Ex.INSTANCE.AllowSetForegroundWindow(ASFW_ANY);
    }

    public static void setForeground(HWND hwnd) {
        User32Ex.INSTANCE.SetForegroundWindow(hwnd);
    }

    public static void show(HWND hwnd) {
        User32Ex.INSTANCE.ShowWindow(hwnd, SW_SHOWNA);
    }

    public static void showMaximized(HWND hwnd) {
        User32Ex.INSTANCE.ShowWindow(hwnd, SW_MAXIMIZE);
    }

    public static void showInTaskBar(HWND hwnd) {
        int style = User32Ex.INSTANCE.GetWindowLong(hwnd, GWL_EXSTYLE);

        style |= WS_EX_APPWINDOW;
        style &= ~WS_EX_TOOLWINDOW;

        User32Ex.INSTANCE.SetWindowLong(hwnd, GWL_EXSTYLE, style);
    }

    public static void hideInTaskBar(HWND hwnd) {
        int style = User32Ex.INSTANCE.GetWindowLong(hwnd, GWL_EXSTYLE);

        style |= WS_EX_TOOLWINDOW;
        style &= ~WS_EX_APPWINDOW;

        User32Ex.INSTANCE.SetWindowLong(hwnd, GWL_EXSTYLE, style);
    }

    public static void hide(HWND hwnd) {
        User32Ex.INSTANCE.ShowWindow(hwnd, SW_HIDE);
    }

    public static void setTopMostMode(HWND hwnd) {
        RECT rect = new RECT();
        User32Ex.INSTANCE.GetWindowRect(hwnd, rect);
        User32Ex.INSTANCE.SetWindowPos(hwnd, HWND_TOPMOST, rect.left, rect.top, (rect.right - rect.left), (rect.bottom - rect.top), SWP_SHOWWINDOW);
    }

    public static void setNormalMode(HWND hwnd) {
        RECT rect = new RECT();
        User32Ex.INSTANCE.GetWindowRect(hwnd, rect);
        User32Ex.INSTANCE.SetWindowPos(hwnd, HWND_NOTOPMOST, rect.left, rect.top, (rect.right - rect.left), (rect.bottom - rect.top), SWP_SHOWWINDOW);
    }
}
