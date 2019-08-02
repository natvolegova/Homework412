package com.example.homework412;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView list;
    private SwipeRefreshLayout swipe_refresh;
    private static final String KEY_NAME = "name";
    private static final String KEY_COUNT = "count";
    private String[] array_from = {KEY_NAME, KEY_COUNT};
    private int[] array_to = {R.id.text_desc, R.id.count};
    public static final String APP_PREFERENCES = "listtext";
    private List<Map<String, String>> list_content;
    private SharedPreferences mSettings;
    private SimpleAdapter newAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        //заполняем файл Preference
        initPreference();
        //заполняем список элементов
        initList();
        //обработка клика на элементе списка
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                list_content.remove(i);
                newAdapter.notifyDataSetChanged(); //уведомляем адаптер, что данные изменены
            }
        });

        //обработка swipeRefresh
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                newAdapter.notifyDataSetChanged(); //уведомляем адаптер, что данные изменены
                swipe_refresh.setRefreshing(false); //убираем значек обновления
            }
        });
    }

    private void initList() {
        list_content = prepareContent(); //заполняем данные списка
        newAdapter = createAdapter(list_content); //создаем адаптер
        list.setAdapter(newAdapter);
    }

    private SimpleAdapter createAdapter(List<Map<String, String>> content) {
        return new SimpleAdapter(this, content, R.layout.list_item, array_from, array_to);
    }

    //заполняем при первом запуске массив значений из Preference
    private void initPreference() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> result = mSettings.getAll();
        //если массив значений переменных пустой, то заполняем
        if (result.size() == 0) {
            SharedPreferences.Editor editor = mSettings.edit();
            String[] result_text = getString(R.string.large_text).split("\n\n");
            for (int i = 0; i < result_text.length; i++) {
                String setting_name = "text_" + i;
                if (!mSettings.contains(setting_name)) {
                    editor.putString(setting_name, result_text[i]);
                    editor.apply();
                }
            }
            editor.commit();
        }
    }

    //заполняем список значениями из preference
    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> listObject = new ArrayList();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> result = mSettings.getAll();
        for (int i = 0; i < result.size(); i++) {
            HashMap<String, String> resultItem = new HashMap();
            String setting_name = "text_" + i;
            String pref_value = (String) result.get(setting_name);
            int count = pref_value.length();
            resultItem.put(KEY_NAME, pref_value);
            resultItem.put(KEY_COUNT, Integer.toString(count));
            listObject.add(resultItem);
        }
        return listObject;
    }
}
