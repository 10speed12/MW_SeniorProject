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

public class DiscardActivity extends AppCompatActivity {
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private LinkedList<LetterTile> discardL=new LinkedList<>();
    private LinkedList<LetterTile> playerHand=new LinkedList<>();

    private Globals g=Globals.getInstance();//Get global variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discard);//Define which activity layout to load
        Intent intent = getIntent();//Get intent from previous activity
        TextView pScoreDisplay=findViewById(R.id.PScoreDisplay);//Get location to display player score
        pScoreDisplay.setText("Player Score:"+g.getPScore());//Display player score
        TextView compScoreD=findViewById(R.id.CScoreDisplay);//Get location to display computer score
        compScoreD.setText("Computer Score:"+g.getCScore());//Display computer score
        char[] tempHand=intent.getCharArrayExtra(MainActivity.EXTRA_BUNDLE);//Get character array storing player hand contents
        LinearLayout pHand= findViewById(R.id.PlayerHand);//Get view to display hand in
        for(int i=0;i<tempHand.length;i++) {//Iterate through array
            LetterTile temp=new LetterTile(this,tempHand[i]);//Create tile using current character
            playerHand.add(temp);//Store tile in player hand list
            pHand.addView(temp);//Store tile in hand display view
        }
        LinearLayout wordConstructor= findViewById(R.id.WordCreator);//Get view that stores tiles being discarded
        //Set drag listeners for both tile views.
        pHand.setOnDragListener(new DragListener());
        wordConstructor.setOnDragListener(new DragListener());

    }
    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN ) {//&& !stackedLayout.empty()
                //LetterTile tile = (LetterTile) stackedLayout.peek();
                //tile.moveToViewGroup((ViewGroup) v);
                //if (stackedLayout.empty()) {
                //    TextView messageBox = (TextView) findViewById(R.id.message_box);
               //     messageBox.setText(word1 + " " + word2);
                //}
                //placedTiles.push(tile);

                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    //View view = (View) event.getLocalState();
                    LetterTile tile = (LetterTile) event.getLocalState();

                   /* ViewGroup owner = (ViewGroup) tile.getParent();
                    owner.removeView(tile);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(tile);
                    tile.setVisibility(View.VISIBLE);*/
                    ViewParent parent = tile.getParent();
                    LinearLayout pHand= findViewById(R.id.PlayerHand);
                    LinearLayout wordConstructor= findViewById(R.id.WordCreator);
                    if(parent.equals(pHand)&&v.equals(wordConstructor)){
                        for(int i=0;i<playerHand.size();i++){
                            if(playerHand.get(i).getChar()==tile.getChar()){
                                discardL.add(tile);
                                playerHand.remove(i);
                                break;
                            }
                        }
                    }else if(parent.equals(wordConstructor)&& v.equals(pHand)){
                        for(int i=0;i<discardL.size();i++){
                            if(discardL.get(i).getChar()==tile.getChar()){
                                playerHand.add(tile);
                                discardL.remove(i);
                                break;
                            }
                        }
                    }
                    tile.moveToViewGroup((ViewGroup) v);

                    break;
                    default:
                        break;
            }
            return true;
        }
    }
    public void onSubmit(View view){//Submit results of activity back to the main activity
        //discardL.clear();
        Intent intent=new Intent();//New intent to send data back to main activity
        LinearLayout wordConstructor= findViewById(R.id.WordCreator);
        int tempSize=wordConstructor.getChildCount();//Get the amount of discarded tiles
        char[] tempArray=new char[tempSize];//Create character array to store letters pf discarded tiles
        for(int i=0; i<tempSize;i++){//Iterate through discarded tile layout
            ///LetterTile temp=(LetterTile) wordConstructor.getChildAt(i);//Get current tile in layout
            //discardL.add(temp);
            //tempArray[i]=temp.getChar();//Store tile character in array
            tempArray[i]= ((LetterTile) wordConstructor.getChildAt(i)).getChar();//Store character of current tile in array
        }

        intent.putExtra(Intent.EXTRA_TEXT,tempArray);//Store array in intent
        StringBuilder handT=new StringBuilder("");//String builder used to store updated hand contents
        for(int i=0; i<playerHand.size();i++){//Iterate through hand
            ///LetterTile temp=(LetterTile) playerHand.get(i);
            handT.append(playerHand.get(i).getChar());//Add character of current tile to string
        }
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT,handT.toString());//Store string in intent
        setResult(RESULT_OK,intent);//Send successful completion signal and intent to main activity layout.
        finish();//Close activity and return to main.
    }
    public void onBack(View view){
        //Return to previous activity without saving any changes
        setResult(RESULT_CANCELED);//Return unchanged signal to main
        finish();//Close activity and return to main.
    }
}
