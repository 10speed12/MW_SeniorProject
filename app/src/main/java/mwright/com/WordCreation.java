package mwright.com;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.LinkedList;

public class WordCreation extends AppCompatActivity {
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private Dictionary dictionary;//Dictionary used to validate words
    private LinkedList<LetterTile> wordCreated=new LinkedList<>();//Stores all tiles being used to create new word
    private LinkedList<LetterTile> playerHand=new LinkedList<>();//Stores all tiles in user player's hand
    private Globals g=Globals.getInstance();//Get global variables
    static final String EXTRA_LEFT = "com.mwright.EXTRA_LEFT";
    static final String EXTRA_RIGHT = "com.mwright.EXTRA_RIGHT";
    static final String EXTRA_INDEX = "com.mwright.EXTRA_INDEX";//String to define that a board tile's location on grid is being stored in intent
    static final String EXTRA_ROOT="com.mwright.EXTRA_ROOT";
    private tileIndex rootIndex;
    private BoardTile[][] grid=g.getGrid();//Get current grid info
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_creation);//Define which activity layout to load
        AssetManager assetManager = getAssets();

        Intent intent = getIntent();//Get intent from previous activity
        TextView pScoreDisplay=findViewById(R.id.PScoreDisplay);//Get location to display player score
        pScoreDisplay.setText("Player Score:"+g.getPScore());//Display player score
        TextView compScoreD=findViewById(R.id.CScoreDisplay);//Get location to display computer score
        compScoreD.setText("Computer Score:"+g.getCScore());//Display computer score
        try {
            InputStream inputStream = assetManager.open("words.txt");//Get list of words
            dictionary = new Dictionary(new InputStreamReader(inputStream));//Create dictionary with list of words loaded from resource
        } catch (IOException e) {//Catch potential load error
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        char[] tempHand=intent.getCharArrayExtra(MainActivity.EXTRA_BUNDLE);//Get character array storing player hand contents
        LinearLayout pHand= findViewById(R.id.PlayerHand);//Get view to display hand in
        for(int i=0;i<tempHand.length;i++) {//Iterate through array
            LetterTile temp=new LetterTile(this,tempHand[i]);//Create tile using current character
            playerHand.add(temp);//Store tile in player hand list
            pHand.addView(temp);//Store tile in hand display view
        }
        LinearLayout wordConstructor= findViewById(R.id.WordCreator);//Get view that word is being made in
        char rootLetter=intent.getCharExtra(MainActivity.EXTRA_TESTTILE,'a');//Get character stored in root tile
        LetterTile rootTile=new LetterTile(this,rootLetter);//Create tile using character
        rootTile.freeze();//Freeze root tile to prevent it being added to hand
        rootTile.setTag("root");//Set tag to find tile later
        wordConstructor.addView(rootTile);//Add root tile to creator view
        rootIndex=(tileIndex) intent.getParcelableExtra(MainActivity.EXTRA_INDEX);//Get root tile's location on board
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
                    LetterTile tile = (LetterTile) event.getLocalState();

                    ViewParent parent = tile.getParent();
                    LinearLayout pHand= findViewById(R.id.PlayerHand);
                    LinearLayout wordConstructor= findViewById(R.id.WordCreator);
                    if(parent.equals(pHand)&&v.equals(wordConstructor)){
                        for(int i=0;i<playerHand.size();i++){
                            if(playerHand.get(i).getChar()==tile.getChar()){
                                wordCreated.add(tile);
                                playerHand.remove(i);
                                break;
                            }
                        }
                    }else if(parent.equals(wordConstructor)&& v.equals(pHand)){
                        for(int i=0;i<wordCreated.size();i++){
                            if(wordCreated.get(i).getChar()==tile.getChar()){
                                playerHand.add(tile);
                                wordCreated.remove(i);
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
    public void onSubmit(View view) {//Submit results of activity back to the main activity
        Intent intent = new Intent();//New intent to send data back to main activity
        LinearLayout wordConstructor = findViewById(R.id.WordCreator);
        int tempSize = wordConstructor.getChildCount();//Get length of word created
        StringBuilder word = new StringBuilder("");
        LetterTile temproot = wordConstructor.findViewWithTag("root");
        int tempScore = g.getPScore();//Get current player score.
        int index = ((ViewGroup) temproot.getParent()).indexOfChild(temproot);//Get index of root node in word
        word.append(temproot.getChar());
        int leftLength = 0;//Value to store and check how many letters are to the left of the root
        int rightLength = 0;//Value to store and check how many letters are to the right of the root
        if (index > 0) {//If root node is not the first letter in the word.
            for (int i = index - 1; i >= 0; i--) {//Iterate through and add all letters left of the node starting from node
                LetterTile temp = (LetterTile) wordConstructor.getChildAt(i);//Get current character in word
                word.insert(0, temp.getChar());//Store character in intent
                tempScore += temp.getPointValue();//Add point value of non-root tile to score
                leftLength++;//Update left length tracker
            }
        }
        //Log.d("WordTest",word.toString()+" "+leftLength+" "+rightLength+" "+tempScore);
        if (index < tempSize) {//If root is not last character in the word
            for (int i = index + 1; i < tempSize; i++) {//Iterate through and add all letters right of the node starting from node
                LetterTile temp = (LetterTile) wordConstructor.getChildAt(i);//Get current character in word
                word.append(temp.getChar());//Store character in intent
                tempScore += temp.getPointValue();//Add point value of non-root tile to score
                rightLength++;//Update left length tracker

            }
        }
        //Log.d("WordTest",word.toString()+" "+leftLength+" "+rightLength+" "+tempScore);
        //if (word.length() >= 3 && word.length() < 7) {//Ensure that created word consists of 3 to 6 letters
            int rowNum = rootIndex.getxIndex();//Get row of root tile
            int colNum = rootIndex.getyIndex();//Get column of root tile
            if (grid[rowNum][colNum].getChar() == temproot.getChar()) {
                //Test to ensure that index gets the right tile on board
                boolean horizontal = grid[rowNum][colNum].isHorizontal();//Flag to declare if root is a part of a horizontal word
                //Flag to indicate if word collides with another and indicates that further actions unnecessary
                boolean collision = collisionTest(rowNum, colNum, leftLength, rightLength, horizontal);
                boolean overflow = overflowTest(rowNum, colNum, leftLength, rightLength, horizontal);//Flag to indicate overflow that indicates no further action to be taken

                if (horizontal) {//Operations for a root that is part of a horizontal word
                    //Log.d("Test", "Vertical Construction");
                    if (!collision && !overflow) {//If word passed overflow and collision tests
                        intent.putExtra(Intent.EXTRA_TEXT, word.toString());//Put created word in intent
                        StringBuilder handT = new StringBuilder("");//Temporary string to store current hand contents in
                        for (int i = 0; i < playerHand.size(); i++) {
                            handT.append(playerHand.get(i).getChar());//Store character of current tile in hand in temp string
                        }
                        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, handT.toString());//Put contents of hand in intent
                        if (word.length() > 1) {//Ensure that created word consists of more than just the original character
                            if (dictionary.isGoodWord(word.toString())) {//Ensure created word is valid
                                g.setPlayerScore(tempScore);//Update global player score to include points gained with created valid word.
                                wordToGrid(rowNum, colNum, leftLength, rightLength, horizontal, word.toString(), index);//Update local grid array with created word
                                g.setGrid(grid);//Store local grid array data in global counterpart
                                intent.putExtra(EXTRA_LEFT, leftLength);
                                intent.putExtra(EXTRA_RIGHT, rightLength);
                                intent.putExtra(EXTRA_INDEX, rootIndex);//Store location of root node on grid for use in updating visuals on main activity in intent
                                intent.putExtra(EXTRA_ROOT, index);
                                setResult(RESULT_OK, intent);//Send completion data back to main activity
                                finish();//Close activity and return to main.
                            } else {//If string is not a real word, display message to user.
                                Toast.makeText(getApplicationContext(), "Invalid Word Entered", Toast.LENGTH_SHORT).show();
                            }
                        } else {//If word consists solely of root character, then display error message to user
                            Toast.makeText(getApplicationContext(), "Word Must Be Longer Than A Single Letter", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {//Operations for a root that was part of a vertical word
                   // Log.d("Test", "Horizontal Construction");
                    if (!collision && !overflow) {//If word passed overflow and collision tests
                        intent.putExtra(Intent.EXTRA_TEXT, word.toString());//Put created word in intent
                        StringBuilder handT = new StringBuilder("");//Temporary string to store current hand contents in
                        for (int i = 0; i < playerHand.size(); i++) {
                            //LetterTile temp=(LetterTile) playerHand.get(i);//
                            handT.append(playerHand.get(i).getChar());//Store character of current tile in hand in temp string
                        }
                        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, handT.toString());//Put contents of hand in intent
                            if (dictionary.isGoodWord(word.toString())) {//Ensure created word is valid
                                g.setPlayerScore(tempScore);//Update global player score to include points gained with created valid word.
                                wordToGrid(rowNum, colNum, leftLength, rightLength, horizontal, word.toString(), index);//Update local grid array with created word
                                g.setGrid(grid);//Store local grid array data in global counterpart
                                intent.putExtra(EXTRA_LEFT, leftLength);//Store amount of characters that come before root in word in intent
                                intent.putExtra(EXTRA_RIGHT, rightLength);//Store amount of characters that come after root in word in intent
                                intent.putExtra(EXTRA_INDEX, rootIndex);//Store location of root node on grid for use in updating visuals on main activity in intent
                                setResult(RESULT_OK, intent);//Send completion data back to main activity
                                finish();//Close activity and return to main.
                            } else {//If string is not a real word, display message to user.
                                Toast.makeText(getApplicationContext(), "Invalid Word Entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                }
            } else {
                Log.d("TestError", "Characters for index [" + rowNum + "," + colNum + "] do not match. Chars are:" + grid[rowNum][colNum].getChar() + " " + temproot.getChar());
            }
        /*}else{
            Toast.makeText(getApplicationContext(), "Word Must Have 3 to 6 letters", Toast.LENGTH_SHORT).show();
        }*/
    }
    public void onBack(View view){
        //Return to previous activity without saving any changes
        setResult(RESULT_CANCELED);//Return unchanged signal to main
        finish();//Close activity and return to main.
    }
    public boolean collisionTest(int rowNum,int colNum, int leftLength, int rightLength,boolean horizontal){
        Log.d("Collision",String.valueOf(horizontal)+" "+String.valueOf(rowNum)+" "+String.valueOf(colNum));
        if(horizontal) {//Vertical word construction
            int collisionIndex = 0;//Location of collision on board relative to root
            for (int i = 1; i <= leftLength + 1; i++) {
                //Go through locations above root index up to projected index of tile that would be
                //directly above letter farthest "left" from root
                if ((rowNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                    if (grid[rowNum - i][colNum].getChar() != ' ') {//If a tile already exists at current location
                        collisionIndex = (rowNum - i);//Get row index of collision
                        //Display error message to user
                        Log.d("Collision",String.valueOf(grid[collisionIndex][colNum].getChar()));
                        Toast.makeText(getApplicationContext(), "Collision at index:" + collisionIndex + "," + colNum, Toast.LENGTH_SHORT).show();
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
                        Log.d("Collision",String.valueOf(grid[collisionIndex][colNum].getChar()));
                        Toast.makeText(getApplicationContext(), "Collision at index:" + collisionIndex + "," + colNum, Toast.LENGTH_SHORT).show();
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
                        Log.d("Collision",String.valueOf(grid[rowNum][collisionIndex].getChar()));
                        Toast.makeText(getApplicationContext(), "Collision at index:" + rowNum  + "," + collisionIndex, Toast.LENGTH_SHORT).show();
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
                        Log.d("Collision",String.valueOf(grid[rowNum][collisionIndex].getChar()));
                        Toast.makeText(getApplicationContext(), "Collision at index:" + rowNum + "," + collisionIndex, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Letters of word would go above grid boundaries by " +
                        overflowL + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((rowNum + rightLength) > 9) {//If word goes below boundaries
                int overflowR = Math.abs(rowNum - rightLength);//Overflow amount
                //Display error message to user
                Toast.makeText(getApplicationContext(), "Letters of word would go below grid boundaries by " +
                        overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((rowNum + rightLength) > 9 && (rowNum - leftLength) < -1) {//If word goes above and below boundaries
                int overflowL = Math.abs(rowNum - leftLength);//Overflow amount above
                int overflowR = Math.abs(rowNum + rightLength);//Overflow amount below
                //Display error message to user
                Toast.makeText(getApplicationContext(), "Letters of word would go above grid boundaries by " +
                        overflowL + " characters and below by " + overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }else{
            if ((colNum - leftLength) < 0) {//If word goes beyond the boundaries of the left side of grid
                int overflowL = Math.abs(colNum - leftLength);//Overflow amount
                //Display error message to user
                Toast.makeText(getApplicationContext(), "Letters of word would go left of grid boundaries by " +
                        overflowL + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((colNum + rightLength) > 9) {//If word goes beyond the boundaries of the right side of grid
                int overflowR = Math.abs(colNum - rightLength);//Overflow amount
                //Display error message to user
                Toast.makeText(getApplicationContext(), "Letters of word would go right of grid boundaries by " +
                        overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            } else if ((colNum + rightLength) > 9 && (colNum - leftLength) < -1) {//If word goes above and below boundaries
                int overflowL = Math.abs(colNum - leftLength);//Overflow amount above
                int overflowR = Math.abs(colNum - rightLength);//Overflow amount below
                //Display error message to user
                Toast.makeText(getApplicationContext(), "Letters of word would go to the left of grid boundaries by " +
                        overflowL + " characters and to the right of grid boundaries by "
                        + overflowR + " characters.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    public void wordToGrid(int rowNum,int colNum, int leftLength, int rightLength,boolean horizontal,String word,int rootIndex){
        //Store letters and details of tiles in word in local Boardtile grid
        ArrayList<tileIndex> tempLiving=g.getLivingTiles();
        ArrayList<tileIndex> tempDead=g.getDeadTiles();
        int initialLiving=tempLiving.size();
     //   Log.d("FatalGridTest", String.valueOf(rowNum)+" "+String.valueOf(colNum)+" "+String.valueOf(horizontal));
        if(horizontal) {//Vertical word construction
            int currentIndex = 0;//Location of collision on board relative to root
            if(rootIndex>0) {
                for (int i = 1; i <= leftLength; i++) {
                    //Go through locations above root index up to projected index of tile that would be
                    //directly above letter farthest "left" from root
                    if ((rowNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                        currentIndex = (rowNum - i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex - i));
                        //Log.d("TestBoardUpdate", String.valueOf(temp.getChar()));
                        temp.settIndex(currentIndex, colNum);
                        boolean life = livingTile(currentIndex, colNum,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            Log.d("FatalRemoveLife",String.valueOf(temp.getChar()));
                            temp.kill();
                        }
                        grid[currentIndex][colNum] = temp;
                    }else if((rootIndex-i)==0){ //Check to prevent out of bounds errors resulting from search
                        currentIndex = (rowNum - i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex - i));
                        //Log.d("TestBoardUpdate", String.valueOf(temp.getChar()));
                        temp.settIndex(currentIndex, colNum);
                        boolean life = livingTile(currentIndex, colNum,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            Log.d("FatalRemoveLife",String.valueOf(temp.getChar()));
                            temp.kill();
                        }
                        grid[currentIndex][colNum] = temp;
                    }
                }
            }
            if(rootIndex<9) {
                for (int i = 1; i <= rightLength; i++) {
                    //Go through locations below root index up to projected index of tile that would be
                    //directly below letter farthest "right" from root
                    if ((rowNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                        currentIndex = (rowNum + i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex + i));
                        temp.settIndex(currentIndex, colNum);
                        boolean life = livingTile(currentIndex, colNum, temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            Log.d("FatalRemoveLife",String.valueOf(temp.getChar()));
                            temp.kill();
                        }
                        grid[currentIndex][colNum] = temp;
                    } else if ((rowNum + i) == 9) {
                        currentIndex = (rowNum + i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex + i));
                        temp.settIndex(currentIndex, colNum);
                        boolean life = livingTile(currentIndex, colNum, temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            Log.d("FatalRemoveLife",String.valueOf(temp.getChar()));
                            temp.kill();
                        }
                        grid[currentIndex][colNum] = temp;

                    }
                }
            }
        }else {//Horizontal Word Construction
            int currentIndex = 0;//Location of collision on board relative to root
            if (rootIndex > 0) {
                for (int i = 1; i <= leftLength ; i++) {
                    //Go through locations left of the root index up to projected index of tile that would be
                    //directly to the left of the letter farthest from root, i.e the first letter of the word
                    if ((colNum - i > 0)) { //Check to prevent out of bounds errors resulting from search
                        currentIndex = (colNum - i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex - i));
                        temp.setHorizontalF();
                        temp.settIndex(rowNum, currentIndex);
                        boolean life = livingTile(rowNum, currentIndex,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            temp.kill();
                        }
                        grid[rowNum][currentIndex] = temp;
                    }else if((colNum-i)==0){ //Check to prevent out of bounds errors resulting from search
                        currentIndex = (colNum - i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex - i));
                        temp.setHorizontalF();
                        temp.settIndex(rowNum, currentIndex);
                        boolean life = livingTile(rowNum, currentIndex,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            temp.kill();
                        }
                        grid[rowNum][currentIndex] = temp;

                    }
                }
            }
            if (rootIndex < 9) {
                for (int i = 1; i <= rightLength ; i++) {
                    //Go through locations to the right of the root index up to projected index of tile that would be
                    //directly right of the letter farthest "right" from root, i.e the last letter of the word
                    if ((colNum + i) < 9) {//Check to prevent out of bounds errors resulting from search
                        currentIndex = (colNum + i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex + i));
                        temp.setHorizontalF();
                        temp.settIndex(rowNum, currentIndex);
                        boolean life = livingTile(rowNum, currentIndex,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            temp.kill();
                        }
                        grid[rowNum][currentIndex] = temp;
                    }else if((colNum+i)==9){//Check to prevent out of bounds errors resulting from search
                        currentIndex = (colNum + i);//Get row index of collision
                        BoardTile temp = new BoardTile(this, word.charAt(rootIndex + i));
                        temp.setHorizontalF();
                        temp.settIndex(rowNum, currentIndex);
                        boolean life = livingTile(rowNum, currentIndex,temp.isHorizontal());//Check if tile could be used again

                        if (life) {
                            tempLiving.add(temp.getBoardIndex());
                            temp.setClickable(true);
                        } else {//If tile can't be reused, inform system that it is dead.
                            temp.kill();
                        }
                        grid[rowNum][currentIndex] = temp;

                    }
                }
            }
        }
        //Kill root tile
        grid[rowNum][colNum].kill();
        ArrayList<Integer> deathList=new ArrayList<>();
        for(int i=0;i<initialLiving;i++){
            tileIndex tempIndex=tempLiving.get(i);
            int row=tempIndex.getxIndex();
            int col=tempIndex.getyIndex();
            if(!livingTile(row,col,grid[row][col].isHorizontal())){
                Log.d("FatalRemove","Killed:"+grid[row][col].getChar());
                deathList.add(i);
            }/*else if(!grid[row][col].isLiving()){
                deathList.add(i);
            }*/
        }
        for(int j=deathList.size()-1;j>0;j--){
            int tIndex=deathList.get(j);
            tileIndex test=tempLiving.remove(tIndex);
            Log.d("FatalTestRemove",test.getxIndex()+" "+test.getyIndex()+".Size:"+tempLiving.size()+".Char:"+grid[test.getxIndex()][test.getyIndex()].getChar());
            grid[test.getxIndex()][test.getyIndex()].kill();
            tempDead.add(test);
            Log.d("FatalTestRemove",String.valueOf(grid[test.getxIndex()][test.getyIndex()].isLiving())+","+tempDead.size());
        }
        g.setDeadTiles(tempDead);
        g.setLivingTiles(tempLiving);

    }
    public boolean livingTile(int rowNum,int colNum,boolean horizontal){//Function to determine if tile can possibly be re-used
        boolean life=true;//Flag for whether or not tile can provide valid moves
        Log.d("FatalTileTestLife",String.valueOf(horizontal)+" "+String.valueOf(rowNum)+","+String.valueOf(colNum)+"."+grid[rowNum][colNum].getChar());
        if(horizontal){//Check if letter is part of horizontal word
            //Check if the two spaces directly above or below tile is occupied by another letter
            if (rowNum + 2 < 9 && rowNum - 2 > 0) {
                if (grid[rowNum - 2][colNum].getChar() != ' ' && grid[rowNum + 2][colNum].getChar() != ' ') {
                    Log.d("FatalTestCheckA", "Dead, both tiles directly two squares above and below are occupied");
                    life = false;
                } else if (colNum + 1 < 9 && colNum - 1 > 0) {
                    //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                    Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                            grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                            grid[rowNum - 1][colNum + 1].getChar());
                    if ((grid[rowNum - 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//-+ ++
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ')//-- +-
                            ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//+- ++
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//-- -+
                            ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//+- -+
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')) //-- ++
                    {
                        //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                        life = false;
                        Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                    }
                }
            }else if (rowNum + 2 < 9 && rowNum - 2 < 0) {//If there is one or fewer spaces below tile
                if (rowNum - 1 < 0) {//If tile at bottom of board
                    if (grid[rowNum + 2][colNum].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tile directly two squares above is occupied");
                        life = false;
                    } else if (colNum + 1 < 9 && colNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar());
                        if (grid[rowNum + 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ') {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                        } else if (grid[rowNum + 2][colNum - 1].getChar() != ' ' || grid[rowNum + 2][colNum + 1].getChar() != ' ') {
                                life = false;
                                Log.d("FatalTestCheckC", "Dead, no space for words");
                             }
                        }
                } else {//If there is one space below the tile
                    if (grid[rowNum - 1][colNum].getChar() != ' ' && grid[rowNum + 2][colNum].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tiles directly two squares above and one square below are occupied");
                        life = false;
                    } else if (colNum + 1 < 9 && colNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                                grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                                grid[rowNum - 1][colNum + 1].getChar());
                        if ((grid[rowNum - 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//-+ ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ')//-- +-
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//+- ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//-- -+
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//+- -+
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')) //-- ++
                        {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all squares diagonally adjacent to tile are occupied");
                        } else if (colNum + 2 < 9 && colNum - 2 > 0) {
                            if (grid[rowNum - 1][colNum - 1].getChar() != ' ' || grid[rowNum - 1][colNum + 1].getChar() != ' ') {
                                //If either square bottom diagonal from tile are occupied
                                if (grid[rowNum + 2][colNum - 1].getChar() != ' ' || grid[rowNum + 2][colNum + 1].getChar() != ' ') {
                                    life = false;
                                    Log.d("FatalTestCheckC", "Dead, no space for words");
                                }
                            }
                        }
                    }
                }
            }else if (rowNum + 2 > 9 && rowNum - 2 > 0) {//If there is one or fewer spaces above tile
                if (rowNum + 1 > 9) {//If tile at top of board
                    if (grid[rowNum - 2][colNum].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tile directly two squares below is occupied");
                        life = false;
                    } else if (colNum + 1 < 9 && colNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum - 1][colNum + 1].getChar() + "," + grid[rowNum - 1][colNum - 1].getChar());
                        if (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ') {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                        } else if (grid[rowNum - 2][colNum - 1].getChar() != ' ' || grid[rowNum - 2][colNum + 1].getChar() != ' ') {
                            life = false;
                            Log.d("FatalTestCheckC", "Dead, no space for words");
                        }
                    }
                } else {//If there is one space below the tile
                    if (grid[rowNum + 1][colNum].getChar() != ' ' && grid[rowNum - 2][colNum].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tiles directly two squares above and one square below are occupied");
                        life = false;
                    } else if (colNum + 1 < 9 && colNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                                grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                                grid[rowNum - 1][colNum + 1].getChar());
                        if ((grid[rowNum - 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//-+ ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ')//-- +-
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//+- ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//-- -+
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//+- -+
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')) //-- ++
                        {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all squares diagonally adjacent to tile are occupied");
                        } else if (colNum + 2 < 9 && colNum - 2 > 0) {
                            if (grid[rowNum - 1][colNum - 1].getChar() != ' ' || grid[rowNum - 1][colNum + 1].getChar() != ' ') {
                                //If either square bottom diagonal from tile are occupied
                                if (grid[rowNum - 2][colNum - 1].getChar() != ' ' || grid[rowNum - 2][colNum + 1].getChar() != ' ') {
                                    life = false;
                                    Log.d("FatalTestCheckC", "Dead, no space for words");
                                }
                            }
                        }
                    }
                }
            }
        }else{
            //Check if the two spaces directly above or below tile is occupied by another letter
            if (colNum + 2 < 9 && colNum - 2 > 0) {
                if (grid[rowNum][colNum - 2].getChar() != ' ' && grid[rowNum][colNum+ 2].getChar() != ' ') {
                    Log.d("FatalTestCheckA", "Dead, both tiles directly two squares above and below are occupied");
                    life = false;
                } else if (rowNum + 1 < 9 && rowNum - 1 > 0) {
                    //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                    Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                            grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                            grid[rowNum - 1][colNum + 1].getChar());
                    if ((grid[rowNum - 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//-+ ++
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ')//-- +-
                            ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//+- ++
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//-- -+
                            ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//+- -+
                            || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')) //-- ++
                    {
                        //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                        life = false;
                        Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                    }
                }
            }else if (colNum + 2 < 9 && colNum - 2 < 0) {//If there is one or fewer spaces below tile
                if (colNum - 1 < 0) {//If tile at bottom of board
                    if (grid[rowNum][colNum+ 2].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tile directly two squares above is occupied");
                        life = false;
                    } else if (rowNum + 1 < 9 && rowNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum - 1][colNum + 1].getChar());
                        if (grid[rowNum + 1][colNum + 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ') {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                        } else if (grid[rowNum - 1][colNum + 2].getChar() != ' ' || grid[rowNum + 1][colNum + 2].getChar() != ' ') {
                            life = false;
                            Log.d("FatalTestCheckC", "Dead, no space for words");
                        }
                    }
                } else {//If there is one space below the tile
                    if (grid[rowNum][colNum - 1].getChar() != ' ' && grid[rowNum][colNum + 2].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tiles directly two squares above and one square below are occupied");
                        life = false;
                    } else if (rowNum + 1 < 9 && rowNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                                grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                                grid[rowNum - 1][colNum + 1].getChar());
                        if ((grid[rowNum - 1][colNum + 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//-+ ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ')//-- +-
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')//+- ++
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//-- -+
                                ||(grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')//+- -+
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')) //-- ++
                        {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all squares diagonally adjacent to tile are occupied");
                        } else if (rowNum + 2 < 9 && rowNum - 2 > 0) {
                            if (grid[rowNum + 1][colNum - 1].getChar() != ' ' || grid[rowNum - 1][colNum - 1].getChar() != ' ') {
                                //If either square bottom diagonal from tile are occupied
                                if (grid[rowNum + 1][colNum + 2].getChar() != ' ' || grid[rowNum - 1][colNum + 2].getChar() != ' ') {
                                    life = false;
                                    Log.d("FatalTestCheckC", "Dead, no space for words");
                                }
                            }
                        }
                    }
                }
            }else if (colNum + 2 > 9 && colNum - 2 > 0) {//If there is one or fewer spaces above tile
                if (colNum + 1 > 9) {//If tile at top of board
                    if (grid[rowNum][colNum - 2].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tile directly two squares below is occupied");
                        life = false;
                    } else if (rowNum + 1 < 9 && rowNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum + 1][colNum - 1].getChar() + "," + grid[rowNum - 1][colNum - 1].getChar());
                        if (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum - 1].getChar() != ' ') {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all diagonally adjacent to tile are occupied");
                        } else if (grid[rowNum - 1][colNum - 2].getChar() != ' ' || grid[rowNum + 1][colNum -2].getChar() != ' ') {
                            life = false;
                            Log.d("FatalTestCheckC", "Dead, no space for words");
                        }
                    }
                } else {//If there is one space below the tile
                    if (grid[rowNum][colNum + 1].getChar() != ' ' && grid[rowNum][colNum - 2].getChar() != ' ') {
                        Log.d("FatalTestCheckA", "Dead, tiles directly two squares above and one square below are occupied");
                        life = false;
                    } else if (rowNum + 1 < 9 && rowNum - 1 > 0) {
                        //Check tiles that are 1 space away diagonally if there is no obstruction two spaces above or below.
                        Log.d("TestRowColNumsA", grid[rowNum - 1][colNum - 1].getChar() + "," +
                                grid[rowNum + 1][colNum + 1].getChar() + "," + grid[rowNum + 1][colNum - 1].getChar() + "," +
                                grid[rowNum - 1][colNum + 1].getChar());
                        if ((grid[rowNum + 1][colNum - 1].getChar() != ' ' && grid[rowNum + 1][colNum + 1].getChar() != ' ')
                                || (grid[rowNum - 1][colNum - 1].getChar() != ' ' && grid[rowNum - 1][colNum + 1].getChar() != ' ')) {
                            //If all the tiles that are directly touch tile at a diagonal are occupied, tile is dead.
                            life = false;
                            Log.d("FatalTestCheckB", "Dead, all squares diagonally adjacent to tile are occupied");
                        } else if (rowNum + 2 < 9 && rowNum - 2 > 0) {
                            if (grid[rowNum - 1][colNum - 1].getChar() != ' ' || grid[rowNum + 1][colNum - 1].getChar() != ' ') {
                                //If either square bottom diagonal from tile are occupied
                                if (grid[rowNum - 1][colNum - 1].getChar() != ' ' || grid[rowNum + 1][colNum - 2].getChar() != ' ') {
                                    life = false;
                                    Log.d("FatalTestCheckC", "Dead, no space for words");
                                }
                            }
                        }
                    }
                }
            }
        }
        return life;
    }

}
