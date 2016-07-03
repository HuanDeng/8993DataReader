package huan.a8993datareader;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


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
        startstopButton.setText("EXIT");
        timer.schedule(task,1000, 1000);
        audioRecordRx.start();
    }

    Timer timer = new Timer(true);
    AudioRecordRx audioRecordRx=new AudioRecordRx();
    void StartStopButton_onClick(View view)
    {
        audioRecordRx.stop();
        System.exit(0);

        //audioRecordRx.start();
    }
    int addPt=0;
    final Handler handler = new Handler()
     {
             public void handleMessage(Message msg)
             {
                     switch (msg.what)
                     {
                             case 1:
                                 updateParam();
                                 break;
                     }
                    super.handleMessage(msg);
             }
     };
    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    void updateParam()
    {
        velTextView.setText(Short.toString(audioRecordRx.get8993Vel()));
        rhTextView.setText(Short.toString(audioRecordRx.get8993Rh()));
        tairTextView.setText(Short.toString(audioRecordRx.get8993Tair()));
        batTextView.setText(Short.toString(audioRecordRx.get8993Bat()));
        return;
    }

}
