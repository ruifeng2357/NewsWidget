package com.damytech.STData;

import android.os.Parcel;
import android.os.Parcelable;

public class STBanner implements Parcelable {
    public long Id = 0;
	public String Title = "";
    public String LinkURL = "";

    public STBanner() {}

    public STBanner(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(Id);
        dest.writeString(Title);
        dest.writeString(LinkURL);

        return;
    }

    private void readFromParcel(Parcel in)
    {
        Id = in.readLong();
        Title = in.readString();
        LinkURL = in.readString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Parcelable.Creator<STBanner> CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new STBanner(source);
        }
        @Override
        public Object[] newArray(int size) {
            return new STBanner[size];
        }
    };
}