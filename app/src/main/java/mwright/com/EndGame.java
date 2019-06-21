package mwright.com;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class EndGame extends AppCompatActivity {
    private Globals g=Globals.getInstance();//Get global variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        Intent intent = getIntent();//Get intent from previous activity
        int pScore=g.getPScore();
        int cScore=g.getCScore();
        TextView victoryText=findViewById(R.id.VictoryDisplay);//Get location to display player score
        if(pScore>cScore){//Player victory
            victoryText.setText("Player wins with final score of: "+String.valueOf(pScore));
        }else if(pScore<cScore){//Computer Victory
            victoryText.setText("Computer wins with final score of: "+String.valueOf(cScore));
        }else{//Draw with equal scores
            victoryText.setText("Game ends in tie with both sides having a final score of: "+String.valueOf(pScore));
        }
    }
    public void onYes(View v){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void onNo(View v){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
