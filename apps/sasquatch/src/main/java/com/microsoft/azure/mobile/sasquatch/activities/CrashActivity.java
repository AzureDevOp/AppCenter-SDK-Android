package com.microsoft.azure.mobile.sasquatch.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.azure.mobile.crashes.Crashes;
import com.microsoft.azure.mobile.crashes.model.TestCrashException;
import com.microsoft.azure.mobile.sasquatch.R;

import java.util.Arrays;
import java.util.List;

public class CrashActivity extends AppCompatActivity {

    private final List<Crash> sCrashes = Arrays.asList(
            new Crash(R.string.title_test_crash, R.string.description_test_crash, new Runnable() {

                @Override
                public void run() {
                    Crashes.generateTestCrash();
                    throw new TestCrashException();
                }
            }),
            new Crash(R.string.title_crash_divide_by_0, R.string.title_crash_divide_by_0, new Runnable() {

                @Override
                @SuppressWarnings("ResultOfMethodCallIgnored")
                public void run() {
                    ("" + (42 / Integer.valueOf("0"))).toCharArray();
                }
            }),
            new Crash(R.string.title_test_ui_crash, R.string.description_test_ui_crash, new Runnable() {

                @Override
                @SuppressWarnings("ResultOfMethodCallIgnored")
                public void run() {
                    ListView view = (ListView) findViewById(R.id.list);
                    view.setAdapter(new ArrayAdapter<>(CrashActivity.this, android.R.layout.simple_list_item_2, sCrashes));
                }
            }),
            new Crash(R.string.title_stack_overflow_crash, R.string.description_stack_overflow_crash, new Runnable() {

                @SuppressWarnings("InfiniteRecursion")
                @Override
                public void run() {
                    run();
                }
            })
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<Crash>(this, android.R.layout.simple_list_item_2, android.R.id.text1, sCrashes) {

            @SuppressWarnings("ConstantConditions")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).title);
                ((TextView) view.findViewById(android.R.id.text2)).setText(getItem(position).description);
                return view;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Crash) parent.getItemAtPosition(position)).crashTask.run();
            }
        });
    }

    private static class Crash {
        final int title;

        final int description;

        final Runnable crashTask;

        Crash(int title, int description, Runnable crashTask) {
            this.title = title;
            this.description = description;
            this.crashTask = crashTask;
        }
    }
}
