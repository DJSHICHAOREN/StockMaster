package com.example.stockmaster.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class ClipBoardUtil {
    public static void CopyStringToClipBoard(Context context, String msg){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", msg);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);

        Toast.makeText(context,"已复制到剪切板", Toast.LENGTH_LONG).show();
    }
}
