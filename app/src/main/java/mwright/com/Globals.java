package mwright.com;

import java.util.ArrayList;

public class Globals {
    private static Globals instance;
    //Global variables:
    private int pScore;//Player Score
    private int cScore;//Computer Score
    private BoardTile[][] grid=new BoardTile[10][10];
    private ArrayList<tileIndex> livingTiles=new ArrayList<tileIndex>();
    private ArrayList<tileIndex> deadTiles=new ArrayList<tileIndex>();
    private Dictionary dictionary;//Dictionary of valid words
    private int pOutCount = 0;
    private int cOutCount = 0;
    // Restrict the constructor from being instantiated
    private Globals(){}
    //Mutator functions:
    public void setPlayerScore(int ps){
        this.pScore=ps;
    }
    public void setCompScore(int cs){
        this.cScore=cs;
    }
    public void setGrid(BoardTile[][] gridIn){
        this.grid=gridIn;
    }
    public void setDictionary(Dictionary dictionaryIn){
        this.dictionary=dictionaryIn;
    }
    public BoardTile[][] getGrid(){
        return grid;
    }
    //Accessor functions:
    public int getPScore(){
        return pScore;
    }
    public int getCScore(){
        return cScore;
    }
    public Dictionary getDictionary() {
        return dictionary;
    }
    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
    public int getpOutCount(){return pOutCount;}
    public int getcOutCount(){return cOutCount;}
    public void setpOutCount(int PlayerOut){pOutCount=PlayerOut;}
    public void setcOutCount(int CompOut){cOutCount=CompOut;}
    public ArrayList<tileIndex> getLivingTiles(){return livingTiles;}
    public void setLivingTiles(ArrayList<tileIndex> indexArray){livingTiles=indexArray;}

    public ArrayList<tileIndex> getDeadTiles() {
        return deadTiles;
    }

    public void setDeadTiles(ArrayList<tileIndex> deadTiles) {
        this.deadTiles = deadTiles;
    }
}
