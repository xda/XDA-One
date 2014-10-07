package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseAttachment implements Parcelable {

    public static final Parcelable.Creator<ResponseAttachment> CREATOR
            = new Parcelable.Creator<ResponseAttachment>() {
        @Override
        public ResponseAttachment createFromParcel(Parcel source) {
            return new ResponseAttachment(source);
        }

        @Override
        public ResponseAttachment[] newArray(int size) {
            return new ResponseAttachment[size];
        }
    };

    @JsonProperty(value = "dateline")
    private long mDateLine;

    @JsonProperty(value = "thumbnail_dateline")
    private long mThumbnailDateLine;

    @JsonProperty(value = "filename")
    private String mFileName;

    @JsonProperty(value = "filesize")
    private float mFileSize;

    @JsonProperty(value = "visible")
    private int mVisible;

    @JsonProperty(value = "attachmentid")
    private int mAttachmentId;

    @JsonProperty(value = "counter")
    private int mCounter;

    @JsonProperty(value = "postid")
    private int mPostId;

    @JsonProperty(value = "hasthumbnail")
    private int mHasThumbnail;

    @JsonProperty(value = "thumbnail_filesize")
    private int mThumbnailFileSize;

    @JsonProperty(value = "build_thumbnail")
    private int mBuildThumbnail;

    @JsonProperty(value = "newwindow")
    private int mNewWindow;

    @JsonProperty(value = "attachment_url")
    private String mAttachmentUrl;

    public ResponseAttachment() {
    }

    private ResponseAttachment(final Parcel in) {
        mDateLine = in.readLong();
        mThumbnailDateLine = in.readLong();
        mFileName = in.readString();
        mFileSize = in.readFloat();
        mVisible = in.readInt();
        mAttachmentId = in.readInt();
        mCounter = in.readInt();
        mPostId = in.readInt();
        mHasThumbnail = in.readInt();
        mThumbnailFileSize = in.readInt();
        mBuildThumbnail = in.readInt();
        mNewWindow = in.readInt();
        mAttachmentUrl = in.readString();
    }

    public long getDateLine() {
        return mDateLine;
    }

    public long getThumbnailDateLine() {
        return mThumbnailDateLine;
    }

    public String getFileName() {
        return mFileName;
    }

    public float getFileSize() {
        return mFileSize;
    }

    public boolean isVisible() {
        return mVisible != 0;
    }

    public int getAttachmentId() {
        return mAttachmentId;
    }

    public int getCounter() {
        return mCounter;
    }

    public int getPostId() {
        return mPostId;
    }

    public boolean hasThumbnail() {
        return mHasThumbnail != 0;
    }

    public int getThumbnailFileSize() {
        return mThumbnailFileSize;
    }

    public int getBuildThumbnail() {
        return mBuildThumbnail;
    }

    public boolean isNewWindow() {
        return mNewWindow != 0;
    }

    public String getAttachmentUrl() {
        return mAttachmentUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDateLine);
        dest.writeLong(mThumbnailDateLine);
        dest.writeString(mFileName);
        dest.writeFloat(mFileSize);
        dest.writeInt(mVisible);
        dest.writeInt(mAttachmentId);
        dest.writeInt(mCounter);
        dest.writeInt(mPostId);
        dest.writeInt(mHasThumbnail);
        dest.writeInt(mThumbnailFileSize);
        dest.writeInt(mBuildThumbnail);
        dest.writeInt(mNewWindow);
        dest.writeString(mAttachmentUrl);
    }
}