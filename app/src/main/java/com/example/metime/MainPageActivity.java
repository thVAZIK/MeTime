package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Fragments.SidePanelDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class MainPageActivity extends AppCompatActivity {
    LinearLayout SearchButtonLL;
    ImageView MenuBtn;

    private void init() {
        SearchButtonLL = findViewById(R.id.SearchButtonLL);
        MenuBtn = findViewById(R.id.MenuBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        SearchButtonLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });

        MenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SidePanelDialogFragment sidePanel = new SidePanelDialogFragment();
                //TODO
//                sidePanel.setOnNavigationItemSelectedListener(MainPageActivity.this);
                //TODO
                sidePanel.show(getSupportFragmentManager(), "SidePanelDialogFragment");
            }
        });
    }
}