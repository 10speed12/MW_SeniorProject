package mwright.com;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Random;
public class Dictionary implements Parcelable {
    private ArrayList<String> wordList=new ArrayList<String>();
    private HashSet<String> wordSet=new HashSet<String>();
//    private HashMap<String,ArrayList<String>> lettersToWord=new HashMap<String,ArrayList<String>>();
//    private HashMap<Integer,ArrayList<String>> sizeToWord=new HashMap<Integer, ArrayList<String>>();
    //private  int wordLength=DEFAULT_WORD_LENGTH;
    public Dictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

        }

    }

    private Dictionary(Parcel in) {
        readFromParcel(in);
    }
    private void readFromParcel(Parcel in){
        wordList=in.readArrayList(String.class.getClassLoader());
        wordSet=(HashSet<String>) in.readSerializable();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(wordList);
        out.writeSerializable(wordSet);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Dictionary> CREATOR = new Parcelable.Creator<Dictionary>() {
        public Dictionary createFromParcel(Parcel in) {
            return new Dictionary(in);
        }

        public Dictionary[] newArray(int size) {
            return new Dictionary[size];
        }
    };
    public boolean isGoodWord(String word) {
        return wordSet.contains(word.toLowerCase());
    }
    public String sortLetters(String word){
        char[] temp=word.toCharArray();
        Arrays.sort(temp);
        String result=new String(temp);
        return result;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }
}
