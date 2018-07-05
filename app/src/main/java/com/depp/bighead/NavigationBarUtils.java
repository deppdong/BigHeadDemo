package com.depp.bighead;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangchende@meizu.com on 17-8-7.
 */

public class NavigationBarUtils {
    private static Method setNavigationBarIconColor;
    private static Method setNavigationBarIconColorExt;
    private static Field mFiledMeizuFlags;
    public static int MEIZU_FLAG_DARK_NAVIGATION_BAR_ICON = 1 << 8;
    private static Method setForcedNavigationBarColor;
    private static String DecorViewClsName = "com.android.internal.policy.DecorView";
    private static Field mLastBottomInset;
    private static Field mLastLeftInset;
    private static Field mLastRightInset;
    private static Field mNavigationBarDefaultBlackIcon;
    private static Method setNavigationBarColorExt;

    static {
        try {
            setNavigationBarIconColor = Window.class.getDeclaredMethod("setNavigationBarIconColor", boolean.class);
            mFiledMeizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            setForcedNavigationBarColor = Window.class.getDeclaredMethod("setForcedNavigationBarColor", boolean.class);
            Field field = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_NAVIGATION_BAR_ICON");
            MEIZU_FLAG_DARK_NAVIGATION_BAR_ICON = field.getInt(null);
            setNavigationBarIconColorExt = Window.class.getDeclaredMethod("setNavigationBarIconColor", boolean.class, boolean.class);
            Class<?> PhoneWindow = Class.forName("com.android.internal.policy.PhoneWindow");
            mNavigationBarDefaultBlackIcon = PhoneWindow.getDeclaredField("mNavigationBarDefaultBlackIcon");
            if (mNavigationBarDefaultBlackIcon != null) {
                mNavigationBarDefaultBlackIcon.setAccessible(true);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Class<?> DecorView = Class.forName(DecorViewClsName);
            mLastBottomInset = DecorView.getDeclaredField("mLastBottomInset");
            if (mLastBottomInset != null) {
                mLastBottomInset.setAccessible(true);
            }
            mLastRightInset = DecorView.getDeclaredField("mLastRightInset");
            if (mLastRightInset != null) {
                mLastRightInset.setAccessible(true);
            }
            mLastLeftInset = DecorView.getDeclaredField("mLastLeftInset");
            if (mLastLeftInset != null) {
                mLastLeftInset.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            setNavigationBarColorExt = Window.class.getDeclaredMethod("setNavigationBarColorExt", int.class);
            if (setNavigationBarColorExt != null) {
                setNavigationBarColorExt.setAccessible(true);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置导航栏图标颜色未深色(默认是白色)
     *
     * @param window
     * @param dark   　true 深色　false　白色
     */
    public static void setDarkIconColor(Window window, boolean dark) {
        if (setNavigationBarIconColor != null) {
            try {
                setNavigationBarIconColor.invoke(window, dark);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 设置导航栏图标颜色未深色(默认是白色)
     *
     * @param window
     * @param dark
     * @param force  强制设置
     */
    public static void setDarkIconColor(Window window, boolean dark, boolean force) {
        if (setNavigationBarIconColorExt != null) {
            try {
                setNavigationBarIconColorExt.invoke(window, dark, force);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            setDarkIconColor(window, dark);
        }
    }

    /**
     * 当前是否显示深色导航栏图标
     *
     * @param window
     * @return
     */
    public static boolean isDarkIconColor(Window window) {
        if (mFiledMeizuFlags != null && window != null) {
            try {
                int flag = mFiledMeizuFlags.getInt(window.getAttributes());
                return (flag & MEIZU_FLAG_DARK_NAVIGATION_BAR_ICON) != 0;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取左边导航栏宽度
     *
     * @param context
     * @return
     */
    public static int getLeftNavigationBarWidth(Activity context) {
        if (context != null && context.getWindow() != null) {
            View decorView = context.getWindow().getDecorView();
            try {
                int height = 0;
                if (mLastLeftInset != null) {
                    height = mLastLeftInset.getInt(decorView);
                }
                return height;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取底部导航栏高度
     *
     * @param context
     * @return
     */
    public static int getBottomNavigationBarWidth(Activity context) {
        if (context != null && context.getWindow() != null) {
            View decorView = context.getWindow().getDecorView();
            try {
                if (decorView != null) {
                    int height = 0;
                    if (mLastBottomInset != null) {
                        height = mLastBottomInset.getInt(decorView);
                    }
                    return height;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取右边导航栏高度
     *
     * @param context
     * @return
     */
    public static int getRightNavigationBarHeight(Activity context) {
        if (context != null && context.getWindow() != null) {
            View decorView = context.getWindow().getDecorView();
            try {
                if (decorView != null) {
                    int height = 0;
                    if (mLastRightInset != null) {
                        height = mLastRightInset.getInt(decorView);
                    }
                    return height;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Activity context) {
        if (context != null && context.getWindow() != null) {
            View decorView = context.getWindow().getDecorView();
            try {
                if (decorView != null) {
                    int height = 0;
                    if (mLastBottomInset != null) {
                        height = mLastBottomInset.getInt(decorView);
                    }
                    if (height == 0 && mLastRightInset != null) {
                        height = mLastRightInset.getInt(decorView);
                    }
                    if (height == 0 && mLastLeftInset != null) {
                        height = mLastLeftInset.getInt(decorView);
                    }
                    return height;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 判断当前系统是否有导航栏
     *
     * @return
     */
    public static boolean isHaveNavigationBar(Activity activity) {
        return getNavigationBarHeight(activity) > 0;
    }

    /**
     * 获取系统默认导航栏图标颜色
     *
     * @param window
     * @return
     */
    public static boolean getNavigationBarDefaultBlackIcon(Window window) {
        if (mNavigationBarDefaultBlackIcon != null) {
            try {
                return mNavigationBarDefaultBlackIcon.getBoolean(window);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    /**
     * 设置导航栏颜色
     *
     * @param window
     * @param color
     * @param blackMode
     */
    public static void setNavigationBarColor(Window window, int color, boolean blackMode) {
        setForcedNavigationBarColor(window, blackMode);
        window.setNavigationBarColor(color);
    }

    public static void setNavigationBarColor(Window window, int color) {
        setNavigationBarColor(window, color, true);
    }

    public static void setForcedNavigationBarColor(Window window, boolean forcedNavigationBarColor) {
        if (setForcedNavigationBarColor != null) {
            try {
                setForcedNavigationBarColor.invoke(window, forcedNavigationBarColor);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setNavigationBarColorExt(Window window, int color) {
        if (setNavigationBarColorExt != null) {
            try {
                setNavigationBarColorExt.invoke(window, color);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}

