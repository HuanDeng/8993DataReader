package huan.a8993datareader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView velTextView,rhTextView,tairTextView,batTextView,errorTextView;
    Button startstopButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        velTextView=(TextView)findViewById(R.id.VelTextView);
        rhTextView=(TextView)findViewById(R.id.RhTextView);
        tairTextView=(TextView)findViewById(R.id.TairTextView);
        batTextView=(TextView)findViewById(R.id.BatTextView);
        errorTextView=(TextView)findViewById(R.id.ErrorTextView);
        startstopButton=(Button)findViewById(R.id.StartStopButton);
    }
    void StartStopButton_onClick(View view)
    {
        if(startstopButton.getText()=="START READ")
        {
            startstopButton.setText("STOP READ");
        }
        else
        {
            startstopButton.setText("START READ");
        }
        Toast.makeText(this,"Button Click",Toast.LENGTH_SHORT).show();
    }
}
