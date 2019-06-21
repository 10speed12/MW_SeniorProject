package mwright.com;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;
import java.util.Collections;
import java.lang.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;



@SuppressWarnings({"ALL", "WeakerAccess"})
public class MainActivity extends AppCompatActivity {

    //private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private LinkedList<LetterTile> letterBank = new LinkedList<>();//Stores all tiles not currently in either players hands.
    private LinkedList<LetterTile> playerHand = new LinkedList<>();//Stores all tiles in user player's hand
    private LinkedList<LetterTile> compHand = new LinkedList<>();//Stores all tiles in computer player's hand.
    private Dictionary dictionary;
    static final String EXTRA_BUNDLE = "com.mwright.EXTRA_BUNDLE";
    static final String EXTRA_TESTTILE = "com.mwright.EXTRA_TESTTILE";//String to define that a tile is being stored in intent
    static final String EXTRA_INDEX = "com.mwright.EXTRA_INDEX";//String to define that a board tile's location on grid is being stored in intent
    static final int PICK_CONTACT_REQUEST = 0;
    static final int PICK_DISCARD_REQUEST = 1;
    private Globals g = Globals.getInstance();//Get global variables.
    private BoardTile[][] grid = g.getGrid();//Get original instance of global grid on load
    private LinearLayout row1;
    private LinearLayout row2;
    private LinearLayout row3;
    private LinearLayout row4;
    private LinearLayout row5;
    private LinearLayout row6;
    private LinearLayout row7;
    private LinearLayout row8;
    private LinearLayout row9;
    private LinearLayout row10;
    public  int pWordsMade=g.getpOutCount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//Define which activity layout to load
        AssetManager assetManager = getAssets();
        row1 = findViewById(R.id.Row1);
        row2 = findViewById(R.id.Row2);
        row3 = findViewById(R.id.Row3);
        row4 = findViewById(R.id.Row4);
        row5 = findViewById(R.id.Row5);
        row6 = findViewById(R.id.Row6);
        row7 = findViewById(R.id.Row7);
        row8 = findViewById(R.id.Row8);
        row9 = findViewById(R.id.Row9);
        row10 = findViewById(R.id.Row10);
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new Dictionary(new InputStreamReader(inputStream));
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        g.setDictionary(dictionary);
        initiateBank();//Call to create letter bank
        playerDraw();//Call to create player's opening hand.
        compDraw();//Call to create computer's opening hand.

        g.setPlayerScore(0);//Set initial value of player score to 0.
        g.setCompScore(0);//Set initial value of computer score to 0.
        TextView pScoreDisplay = findViewById(R.id.PScoreDisplay);//Get location to display player score
        pScoreDisplay.setText("Player Score:" + g.getPScore());//Display player score
        TextView compScoreD = findViewById(R.id.CScoreDisplay);//Get location to display computer score
        compScoreD.setText("Computer Score:" + g.getCScore());//Display computer score
        Log.d("Player Score:", String.valueOf(g.getPScore()));

        //LinearLayout wordGrid= findViewById(R.id.WordGridParent);

        //Creation of test board tile(s)
        createStartGrid();
        g.setGrid(grid);//Update global grid with new info
        setStartWord();
        Button finish = (Button)findViewById(R.id.Finish);
        finish.setClickable(false);
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                return true;
            }
            return false;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void initiateBank() {//Create the letter bank to satisfy the Scrabble rules.
        for (int i = 0; i < 9; i++) {//add a's to letter bank.
            LetterTile temp = new LetterTile(this, 'a');
            letterBank.add(temp);
        }
        //add b's to bank
        LetterTile temp = new LetterTile(this, 'b');
        letterBank.add(temp);
        letterBank.add(temp);
        //add c's
        temp = new LetterTile(this, 'c');
        letterBank.add(temp);
        letterBank.add(temp);
        //add d's
        for (int i = 0; i < 4; i++) {
            temp = new LetterTile(this, 'd');
            letterBank.add(temp);
        }
        for (int i = 0; i < 12; i++) {//add e's
            temp = new LetterTile(this, 'e');
            letterBank.add(temp);
        }
        //add f's
        temp = new LetterTile(this, 'f');
        letterBank.add(temp);
        letterBank.add(temp);
        for (int i = 0; i < 4; i++) {//add g's
            temp = new LetterTile(this, 'g');
            letterBank.add(temp);
        }
        //add h's
        temp = new LetterTile(this, 'h');
        letterBank.add(temp);
        letterBank.add(temp);
        for (int i = 0; i < 9; i++) {//add i's
            temp = new LetterTile(this, 'i');
            letterBank.add(temp);
        }
        LetterTile k = new LetterTile(this, 'j');
        letterBank.add(k);
        k = new LetterTile(this, 'k');
        letterBank.add(k);
        for (int i = 0; i < 4; i++) {//add l's
            temp = new LetterTile(this, 'l');
            letterBank.add(temp);
        }
        //add m's
        temp = new LetterTile(this, 'm');
        letterBank.add(temp);
        letterBank.add(temp);
        for (int i = 0; i < 6; i++) {//add n's
            temp = new LetterTile(this, 'n');
            letterBank.add(temp);
        }
        for (int i = 0; i < 8; i++) {//add o's
            temp = new LetterTile(this, 'o');
            letterBank.add(temp);
        }
        //add p's
        temp = new LetterTile(this, 'p');
        letterBank.add(temp);
        letterBank.add(temp);
        k = new LetterTile(this, 'q');//add q
        letterBank.add(k);
        for (int i = 0; i < 6; i++) {//add r's
            temp = new LetterTile(this, 'r');
            letterBank.add(temp);
        }
        for (int i = 0; i < 4; i++) {//add s's
            temp = new LetterTile(this, 's');
            letterBank.add(temp);
        }
        for (int i = 0; i < 6; i++) {//add t's
            temp = new LetterTile(this, 't');
            letterBank.add(temp);
        }
        for (int i = 0; i < 4; i++) {//add u's
            temp = new LetterTile(this, 'u');
            letterBank.add(temp);
        }
        //add v's
        temp = new LetterTile(this, 'v');
        letterBank.add(temp);
        letterBank.add(temp);
        //add w's
        temp = new LetterTile(this, 'w');
        letterBank.add(temp);
        letterBank.add(temp);
        k = new LetterTile(this, 'x');//add x
        letterBank.add(k);
        //add y's
        temp = new LetterTile(this, 'y');
        letterBank.add(temp);
        letterBank.add(temp);
        k = new LetterTile(this, 'z');//add z
        letterBank.add(k);
        Collections.shuffle(letterBank);//Shuffle contents of bank to assure random distribution of tiles.

    }

    @SuppressWarnings("WeakerAccess")
    public void playerDraw() {
        while (playerHand.size() < 7 && letterBank.size() > 0) {//While player has less than seven tiles in its hand, and tiles exist in the letterbank, draw until hand full or bank is empty
            LetterTile temp = letterBank.removeFirst();//Assign temp location of tile at top of bank
            playerHand.add(temp);//Add tile to hand.
        }
        LinearLayout letterbar = findViewById(R.id.LetterBar);//Get player hand view
        letterbar.removeAllViews();//Remove all prior views to prevent overflow
        for (int i = 0; i < playerHand.size(); i++) {//Iterate through list to add tiles in hand to view
            if (playerHand.get(i).getParent() == null) {//Ensure that tile has no parents
                letterbar.addView(playerHand.get(i));//If no parents, simply add current tile to player hand view.
            } else {//Debugging to track any parents of tiles in hand.
                Log.d("Test", "View had parent, tile:" + String.valueOf(playerHand.get(i).getChar()));
                ViewParent parent = playerHand.get(i).getParent();
                ViewGroup owner = (ViewGroup) parent;
                owner.removeView(playerHand.get(i));
                letterbar.addView(playerHand.get(i));
            }
        }

    }

    public void compDraw() {
        while (compHand.size() < 7 && letterBank.size() > 0) {//While computer player has less than seven tiles in its hand, and tiles exist in the letterbank, draw until hand full or bank is empty
            compHand.add(letterBank.removeFirst());//Add tile to hand.
        }
    }

    public void createStartGrid() {
        LinearLayout row1 = findViewById(R.id.Row1);
        LinearLayout row2 = findViewById(R.id.Row2);
        LinearLayout row3 = findViewById(R.id.Row3);
        LinearLayout row4 = findViewById(R.id.Row4);
        LinearLayout row5 = findViewById(R.id.Row5);
        LinearLayout row6 = findViewById(R.id.Row6);
        LinearLayout row7 = findViewById(R.id.Row7);
        LinearLayout row8 = findViewById(R.id.Row8);
        LinearLayout row9 = findViewById(R.id.Row9);
        LinearLayout row10 = findViewById(R.id.Row10);
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(0, i);
            row1.addView(test);
            grid[0][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(1, i);
            row2.addView(test);
            grid[1][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(2, i);
            row3.addView(test);
            grid[2][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(3, i);
            row4.addView(test);
            grid[3][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(4, i);
            row5.addView(test);
            grid[4][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(5, i);
            row6.addView(test);
            grid[5][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(6, i);
            row7.addView(test);
            grid[6][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(7, i);
            row8.addView(test);
            grid[7][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(8, i);
            row9.addView(test);
            grid[8][i] = test;
        }
        for (int i = 0; i < 10; i++) {
            BoardTile test = new BoardTile(this, ' ');
            test.settIndex(9, i);
            row10.addView(test);
            grid[9][i] = test;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {//If returning from word creation activity
            if (resultCode == RESULT_OK) {//If activity completed successfully
                String word = data.getStringExtra(Intent.EXTRA_TEXT);//Get created word from intent sent back from activity
                String handT = data.getStringExtra(Intent.EXTRA_PROCESS_TEXT);//Get current player hand character contents from intent.
                int leftLength = data.getIntExtra(WordCreation.EXTRA_LEFT, 0);
                int rightLength = data.getIntExtra(WordCreation.EXTRA_RIGHT, 0);
                tileIndex rootIndex = data.getParcelableExtra(WordCreation.EXTRA_INDEX);
                int index = data.getIntExtra(WordCreation.EXTRA_ROOT, 0);
                playerHand.clear();//Clear player hand list contents
                LinearLayout letterbar = findViewById(R.id.LetterBar);//Get view that displays the player hand contents.
                letterbar.removeAllViews();//Clear current contents of hand view
                for (int i = 0; i < handT.length(); i++) {
                    LetterTile temp = new LetterTile(this, handT.charAt(i));//Create tile for current character in hand array
                    playerHand.add(temp);//Add tile to player hand list
                    letterbar.addView(temp);//Add tile view to player hand display.
                }
                if (playerHand.size() < 7) {//Check is hand has less than 7 tiles.
                    playerDraw();//If hand has less than 7 tiles, call draw function
                }
                grid = g.getGrid();//Update local grid information
                int rowNum = rootIndex.getxIndex();
                int colNum = rootIndex.getyIndex();

                boolean horizontal = grid[rowNum][colNum].isHorizontal();//Check root tile to determine if vertical or horizontal placement is called for
                Log.d("FatalIntentTest", String.valueOf(rowNum)+" "+String.valueOf(colNum)+" "+String.valueOf(horizontal));
                int currentIndex = 0;
                if (horizontal) {
                    if (index > 0) {
                        //int leftStart=index-1;
                        for (int i = 1; i <= leftLength; i++) {
                            //Go through locations above root index up to projected index of tile that would be
                            //directly above letter farthest "left" from root
                            //Log.d("TestTracking", String.valueOf(rowNum - i));
                            if ((rowNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                                currentIndex = (rowNum - i);//Get row index of collision
                                BoardTile temp = grid[currentIndex][colNum];//Get tile data from grid
                                Log.d("FatalTestA",String.valueOf(grid[currentIndex][colNum].getChar()));
                                if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                    Log.d("LifeTestLM", "Alive");
                                    temp.setClickable(true);
                                    temp.setOnClickListener(new ClickListener());
                                }
                                //grid[currentIndex][colNum]=temp;'
                                switch (currentIndex) {
                                    case 0:
                                        row1.addView(temp, colNum);
                                        row1.removeViewAt(colNum + 1);
                                        break;
                                    case 1:
                                        row2.addView(temp, colNum);
                                        row2.removeViewAt(colNum + 1);
                                        break;
                                    case 2:
                                        row3.addView(temp, colNum);
                                        row3.removeViewAt(colNum + 1);
                                        break;
                                    case 3:
                                        row4.addView(temp, colNum);
                                        row4.removeViewAt(colNum + 1);
                                        break;
                                    case 4:
                                        row5.addView(temp, colNum);
                                        row5.removeViewAt(colNum + 1);
                                        break;
                                    case 5:
                                        row6.addView(temp, colNum);
                                        row6.removeViewAt(colNum + 1);
                                        break;
                                    case 6:
                                        row7.addView(temp, colNum);
                                        row7.removeViewAt(colNum + 1);
                                        break;
                                    case 7:
                                        row8.addView(temp, colNum);
                                        row8.removeViewAt(colNum + 1);
                                        break;
                                    case 8:
                                        row9.addView(temp, colNum);
                                        row9.removeViewAt(colNum + 1);
                                        break;
                                    case 9:
                                        row10.addView(temp, colNum);
                                        row10.removeViewAt(colNum + 1);
                                        break;
                                    default:
                                        break;
                                }
                            }else if(rowNum-i==0){
                                currentIndex = (rowNum - i);//Get row index of collision
                                BoardTile temp = grid[currentIndex][colNum];//Get tile data from grid
                                Log.d("FatalTestAB",String.valueOf(grid[currentIndex][colNum].getChar())+","+String.valueOf(colNum));
                                if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                    Log.d("LifeTestLM", "Alive");
                                    temp.setClickable(true);
                                    temp.setOnClickListener(new ClickListener());
                                }
                                row1.addView(temp, colNum);
                                row1.removeViewAt(colNum + 1);
                            }
                        }
                    }
                    if (index < word.length()) {
                        for (int i = 1; i <= rightLength; i++) {
                            //Go through locations below root index up to projected index of tile that would be
                            //directly below letter farthest "right" from root
                            //Log.d("TestTracking", String.valueOf(rowNum + i));
                            if ((rowNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                                currentIndex = (rowNum + i);//Get row index of collision
                                BoardTile temp = grid[currentIndex][colNum];
                                Log.d("FatalTestB",String.valueOf(grid[currentIndex][colNum].getChar()));
                                if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                    Log.d("LifeTestRM", "Alive");
                                    temp.setClickable(true);
                                    temp.setOnClickListener(new ClickListener());
                                }else{
                                    temp.setClickable(false);
                                }
                                switch (currentIndex) {
                                    case 0:
                                        row1.addView(temp, colNum);
                                        row1.removeViewAt(colNum + 1);
                                        break;
                                    case 1:
                                        row2.addView(temp, colNum);
                                        row2.removeViewAt(colNum + 1);
                                        break;
                                    case 2:
                                        row3.addView(temp, colNum);
                                        row3.removeViewAt(colNum + 1);
                                        break;
                                    case 3:
                                        row4.addView(temp, colNum);
                                        row4.removeViewAt(colNum + 1);
                                        break;
                                    case 4:
                                        row5.addView(temp, colNum);
                                        row5.removeViewAt(colNum + 1);
                                        break;
                                    case 5:
                                        row6.addView(temp, colNum);
                                        row6.removeViewAt(colNum + 1);
                                        break;
                                    case 6:
                                        row7.addView(temp, colNum);
                                        row7.removeViewAt(colNum + 1);
                                        break;
                                    case 7:
                                        row8.addView(temp, colNum);
                                        row8.removeViewAt(colNum + 1);
                                        break;
                                    case 8:
                                        row9.addView(temp, colNum);
                                        row9.removeViewAt(colNum + 1);
                                        break;
                                    case 9:

                                    default:
                                        break;
                                }
                            }else if(rowNum+i==9){
                                currentIndex = (rowNum + i);//Get row index of collision
                                BoardTile temp = grid[currentIndex][colNum];
                                Log.d("FatalTestBB",String.valueOf(grid[currentIndex][colNum].getChar())+","+String.valueOf(colNum));
                                if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                    Log.d("LifeTestRM", "Alive");
                                    temp.setClickable(true);
                                    temp.setOnClickListener(new ClickListener());
                                }else{
                                    temp.setClickable(false);
                                }
                                row10.addView(temp, colNum);
                                row10.removeViewAt(colNum + 1);
                                break;
                            }
                        }

                    }
                } else {
                    grid[rowNum][colNum].setHorizontalF();//Set flag for root tile to say that it is a part of a horizontal word
                    for (int i = 1; i <= leftLength; i++) {
                        //Go through locations above root index up to projected index of tile that would be
                        //directly above letter farthest "left" from root
                        //Log.d("TestTracking", String.valueOf(colNum - i));
                        if ((colNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                            currentIndex = (colNum - i);//Get row index of collision
                            BoardTile temp = grid[rowNum][currentIndex];
                            if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                Log.d("LifeTestLM", "Alive");
                                temp.setClickable(true);
                                temp.setOnClickListener(new ClickListener());
                            }
                            //grid[currentIndex][colNum]=temp;
                            Log.d("FatalTestC", String.valueOf(temp.getChar()));
                            switch (rowNum) {
                                case 0:
                                    row1.addView(temp, currentIndex);
                                    row1.removeViewAt(currentIndex +  1);
                                    break;
                                case 1:
                                    row2.addView(temp, currentIndex);
                                    row2.removeViewAt(currentIndex + 1);
                                    break;
                                case 2:
                                    row3.addView(temp, currentIndex);
                                    row3.removeViewAt(currentIndex + 1);
                                    break;
                                case 3:
                                    row4.addView(temp, currentIndex);
                                    row4.removeViewAt(currentIndex + 1);
                                    break;
                                case 4:
                                    row5.addView(temp, currentIndex);
                                    row5.removeViewAt(currentIndex + 1);
                                    break;
                                case 5:
                                    row6.addView(temp, currentIndex);
                                    row6.removeViewAt(currentIndex + 1);
                                    break;
                                case 6:
                                    row7.addView(temp, currentIndex);
                                    row7.removeViewAt(currentIndex + 1);
                                    break;
                                case 7:
                                    row8.addView(temp, currentIndex);
                                    row8.removeViewAt(currentIndex + 1);
                                    break;
                                case 8:
                                    row9.addView(temp, currentIndex);
                                    row9.removeViewAt(currentIndex + 1);
                                    break;
                                case 9:
                                    row10.addView(temp, currentIndex);
                                    row10.removeViewAt(currentIndex + 1);
                                    break;
                                default:
                                    break;
                            }
                        }else if(colNum-i==0){
                            currentIndex = (colNum - i);//Get row index of collision
                            BoardTile temp = grid[rowNum][currentIndex];
                            if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                Log.d("LifeTestLM", "Alive");
                                temp.setClickable(true);
                                temp.setOnClickListener(new ClickListener());
                            }
                            switch (rowNum) {
                                case 0:
                                    row1.addView(temp, currentIndex);
                                    row1.removeViewAt(currentIndex +  1);
                                    break;
                                case 1:
                                    row2.addView(temp, currentIndex);
                                    row2.removeViewAt(currentIndex + 1);
                                    break;
                                case 2:
                                    row3.addView(temp, currentIndex);
                                    row3.removeViewAt(currentIndex + 1);
                                    break;
                                case 3:
                                    row4.addView(temp, currentIndex);
                                    row4.removeViewAt(currentIndex + 1);
                                    break;
                                case 4:
                                    row5.addView(temp, currentIndex);
                                    row5.removeViewAt(currentIndex + 1);
                                    break;
                                case 5:
                                    row6.addView(temp, currentIndex);
                                    row6.removeViewAt(currentIndex + 1);
                                    break;
                                case 6:
                                    row7.addView(temp, currentIndex);
                                    row7.removeViewAt(currentIndex + 1);
                                    break;
                                case 7:
                                    row8.addView(temp, currentIndex);
                                    row8.removeViewAt(currentIndex + 1);
                                    break;
                                case 8:
                                    row9.addView(temp, currentIndex);
                                    row9.removeViewAt(currentIndex + 1);
                                    break;
                                case 9:
                                    row10.addView(temp, currentIndex);
                                    row10.removeViewAt(currentIndex + 1);
                                    break;
                                default:
                                    break;
                            }
                            //grid[currentIndex][colNum]=temp;
                            Log.d("FatalTestCB", String.valueOf(temp.getChar())+","+String.valueOf(rowNum));

                        }
                    }
                    for (int i = 1; i <= rightLength; i++) {
                        //Go through locations below root index up to projected index of tile that would be
                        //directly below letter farthest "right" from root
                        if ((colNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                            currentIndex = (colNum + i);//Get row index of collision
                            BoardTile temp = grid[rowNum][currentIndex];
                            if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                Log.d("LifeTestRM", "Alive");
                                temp.setClickable(true);
                                temp.setOnClickListener(new ClickListener());
                            }
                            Log.d("FatalTestD", String.valueOf(temp.getChar()));
                            switch (rowNum) {
                                case 0:
                                    row1.addView(temp, currentIndex);
                                    row1.removeViewAt(currentIndex + 1);
                                    break;
                                case 1:
                                    row2.addView(temp, currentIndex);
                                    row2.removeViewAt(currentIndex + 1);
                                    break;
                                case 2:
                                    row3.addView(temp, currentIndex);
                                    row3.removeViewAt(currentIndex + 1);
                                    break;
                                case 3:
                                    row4.addView(temp, currentIndex);
                                    row4.removeViewAt(currentIndex + 1);
                                    break;
                                case 4:
                                    row5.addView(temp, currentIndex);
                                    row5.removeViewAt(currentIndex + 1);
                                    break;
                                case 5:
                                    row6.addView(temp, currentIndex);
                                    row6.removeViewAt(currentIndex + 1);
                                    break;
                                case 6:
                                    row7.addView(temp, currentIndex);
                                    row7.removeViewAt(currentIndex + 1);
                                    break;
                                case 7:
                                    row8.addView(temp, currentIndex);
                                    row8.removeViewAt(currentIndex + 1);
                                    break;
                                case 8:
                                    row9.addView(temp, currentIndex);
                                    row9.removeViewAt(currentIndex + 1);
                                    break;
                                case 9:
                                    row10.addView(temp, currentIndex);
                                    row10.removeViewAt(currentIndex + 1);
                                    break;
                                default:
                                    break;
                            }
                        }else if(colNum+i==9){
                            currentIndex = (colNum + i);//Get row index of collision
                            BoardTile temp = grid[rowNum][currentIndex];
                            if (temp.isLiving()) {//Set tile to clickable if it is still alive
                                Log.d("LifeTestRM", "Alive");
                                temp.setClickable(true);
                                temp.setOnClickListener(new ClickListener());
                            }
                            Log.d("FatalTestDB", String.valueOf(temp.getChar())+","+String.valueOf(rowNum));
                            switch (rowNum) {
                                case 0:
                                    row1.addView(temp, currentIndex);
                                    row1.removeViewAt(currentIndex +  1);
                                    break;
                                case 1:
                                    row2.addView(temp, currentIndex);
                                    row2.removeViewAt(currentIndex + 1);
                                    break;
                                case 2:
                                    row3.addView(temp, currentIndex);
                                    row3.removeViewAt(currentIndex + 1);
                                    break;
                                case 3:
                                    row4.addView(temp, currentIndex);
                                    row4.removeViewAt(currentIndex + 1);
                                    break;
                                case 4:
                                    row5.addView(temp, currentIndex);
                                    row5.removeViewAt(currentIndex + 1);
                                    break;
                                case 5:
                                    row6.addView(temp, currentIndex);
                                    row6.removeViewAt(currentIndex + 1);
                                    break;
                                case 6:
                                    row7.addView(temp, currentIndex);
                                    row7.removeViewAt(currentIndex + 1);
                                    break;
                                case 7:
                                    row8.addView(temp, currentIndex);
                                    row8.removeViewAt(currentIndex + 1);
                                    break;
                                case 8:
                                    row9.addView(temp, currentIndex);
                                    row9.removeViewAt(currentIndex + 1);
                                    break;
                                case 9:
                                    row10.addView(temp, currentIndex);
                                    row10.removeViewAt(currentIndex + 1);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                switch (rowNum) {//Render root tile unclickable on board
                    case 0:
                        row1.getChildAt(colNum).setClickable(false);
                        break;
                    case 1:
                        row2.getChildAt(colNum).setClickable(false);
                        break;
                    case 2:
                        row3.getChildAt(colNum).setClickable(false);
                        break;
                    case 3:
                        row4.getChildAt(colNum).setClickable(false);
                        break;
                    case 4:
                        row5.getChildAt(colNum).setClickable(false);
                        break;
                    case 5:
                        row6.getChildAt(colNum).setClickable(false);
                        break;
                    case 6:
                        row7.getChildAt(colNum).setClickable(false);
                        break;
                    case 7:
                        row8.getChildAt(colNum).setClickable(false);
                        break;
                    case 8:
                        row9.getChildAt(colNum).setClickable(false);
                        break;
                    case 9:
                        row10.getChildAt(colNum).setClickable(false);
                        break;
                    default:
                        break;
                }

                ArrayList<tileIndex> tempDead=g.getDeadTiles();
                for(int i=0;i<tempDead.size();i++){
                    tileIndex temp=tempDead.get(i);
                    int rowNumD=temp.getxIndex();
                    int colNumD=temp.getyIndex();
                    switch (rowNumD) {//Render root tile unclickable on board
                        case 0:
                            row1.getChildAt(colNumD).setClickable(false);
                            break;
                        case 1:
                            row2.getChildAt(colNumD).setClickable(false);
                            break;
                        case 2:
                            row3.getChildAt(colNumD).setClickable(false);
                            break;
                        case 3:
                            row4.getChildAt(colNumD).setClickable(false);
                            break;
                        case 4:
                            row5.getChildAt(colNumD).setClickable(false);
                            break;
                        case 5:
                            row6.getChildAt(colNumD).setClickable(false);
                            break;
                        case 6:
                            row7.getChildAt(colNumD).setClickable(false);
                            break;
                        case 7:
                            row8.getChildAt(colNumD).setClickable(false);
                            break;
                        case 8:
                            row9.getChildAt(colNumD).setClickable(false);
                            break;
                        case 9:
                            row10.getChildAt(colNumD).setClickable(false);
                            break;
                        default:
                            break;
                    }
                }
                //Update words made counter
                pWordsMade++;
                g.setpOutCount(pWordsMade);
                //Update score display with increased score
                TextView pScoreDisplay = findViewById(R.id.PScoreDisplay);//Get location to display player score
                pScoreDisplay.setText("Player Score:" + g.getPScore());//Refresh player score display
            }
        } else if (requestCode == PICK_DISCARD_REQUEST) {//If returning from discard activity
            if (resultCode == RESULT_OK) {//If activity completed successfully
                char[] tempHand = data.getCharArrayExtra(Intent.EXTRA_TEXT);//Get array of tiles discarded from hand
                for (int i = 0; i < tempHand.length; i++) {
                    LetterTile temp = new LetterTile(this, tempHand[i]);//Create tile for current character in array
                    letterBank.add(temp);//Add tile to letter bank
                }
                Collections.shuffle(letterBank);//Reshuffle letter bank to assure random draws
                String handT = data.getStringExtra(Intent.EXTRA_PROCESS_TEXT);//Get current player hand character contents from intent.
                playerHand.clear();//Clear player hand list contents
                LinearLayout letterbar = findViewById(R.id.LetterBar);//Get view that displays the player hand contents.
                letterbar.removeAllViews();//Clear current contents of hand view
                for (int i = 0; i < handT.length(); i++) {
                    LetterTile temp = new LetterTile(this, handT.charAt(i));//Create tile for current character in hand array
                    playerHand.add(temp);//Add tile to player hand list
                    letterbar.addView(temp);//Add tile view to player hand display.
                }
                if (playerHand.size() < 7) {//Check is hand has less than 7 tiles.
                    playerDraw();//If hand has less than 7 tiles, call draw function
                }
            }
        }
        victoryCheck();//Check if victory conditions are met.
        Button finish = (Button)findViewById(R.id.Finish);
        if(pWordsMade>6&&!finish.isClickable()){
            finish.setClickable(true);
            finish.setVisibility(View.VISIBLE);
        }
    }

    public void onCreate(View v) {//Used when word creation activity is called from clicking a board tile
        if (v.getClass().equals(BoardTile.class)) {//Check if clicked view is a board tile or not.
            BoardTile tempTile = (BoardTile) v;//Create temporary storage for the clicked root tile
            if (((BoardTile) v).isLiving()) {//Check is clicked tile is alive or not
                Intent intent = new Intent(this, WordCreation.class);//Create intent to transfer data to new activity
                char[] tempHand = new char[playerHand.size()];//Char array to store the letters of tiles in hand
                String[] handVTemp = new String[playerHand.size()];
                for (int i = 0; i < playerHand.size(); i++) {
                    tempHand[i] = playerHand.get(i).getChar();//Add current character of tile to array
                    handVTemp[i] = String.valueOf(playerHand.get(i).getChar());
                }
                char tempLetter = tempTile.getChar();//Get root tile's character
                tileIndex tempIndex = tempTile.getBoardIndex();//Get root tile's location on board
                /*HashSet<String> permutations = (HashSet<String>) permute(tempHand.toString()+String.valueOf(tempLetter));
                Log.d("Fatal",String.valueOf(permutations.size()));
                ArrayList<String> wordList=permuteWords(permutations,tempLetter);
                if(wordList.size()>0) {*/
                    g.setpOutCount(0);
                    intent.putExtra(EXTRA_BUNDLE, tempHand);//Add array to intent.
                    intent.putExtra(EXTRA_TESTTILE, tempLetter);//Store root tile character in unique location for activity
                    intent.putExtra(EXTRA_INDEX, (Parcelable) tempIndex);//Store root tile's location on board for activity
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);//Start activity
                /*}else{
                    int out=g.getpOutCount()+1;
                    g.setpOutCount(out);
                    Toast.makeText(getApplicationContext(), "No words can be made from that tile with current hand contents", Toast.LENGTH_SHORT).show();
                }*/
            } else {//If tile is dead, inform user
            Toast.makeText(getApplicationContext(), "No moves can be made from that tile", Toast.LENGTH_SHORT).show();
            }
        } else {//If view is not a board tile, display error message
            Toast.makeText(getApplicationContext(), "Not a board tile", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDiscard(View view) {
        //Makes call to activity for selecting tiles from hand to discard to bank, and draw new ones from shuffled bank
        Intent intent = new Intent(this, DiscardActivity.class);//Create intent to transfer data to new activity
        char[] tempHand = new char[playerHand.size()];//Char array to store the letters of tiles in hand
        for (int i = 0; i < playerHand.size(); i++) {
            tempHand[i] = playerHand.get(i).getChar();//Add current character of tile to array
        }
        int t=g.getpOutCount();
        g.setpOutCount(t++);
        intent.putExtra(EXTRA_BUNDLE, tempHand);//Add array to intent.
        startActivityForResult(intent, PICK_DISCARD_REQUEST);//Start activity
    }

    public void onFinish(View v){
        Intent intent=new Intent(this,EndGame.class);
        startActivity(intent);
    }

    private class ClickListener implements View.OnClickListener {
        public void onClick(View v) {
            if (v.getClass().equals(BoardTile.class)) {
                onCreate(v);
            }
        }
    }

    public void victoryCheck() {
        boolean gameOver = false;//Flag to signal whether game can be continued
        int gridPop = 0;//Value to store number of letters on board
        int deathToll = 0;//Number of dead tiles
        //Check to determine if there are still spaces for possible moves
        for (int rowNum = 0; rowNum < 9; rowNum++) {
            for (int colNum = 0; colNum < 9; colNum++) {//Iterate through grid for signs of life
                if (grid[rowNum][colNum].getChar() != ' ') {//Check if space contains a letter
                    gridPop++;//Increment size tracker
                    if (!grid[rowNum][colNum].isLiving()) {//Check if tile is alive
                        Log.d("FatalObitutarty",String.valueOf(grid[rowNum][colNum].getChar()));
                        deathToll++;//If tile is dead, increment deathToll
                    }
                }
            }
        }
        Log.d("FatalDeathToll",String.valueOf(gridPop)+". Deathtoll:"+String.valueOf(deathToll));
        if (deathToll != 0 && gridPop == deathToll) {//Compare grid size to a non-zero death toll
            gameOver = true;//If grid size is the same as death toll, all the tiles are dead, and game is over
        }
        if (letterBank.size() == 0) {//Check if letterbank is empty
            if (playerHand.size() <= 1 || compHand.size() <= 1) {//Check hands to determine if both players still have tiles
                gameOver = true;//If either player hand size is one or less, then the game is over
            }
        }
        /*if (g.getpOutCount() == 3 | g.getcOutCount() == 3) {//If either player is forced to discard three times in a row, then game ends.
            gameOver = true;
        }*/
        if(gameOver){
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            goToEnd();
                        }
                    },10000
            );
        }
    }
    private void setStartWord(){
        LinkedList<LetterTile> tempHand=new LinkedList<>();
        while (tempHand.size() < 7 ) {//While computer player has less than seven tiles in its hand, and tiles exist in the letterbank, draw until hand full or bank is empty
            tempHand.add(letterBank.removeFirst());//Add tile to hand.
        }
        String x="";
        for(int i=0;i<tempHand.size();i++){
            x+=tempHand.get(i).getChar();
        }

        HashSet<String> permutations = (HashSet<String>) permute(x);
        ArrayList<String> wordList=permuteWords(permutations);
        if (wordList.size() <= 0) {
            do {
                permutations.clear();
                wordList.clear();
                while(!tempHand.isEmpty()){
                    LetterTile tempT=tempHand.removeFirst();
                    letterBank.add(tempT);
                }
                Collections.shuffle(letterBank);
                while (tempHand.size() < 7 ) {//While computer player has less than seven tiles in its hand, and tiles exist in the letterbank, draw until hand full or bank is empty
                    tempHand.add(letterBank.removeFirst());//Add tile to hand.
                }
                x="";
                for(int i=0;i<tempHand.size();i++){
                    x+=tempHand.get(i).getChar();
                }

                permutations=(HashSet<String>) permute(x);
                wordList=permuteWords(permutations);
            } while (wordList.size() <= 0);
        }
        int rootW=random.nextInt(wordList.size());
        String tword=wordList.get(rootW);
        int mid=tword.length() / 2;
        for(int i=0;i<tword.length();i++){
            int index=x.indexOf(tword.charAt(i));
            String pre = x.substring(0, index);
            String post = x.substring(index+1);
            x = pre + post;
            LetterTile test=tempHand.remove(index);
        }
        while(!tempHand.isEmpty()){
            LetterTile tempT=tempHand.removeFirst();
            letterBank.add(tempT);
        }
        Collections.shuffle(letterBank);
        BoardTile midTile = new BoardTile(this, tword.charAt(mid));
        midTile.setClickable(true);
        midTile.setOnClickListener(new ClickListener());
        midTile.settIndex(4,4);
        int currentIndex=0;
        //int setUpV=random.nextInt(2)+1;
        int setUpV=1;
        ArrayList<tileIndex> living=g.getLivingTiles();
        living.add(midTile.getBoardIndex());
        if(setUpV==1){
            midTile.setHorizontalF();
            grid[4][4]=midTile;
            row5.addView(midTile, 4);
            row5.removeViewAt(5);
            for (int i = 1; i < mid+1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((4 - i > 0)&&(mid-i)>-1) { //Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 - i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid-i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.setHorizontalF();
                    temp.settIndex(4,currentIndex);
                    living.add(temp.getBoardIndex());
                    grid[4][currentIndex]=temp;
                    row5.addView(temp,currentIndex);
                    temp= (BoardTile) row5.getChildAt(currentIndex+1);
                    row5.removeViewAt(currentIndex+1);
                }
            }for (int i =1; i < mid+1; i++) {
                //Go through locations below root index up to projected index of tile that would be
                //directly below letter farthest "right" from root
                //Log.d("TestTracking", String.valueOf(rowNum + i));
                if ((4 + i) < 10&&(mid+i)<tword.length()) {//Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 + i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid+i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.setHorizontalF();
                    temp.settIndex(4,currentIndex);
                    living.add(temp.getBoardIndex());
                    grid[4][currentIndex]=temp;
                    row5.addView(temp,currentIndex);
                    temp= (BoardTile) row5.getChildAt(currentIndex+1);
                    row5.removeViewAt(currentIndex+1);
                }
            }
        }else if(setUpV==2){
            grid[4][4]=midTile;
            row5.addView(midTile, 4);
            row5.removeViewAt(5);
            for (int i = 1; i < mid+1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((4 - i > 0)&&(mid-i)>-1) { //Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 - i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid-i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.settIndex(currentIndex,4);
                    living.add(temp.getBoardIndex());
                    grid[currentIndex][4]=temp;
                    switch (currentIndex) {
                        case 0:
                            row1.addView(temp, 4);
                            row1.removeViewAt(5);
                            break;
                        case 1:
                            row2.addView(temp, 4);
                            row2.removeViewAt(5);
                            break;
                        case 2:
                            row3.addView(temp, 4);
                            row3.removeViewAt(5);
                            break;
                        case 3:
                            row4.addView(temp, 4);
                            row4.removeViewAt(5);
                            break;
                        case 4:
                            row5.addView(temp, 4);
                            row5.removeViewAt(5);
                            break;
                        case 5:
                            row6.addView(temp, 4);
                            row6.removeViewAt(5);
                            break;
                        case 6:
                            row7.addView(temp, 4);
                            row7.removeViewAt(5);
                            break;
                        case 7:
                            row8.addView(temp, 4);
                            row8.removeViewAt(5);
                            break;
                        case 8:
                            row9.addView(temp, 4);
                            row9.removeViewAt(5);
                            break;
                        case 9:
                            row10.addView(temp, 4);
                            row10.removeViewAt(5);
                            break;
                        default:
                            break;
                    }
                }
            }for (int i =1; i < mid+1; i++) {
                //Go through locations below root index up to projected index of tile that would be
                //directly below letter farthest "right" from root
                if ((4 + i) < 10&&(mid+i)<tword.length()) {//Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 + i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid+i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.settIndex(currentIndex,4);
                    living.add(temp.getBoardIndex());
                    grid[currentIndex][4]=temp;
                    switch (currentIndex) {
                        case 0:
                            row1.addView(temp, 4);
                            row1.removeViewAt(5);
                            break;
                        case 1:
                            row2.addView(temp, 4);
                            row2.removeViewAt(5);
                            break;
                        case 2:
                            row3.addView(temp, 4);
                            row3.removeViewAt(5);
                            break;
                        case 3:
                            row4.addView(temp, 4);
                            row4.removeViewAt(5);
                            break;
                        case 4:
                            row5.addView(temp, 4);
                            row5.removeViewAt(5);
                            break;
                        case 5:
                            row6.addView(temp, 4);
                            row6.removeViewAt(5);
                            break;
                        case 6:
                            row7.addView(temp, 4);
                            row7.removeViewAt(5);
                            break;
                        case 7:
                            row8.addView(temp, 4);
                            row8.removeViewAt(5);
                            break;
                        case 8:
                            row9.addView(temp, 4);
                            row9.removeViewAt(5);
                            break;
                        case 9:
                            row10.addView(temp, 4);
                            row10.removeViewAt(5);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        g.setLivingTiles(living);
    }
    private void testVictoryWord(){
        String tword="Rye";
        int mid=tword.length() / 2;
        BoardTile midTile = new BoardTile(this, tword.charAt(mid));
        midTile.setClickable(true);
        midTile.setOnClickListener(new ClickListener());
        midTile.settIndex(4,4);
        int currentIndex=0;
        int setUpV=random.nextInt(2)+1;
        ArrayList<tileIndex> living=g.getLivingTiles();
        living.add(midTile.getBoardIndex());
        Log.d("FatalCollision",String.valueOf(setUpV));
        if(setUpV==1){
            midTile.setHorizontalF();
            grid[4][4]=midTile;
            row5.addView(midTile, 4);
            row5.removeViewAt(5);
            for (int i = 1; i < mid+1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((4 - i > 0)&&(mid-i)>-1) { //Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 - i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid-i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.setHorizontalF();
                    temp.settIndex(4,currentIndex);
                    living.add(temp.getBoardIndex());
                    grid[4][currentIndex]=temp;
                    row5.addView(temp,currentIndex);
                    temp= (BoardTile) row5.getChildAt(currentIndex+1);
                    row5.removeViewAt(currentIndex+1);
                }
            }for (int i =1; i < mid+1; i++) {
                //Go through locations below root index up to projected index of tile that would be
                //directly below letter farthest "right" from root
                //Log.d("TestTracking", String.valueOf(rowNum + i));
                if ((4 + i) < 10&&(mid+i)<tword.length()) {//Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 + i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid+i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.setHorizontalF();
                    temp.settIndex(4,currentIndex);
                    living.add(temp.getBoardIndex());
                    grid[4][currentIndex]=temp;
                    row5.addView(temp,currentIndex);
                    temp= (BoardTile) row5.getChildAt(currentIndex+1);
                    row5.removeViewAt(currentIndex+1);
                }
            }
        }else if(setUpV==2){
            grid[4][4]=midTile;
            row5.addView(midTile, 4);
            row5.removeViewAt(5);
            for (int i = 1; i < mid+1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((4 - i > 0)&&(mid-i)>-1) { //Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 - i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid-i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.settIndex(currentIndex,4);
                    living.add(temp.getBoardIndex());
                    grid[currentIndex][4]=temp;
                    switch (currentIndex) {
                        case 0:
                            row1.addView(temp, 4);
                            row1.removeViewAt(5);
                            break;
                        case 1:
                            row2.addView(temp, 4);
                            row2.removeViewAt(5);
                            break;
                        case 2:
                            row3.addView(temp, 4);
                            row3.removeViewAt(5);
                            break;
                        case 3:
                            row4.addView(temp, 4);
                            row4.removeViewAt(5);
                            break;
                        case 4:
                            row5.addView(temp, 4);
                            row5.removeViewAt(5);
                            break;
                        case 5:
                            row6.addView(temp, 4);
                            row6.removeViewAt(5);
                            break;
                        case 6:
                            row7.addView(temp, 4);
                            row7.removeViewAt(5);
                            break;
                        case 7:
                            row8.addView(temp, 4);
                            row8.removeViewAt(5);
                            break;
                        case 8:
                            row9.addView(temp, 4);
                            row9.removeViewAt(5);
                            break;
                        case 9:
                            row10.addView(temp, 4);
                            row10.removeViewAt(5);
                            break;
                        default:
                            break;
                    }
                }
            }for (int i =1; i < mid+1; i++) {
                //Go through locations below root index up to projected index of tile that would be
                //directly below letter farthest "right" from root
                if ((4 + i) < 10&&(mid+i)<tword.length()) {//Check to prevent out of bounds errors resulting from search
                    currentIndex = (4 + i);//Get row index of collision
                    BoardTile temp = new BoardTile(this,tword.charAt(mid+i));
                    temp.setClickable(true);
                    temp.setOnClickListener(new ClickListener());
                    temp.settIndex(currentIndex,4);
                    living.add(temp.getBoardIndex());
                    grid[currentIndex][4]=temp;
                    switch (currentIndex) {
                        case 0:
                            row1.addView(temp, 4);
                            row1.removeViewAt(5);
                            break;
                        case 1:
                            row2.addView(temp, 4);
                            row2.removeViewAt(5);
                            break;
                        case 2:
                            row3.addView(temp, 4);
                            row3.removeViewAt(5);
                            break;
                        case 3:
                            row4.addView(temp, 4);
                            row4.removeViewAt(5);
                            break;
                        case 4:
                            row5.addView(temp, 4);
                            row5.removeViewAt(5);
                            break;
                        case 5:
                            row6.addView(temp, 4);
                            row6.removeViewAt(5);
                            break;
                        case 6:
                            row7.addView(temp, 4);
                            row7.removeViewAt(5);
                            break;
                        case 7:
                            row8.addView(temp, 4);
                            row8.removeViewAt(5);
                            break;
                        case 8:
                            row9.addView(temp, 4);
                            row9.removeViewAt(5);
                            break;
                        case 9:
                            row10.addView(temp, 4);
                            row10.removeViewAt(5);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        g.setLivingTiles(living);
    }
    public Set<String> permute(String chars) {
        // Use sets to eliminate semantic duplicates (aab is still aab even if you switch the two 'a's)
        // Switch to HashSet for better performance
        Set<String> set = new HashSet<String>();

        // Termination condition: only 1 permutation for a string of length 1
        if (chars.length() == 1) {
            set.add(chars);
        } else {
            // Give each character a chance to be the first in the permuted string
            for (int i=0; i<chars.length(); i++) {
                // Remove the character at index i from the string
                String pre = chars.substring(0, i);
                String post = chars.substring(i+1);
                String remaining = pre+post;

                // Recurse to find all the permutations of the remaining chars
                for (String permutation : permute(remaining)) {
                    // Concatenate the first character with the permutations of the remaining chars
                    if(dictionary.isGoodWord(permutation)){
                        set.add(permutation);
                    }
                    String temp=chars.charAt(i) + permutation;
                    if(dictionary.isGoodWord(temp)) {
                        set.add(temp);
                    }
                }
            }
        }
        return set;
    }
    public ArrayList<String> permuteWords(HashSet<String> permutations){
        ArrayList<String> words=new ArrayList<String>();
        for(String temp : permutations){
            if(2<temp.length()) {//&&temp.length()<7
                words.add(temp);
            }
        }
        return words;
    }
    public ArrayList<String> permuteWords(HashSet<String> permutations,char root){
        ArrayList<String> words=new ArrayList<String>();

        for(String temp : permutations) {
            if (temp.contains(String.valueOf(root))) {//Confirm that word contains root character somewhere
                if (2 < temp.length()) {//&&temp.length()<7
                    words.add(temp);
                }
            }
        }
        return words;
    }
    public void compTurn(){
        victoryCheck();
    }
    public void testValid(){
        BoardTile temp = grid[4][4];
        String hand="";
        for(int i=0;i<playerHand.size();i++){
            hand+=playerHand.get(i).getChar();
        }
        HashSet<String> permutations = (HashSet<String>) permute(hand+temp.getChar());
        ArrayList<String> wordList=permuteWords(permutations,temp.getChar());
        if(HandValidation(temp.getBoardIndex(),temp.getChar(),wordList)){
            Log.d("FatalSucess","Sucessful");
        }else{
            Log.d("FatalFail","Unsucessful");
        }
    }
    public boolean HandValidation(tileIndex tempIndex,char tempLetter,ArrayList<String> wordList){
        boolean workingHand=false;
        int colNum = tempIndex.getyIndex();//Get column of root tile
        int rowNum = tempIndex.getxIndex();//Get row of root tile
        if (grid[rowNum][colNum].getChar() == tempLetter) {
            //Test to ensure that index gets the right tile on board
            boolean horizontal = grid[rowNum][colNum].isHorizontal();//Flag to declare if root is a part of a horizontal word
            //Flag to indicate if word collides with another and indicates that further actions unnecessary
            for(int i=0;i<wordList.size();i++) {
                String temp=wordList.get(i);
                Log.d("FatalWord",temp);
                int rootI=temp.indexOf(tempLetter);
                int leftLength=rootI-1;
                Log.d("FatalLeft", String.valueOf(leftLength));
                int rightLength=temp.length()-rootI;
                Log.d("FatalRight", String.valueOf(rightLength));
                boolean collision = collisionTest(rowNum, colNum, leftLength, rightLength, horizontal);
                boolean overflow = overflowTest(rowNum, colNum, leftLength, rightLength, horizontal);//Flag to indicate overflow that indicates no further action to be taken
                if(!collision&&!overflow) {
                    workingHand = true;
                    break;
                }
            }
        }
        return workingHand;
    }
    public boolean collisionTest(int rowNum,int colNum, int leftLength, int rightLength,boolean horizontal){
        if(horizontal) {//Vertical word construction
            int collisionIndex = 0;//Location of collision on board relative to root
            for (int i = 1; i <= leftLength + 1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((rowNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                    if (grid[rowNum - i][colNum].getChar() != ' ') {//If a tile already exists at current location
                        collisionIndex = (rowNum - i);//Get row index of collision
                        //Display error message to user
                        Log.d("FatalCollision","Collision at index:" + collisionIndex + "," + colNum);
                        //Toast.makeText(getApplicationContext(), "Collision at index:" + collisionIndex + "," + colNum, Toast.LENGTH_SHORT).show();
                        return true;//Return flag confirming collision to calling function
                    }
                }
            }
            for (int i = 1; i <= rightLength + 1; i++) {
                //Go through locations below root index up to projected index of tile that would be
                //directly below letter farthest "right" from root
                if ((rowNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                    if (grid[rowNum + i][colNum].getChar() != ' ') {//If a tile already exists at current location
                        collisionIndex = (rowNum + i);//Get row index of collision
                        //Display error message to user
                        Log.d("FatalCollision","Collision at index:" + collisionIndex + "," + colNum);
//                        Toast.makeText(getApplicationContext(), "Collision at index:" + collisionIndex + "," + colNum, Toast.LENGTH_SHORT).show();
                        return true;//Return flag confirming collision to calling function
                    }
                }
            }
        }else{
            int collisionIndex = 0;//Location of collision on board relative to root
            for (int i = 1; i <= leftLength + 1; i++) {
                //Go through locations left of the root index up to projected index of tile that would be
                //directly to the left of the letter farthest from root, i.e the first letter of the word
                if ((colNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                    if (grid[rowNum][colNum - i].getChar() != ' ') {//If a tile already exists at current location
                        collisionIndex = (colNum - i);//Get column index of collision
                        //Display error message to user
                        Log.d("FatalCollision","Collision at index:" + rowNum  + "," + collisionIndex);
                        //Toast.makeText(getApplicationContext(), "Collision at index:" + rowNum  + "," + collisionIndex, Toast.LENGTH_SHORT).show();
                        return true;//Return flag confirming collision to calling function
                    }
                }
            }
            for (int i = 1; i <= rightLength + 1; i++) {
                //Go through locations to the right of the root index up to projected index of tile that would be
                //directly right of the letter farthest "right" from root, i.e the last letter of the word
                if ((colNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                    if (grid[rowNum][colNum + i].getChar() != ' ') {//If a tile already exists at current location
                        collisionIndex = (colNum + i);//Get column index of collision
                        //Display error message to user
                        Log.d("FatalCollision","Collision at index:" + rowNum  + "," + collisionIndex);
                        //Toast.makeText(getApplicationContext(), "Collision at index:" + rowNum + "," + collisionIndex, Toast.LENGTH_SHORT).show();
                        return true;//Return flag confirming collision to calling function
                    }
                }
            }
        }
        return false;
    }
    public boolean overflowTest(int rowNum,int colNum, int leftLength, int rightLength,boolean horizontal){
        if(horizontal){//Vertical word construction
            if ((rowNum - leftLength) < 0) {//If word goes above boundaries
                int overflowL = Math.abs(rowNum - leftLength);//Overflow amount
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go above grid boundaries by " +
                        overflowL + " characters.");
                //Toast.makeText(getApplicationContext(), "Letters of word would go above grid boundaries by " +
                //        overflowL + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((rowNum + rightLength) > 9) {//If word goes below boundaries
                int overflowR = Math.abs(rowNum + rightLength);//Overflow amount
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go below grid boundaries by " +
                        overflowR + " characters.");
                /*Toast.makeText(getApplicationContext(), "Letters of word would go below grid boundaries by " +
                        overflowR + " characters.", Toast.LENGTH_SHORT).show();*/
                return true;
            } else if ((rowNum + rightLength) > 9 && (rowNum - leftLength) < -1) {//If word goes above and below boundaries
                int overflowL = Math.abs(rowNum - leftLength);//Overflow amount above
                int overflowR = Math.abs(rowNum + rightLength);//Overflow amount below
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go above grid boundaries by " +
                        overflowL + " characters and below by " + overflowR + " characters.");
//                Toast.makeText(getApplicationContext(), "Letters of word would go above grid boundaries by " +
//                        overflowL + " characters and below by " + overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }else{
            if ((colNum - leftLength) < 0) {//If word goes beyond the boundaries of the left side of grid
                int overflowL = Math.abs(colNum - leftLength);//Overflow amount
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go left of grid boundaries by " +
                        overflowL + " characters.");
//                Toast.makeText(getApplicationContext(), "Letters of word would go left of grid boundaries by " +
//                        overflowL + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((colNum + rightLength) > 9) {//If word goes beyond the boundaries of the right side of grid
                int overflowR = Math.abs(colNum + rightLength);//Overflow amount
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go right of grid boundaries by " +
                        overflowR + " characters.");
//                Toast.makeText(getApplicationContext(), "Letters of word would go right of grid boundaries by " +
//                        overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((colNum + rightLength) > 9 && (colNum - leftLength) < -1) {//If word goes above and below boundaries
                int overflowL = Math.abs(colNum - leftLength);//Overflow amount above
                int overflowR = Math.abs(colNum + rightLength);//Overflow amount below
                //Display error message to user
                Log.d("FatalOverFlow","Letters of word would go to the left of grid boundaries by " +
                        overflowL + " characters and to the right of grid boundaries by "
                        + overflowR + " characters.");
//                Toast.makeText(getApplicationContext(), "Letters of word would go to the left of grid boundaries by " +
//                        overflowL + " characters and to the right of grid boundaries by "
//                        + overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    public void goToEnd(){
        Intent intent=new Intent(this,EndGame.class);
        startActivity(intent);
    }
}