package com.willkong.network.environment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.willkong.network.R;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network.environment
 * @Author: willkong
 * @CreateDate: 2020/5/13 16:45
 * @Description: 环境切换页面
 */
public class EnvironmentActivity extends AppCompatActivity {

    private static Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSetting();
            }
        });
    }

    //点击按钮弹出一个单选对话框
    public void clickSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EnvironmentActivity.this);
        builder.setTitle("设置网络环境");
        final String items[] = {"正式环境", "测试环境"};
        boolean isOfficial = isOfficialEnvironment(applicationContext);
        int checkedItem = isOfficial ? 0 : 1;
        //-1代表没有条目被选中
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.把选中的条目取出来
                String item = items[which];
                saveEnvironment(applicationContext,which);
                Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_LONG).show();
                //2.然后把对话框关闭
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static boolean isOfficialEnvironment(Context context) {
        applicationContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("environment", Context.MODE_PRIVATE); //私有数据
        return sharedPreferences.getBoolean("isOfficial", true);
    }

    private void saveEnvironment(Context context, int type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("environment", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        if (type == 1) {
            editor.putBoolean("isOfficial", false);
        } else {
            editor.putBoolean("isOfficial", true);
        }
        editor.apply();//提交修改
    }
}
