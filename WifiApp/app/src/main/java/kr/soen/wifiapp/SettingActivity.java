package kr.soen.wifiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static final int REQUEST_ID_AND_PASSWORD = 1;
    public static final int RESULT_ID_AND_PASSWORD = 2;

    SharedPreferences pref;

    TextView title;

    ListView listview ;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        title = (TextView)findViewById(R.id.text3);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        String userid = pref.getString("id", "");
        if(!userid.equals(""))
        {
            // 첫 번째 아이템 추가.
            adapter.addItem("계정", userid) ;
            if (pref.getBoolean("islock", false))
            {
                // 두 번째 아이템 추가.
                adapter.addItem("잠금 설정", "ON") ;
            }
            else
            {
                // 두 번째 아이템 추가.
                adapter.addItem("잠금 설정", "OFF") ;
            }
        }
        else
        {
            // 첫 번째 아이템 추가.
            adapter.addItem("계정", "계정을 등록해주세요") ;
            // 두 번째 아이템 추가.
            adapter.addItem("잠금 설정", "OFF") ;
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

        switch (position)
        {
            case 0:
                if (pref.getString("id", "").equals(""))
                {
                    Intent intent = new Intent(SettingActivity.this, SignUpActivity.class);
                    startActivityForResult(intent, REQUEST_ID_AND_PASSWORD);
                    break;
                }

                break;
            case 1:
                if (!pref.getString("id", "").equals(""))
                {
                    if (pref.getBoolean("islock", false))
                    {
                        item.setDesc("OFF");
                        adapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("islock", false);
                        editor.apply();
                        break;
                    }
                    else
                    {
                        item.setDesc("ON");
                        adapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("islock", true);
                        editor.apply();
                        break;
                    }
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ID_AND_PASSWORD)
        {
            if(resultCode == RESULT_ID_AND_PASSWORD)
            {
                final String id = data.getStringExtra("id");
                String password = data.getStringExtra("password");

                ListViewItem item = (ListViewItem)listview.getItemAtPosition(0);
                item.setDesc(id);
                adapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("id", id);
                editor.putString("password", password);
                editor.commit();
            }
        }
    }
}
