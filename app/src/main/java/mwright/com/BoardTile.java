package mwright.com;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class BoardTile extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {
    public static final int TILE_SIZE = 100;//Default size of tile
    private Character letter='a';//Storage for character in tile, and definition of default value
    private int pointValue= 0;//Storage for tile's point value, and definition of default value
    private SpannableStringBuilder mSSBuilder;//Spannable String Builder used for subscript display
    private String mText;//String used to display tile's letter and value
    private boolean horizontalF=false;//Boolean used as flag to determine if part of vertical or horizontally displayed word, initially set to false for test purposes
    private boolean living=true;//Flag to check if tile can be used to make moves
    //private final InternalListener listenerAdapter = new InternalListener();
    private Drawable image = null;
    private tileIndex tIndex;//Tile's location on board.
    public BoardTile(Context context, Character letterI) {
        super(context);
        this.letter = Character.toUpperCase(letterI);//Ensure letter on tile is uppercase.
        setPoints();//Assign point value to tile based on character
        if(pointValue!=0) {//If point value was assigned correctly.
            mText = letter.toString() + pointValue;//Define text that should be on tile.
            mSSBuilder = new SpannableStringBuilder(mText);//Store text in Spannable String Builder
            SubscriptSpan subscriptSpan = new SubscriptSpan();
            mSSBuilder.setSpan(
                    subscriptSpan, // Span to add
                    mText.indexOf(String.valueOf(pointValue)), // Start of the span (inclusive)
                    mText.indexOf(String.valueOf(pointValue)) + String.valueOf(String.valueOf(pointValue)).length(), // End of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            );
            showSmallSizeText(String.valueOf(pointValue));//Make point value a small subscript on tile.
            setText(mSSBuilder);//Place modified text on tile.
        }else {//Debugging test to ensure code can run properly if value is not assigned correctly.
            setText(letter.toString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);//Center text in tile
        }
        //Declare and set height and width of tile
        setHeight(TILE_SIZE);
        setWidth(TILE_SIZE);
        //Log.d("Size test",getWidth()+" "+getHeight());
        setTextSize(30);//Set size of text in tile
        setBackgroundColor(Color.rgb(255, 5, 200));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image= getResources().getDrawable(R.drawable.rounded_corners,null);//Get drawable that stores the beveled rounded corner definitions
            setBackground(image);//Set background of tile to the beveled rounded corner
        }
        
        //setFocusable(true);
        if(pointValue!=0) {
            setClickable(true);//Define tile as clickable
        }else{
            setClickable(false);
        }
        tIndex=new tileIndex(0,0);
//        setOnClickListener(listenerAdapter);
    }
/*
    protected void onFocusChanged(boolean gainFocus, int direct, Rect previouslyFocusedRect){
        if(gainFocus==true){
            this.setBackgroundColor(Color.rgb(255, 165, 0));
        }else{
            this.setBackgroundColor(Color.rgb(0,0,0));
        }
    }
*/

    /*public boolean onTouchEvent(MotionEvent motionEvent) {

        return true;
    }*/
    //Drawing tile and defining its dimensions
    protected void onDraw(Canvas canvas){
        Drawable image=getResources().getDrawable(R.drawable.rounded_corners);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(image);
        }
        image.draw(canvas);
        //Log.d("Size test",getWidth()+" "+getHeight());
        super.onDraw(canvas);
    }

    protected void onMeasure(int widthSpec,int heightSpec){
        setMeasuredDimension(measureWidth(widthSpec),measureHeight(heightSpec));
    }
    private int measureWidth(int measureSpec){
        int preferred = TILE_SIZE ;
        return getMeasurement(measureSpec, preferred);
    }
    private int measureHeight(int measureSpec){
        int preferred = TILE_SIZE ;
        return getMeasurement(measureSpec, preferred);
    }
     private int getMeasurement(int measureSpec, int preferred){
        int specSize=MeasureSpec.getSize(measureSpec);
        int measurement=0;
        switch (MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:
                measurement=specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement=Math.min(preferred,specSize);
                break;
            default:
                measurement=preferred;
                break;
        }
        return measurement;
     }
     public void onClick(View v){//On click function
        Log.d("Click","TileClicked");

     }
     //Point value assignment based on character stored in tile
    public void setPoints(){//Assign point values to tiles in accordance with scrabble rules.
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
                pointValue=0;
                break;
        }
    }
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
    public char getChar(){return letter;}//Get character on tile
    public int getPointValue(){ return pointValue; }//Get point value of tile
    public void setHorizontalF(){horizontalF=true;}
    public boolean isHorizontal(){ return horizontalF; }
    public void settIndex(int x,int y){ tIndex=new tileIndex(x,y); }
    public tileIndex getBoardIndex(){ return tIndex; }
    public void kill(){
        living=false;
        setClickable(false);
    }
    public boolean isLiving(){return living;}

}
