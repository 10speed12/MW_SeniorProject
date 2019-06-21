package mwright.com;

import android.os.Parcel;
import android.os.Parcelable;

public class tileIndex implements Parcelable {
    private int xIndex;
    private int yIndex;
    tileIndex(int x, int y){
        xIndex=x;
        yIndex=y;
    }
    public tileIndex(Parcel source) {
        xIndex=source.readInt();
        yIndex=source.readInt();
    }
    public int getxIndex(){return xIndex;}
    public int getyIndex(){return yIndex;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(xIndex);
        parcel.writeInt(yIndex);
    }
    public static final Creator<tileIndex> CREATOR = new Creator<tileIndex>() {
        @Override
        public tileIndex[] newArray(int size) {
            return new tileIndex[size];
        }

        @Override
        public tileIndex createFromParcel(Parcel source) {
            return new tileIndex(source);
        }
    };
}

