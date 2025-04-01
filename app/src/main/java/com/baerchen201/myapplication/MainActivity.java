package com.baerchen201.myapplication;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  public int num = 0;
  @Nullable private Toast t;

  public String[] values;

  private List<TextView> valueViews;

  private MediaPlayer m;
  private MediaPlayer bgm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    ((Spinner) findViewById(R.id.spinner))
        .setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                  changeBgm(
                      getResources()
                          .openRawResourceFd(
                              getResources()
                                  .getIdentifier(
                                      ((TextView) view).getText().toString(),
                                      "raw",
                                      getPackageName())));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {
                bgm.stop();
              }
            });

    m = MediaPlayer.create(getApplicationContext(), R.raw.vineboom);
    bgm = MediaPlayer.create(getApplicationContext(), R.raw.elevator);
    bgm.setLooping(true);

    values = getResources().getStringArray(R.array.values);

    ((Button) findViewById(R.id.button)).setOnClickListener(this::onButtonClick);

    LinearLayout valueList = (LinearLayout) findViewById(R.id.valueList);
    valueViews = new ArrayList<TextView>();
    for (String i : values) {
      TextView textView = new TextView(getApplicationContext());
      textView.setText(i);
      textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
      valueList.addView(textView);
      valueViews.add(textView);
    }

    updateTextView();
  }

  @Override
  public void onResume() {
    super.onResume();
    try {
      bgm.start();
    } catch (IllegalStateException ignored) {
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    bgm.pause();
  }

  private void onButtonClick(View v) {
    num++;

    if (t != null) t.cancel();
    t = new Toast(getApplicationContext());
    t.setText(String.format("Hello, %s!", value()));
    t.setDuration(Toast.LENGTH_LONG);
    t.show();

    updateTextView();
    playSound();
  }

  private void updateTextView() {
    ((TextView) findViewById(R.id.textView)).setText(String.format("%s", num));

    for (int i = 0; i < valueViews.size(); i++) {
      valueViews
          .get(i)
          .setTextColor(
              getResources()
                  .getColor(i == valueId() ? R.color.active : R.color.inactive, getTheme()));
    }
  }

  public String value(int num) {
    return values[valueId(num)];
  }

  public String value() {
    return value(num);
  }

  public int valueId(int num) {
    return (num - 1) % values.length;
  }

  public int valueId() {
    return (num - 1) % values.length;
  }

  private void playSound() {
    m.stop();
    try {
      m.prepare();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    m.start();
  }

  private void changeBgm(AssetFileDescriptor fd) throws IOException {
    bgm.stop();
    bgm.reset();
    bgm.setLooping(true);
    bgm.setDataSource(fd);
    bgm.prepare();
    bgm.start();
  }
}
