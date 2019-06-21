package mwright.com;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;


import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

public class LetterTile extends android.support.v7.widget.AppCompatTextView {
    public static final int TILE_SIZE = 100;//Default size of tile
    private Character letter;//Storage for character in tile, and definition of default value
    private boolean frozen;//Boolean value used to define root node for word creation
    private int pointValue= 0;//Storage for tile's point value, and definition of default value
    private SpannableStringBuilder mSSBuilder;//Spannable String Builder used for subscript display
    private String mText;//String used to display tile's letter and value
    public LetterTile(Context context, Character letterI) {
        super(context);
        this.letter = Character.toUpperCase(letterI);//Ensure letter on tile is uppercase.
        setPoints();
        if(pointValue!=0) {//If point value was assigned correctly.
            mText = letter.toString() + pointValue;//Define text that should be on tile.
            mSSBuilder = new SpannableStringBuilder(mText);
            SubscriptSpan subscriptSpan = new SubscriptSpan();
            mSSBuilder.setSpan(
                    subscriptSpan, // Span to add
                    mText.indexOf(String.valueOf(pointValue)), // Start of the span (inclusive)
                    mText.indexOf(String.valueOf(pointValue)) + String.valueOf(String.valueOf(pointValue)).length(), // End of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            );
            showSmallSizeText(String.valueOf(pointValue));//Make point value a small subscript on tile.
            setText(mSSBuilder);//Place modified text on tile.
        }else {//Debugging test to ensure code can run properly is value is not assigned correctly.
            setText(letter.toString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        //Declare and set height and width of tile
        setHeight(TILE_SIZE);
        setWidth(TILE_SIZE);

        setTextSize(30);//Set size of text in tile
        //setBackgroundColor(Color.rgb(255, 255, 200));
        Drawable image= getResources().getDrawable(R.drawable.rounded_corners);//Get drawable that stores the beveled rounded corner definitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(image);//Set background of tile to the beveled rounded corner
        }
        //Log.d("Size", String.valueOf(getHeight())+" "+String.valueOf(getWidth()));
        //Log.d("Size","Height:"+getHeight()+".Width:"+getWidth());
    }
    //Point value assignment based on character stored in tile
    public void setPoints(){
        switch(letter){
            case 'A':
                pointValue=1;
                break;
            case 'B':
                pointValue=3;
                break;
            case 'C':
                pointValue=3;
                break;
            case 'D':
                pointValue=2;
                break;
            case 'E':
                pointValue=1;
                break;
            case 'F':
                pointValue=4;
                break;
            case 'G':
                pointValue=2;
                break;
            case 'H':
                pointValue=4;
                break;
            case 'I':
                pointValue=1;
                break;
            case 'J':
                pointValue=8;
                break;
            case 'K':
                pointValue=5;
                break;
            case 'L':
                pointValue=1;
                break;
            case 'M':
                pointValue=3;
                break;
            case 'N':
                pointValue=1;
                break;
            case 'O':
                pointValue=1;
                break;
            case 'P':
                pointValue=3;
                break;
            case 'Q':
                pointValue=10;
                break;
            case 'R':
                pointValue=1;
                break;
            case 'S':
                pointValue=1;
                break;
            case 'T':
                pointValue=1;
                break;
            case 'U':
                pointValue=1;
                break;
            case 'V':
                pointValue=4;
                break;
            case 'W':
                pointValue=4;
                break;
            case 'X':
                pointValue=8;
                break;
            case 'Y':
                pointValue=4;
                break;
            case 'Z':
                pointValue=10;
                break;
            default:
                break;
        }
    }
    public char getChar(){return letter;}//Get character on tile
    public int getPointValue(){
        return pointValue;
    }//Get point value of tile
    protected void showSmallSizeText(String textToSmall){
        // Initialize a new RelativeSizeSpan instance
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(.5f);

        // Apply the RelativeSizeSpan to display small text
        mSSBuilder.setSpan(
                relativeSizeSpan,
                mText.indexOf(textToSmall),
                mText.indexOf(textToSmall) + String.valueOf(textToSmall).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
    public void moveToViewGroup(ViewGroup targetView) {

        ViewParent parent = getParent();
        ViewGroup owner = (ViewGroup) parent;
        if(!frozen) {
            owner.removeView(this);
            LinearLayout container = (LinearLayout) targetView;
            container.addView(this);
            this.setVisibility(View.VISIBLE);
        }else if(frozen&&targetView==parent){
            //Log.d("Test","Rearranging creator");
            owner.removeView(this);
            LinearLayout container = (LinearLayout) targetView;
            container.addView(this);
            this.setVisibility(View.VISIBLE);
        }else{
            //Log.d("Test","Unable to add root to hand");
        }
    }

    public void freeze() {
        frozen = true;
    }//Define tile as root of word being constructed
    public void unfreeze() {
        frozen = false;
    }
    public boolean isRoot(){
        return frozen;
    }//Check if tile is the root of word being made.


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        /*
          frozen!=true&&
         */
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return startDragAndDrop(ClipData.newPlainText("", ""),new DragShadowBuilder(this),this,0);
            }
        }
        return super.onTouchEvent(motionEvent);
    }

}
