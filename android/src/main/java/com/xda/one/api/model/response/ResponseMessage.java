package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.parser.TextDataStructure;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage implements Message {

    public static final Parcelable.Creator<ResponseMessage> CREATOR
            = new Parcelable.Creator<ResponseMessage>() {
        @Override
        public ResponseMessage createFromParcel(final Parcel in) {
            return new ResponseMessage(in);
        }

        @Override
        public ResponseMessage[] newArray(final int size) {
            return new ResponseMessage[size];
        }
    };

    private final ResponseAvatar mAvatar;

    @JsonProperty(value = "pmid")
    private int mPmId;

    @JsonProperty(value = "fromuserid")
    private String mFromUserId;

    @JsonProperty(value = "fromusername")
    private String mFromUserName;

    @JsonProperty(value = "title")
    private String mTitle;

    @JsonProperty(value = "message")
    private String mMessageContent;

    @JsonProperty(value = "touserarray")
    private String mToUserArray;

    @JsonProperty(value = "dateline")
    private long mDate;

    @JsonProperty(value = "showsignature")
    private int mShowSignature;

    @JsonProperty(value = "allowsmilie")
    private int mAllowSmilie;

    /*
     * The meaning behind the int:
     * 0: unread
     * 1: read
     * 2: forwarded message
     * 3: BCC'd message
     */
    @JsonProperty(value = "messageread")
    private int mMessageRead;

    public ResponseMessage(final Parcel in) {
        mPmId = in.readInt();
        mFromUserId = in.readString();
        mFromUserName = in.readString();
        mTitle = in.readString();
        mMessageContent = in.readString();
        mToUserArray = in.readString();
        mDate = in.readLong();
        mShowSignature = in.readInt();
        mAllowSmilie = in.readInt();
        mAvatar = new ResponseAvatar(in);
        mMessageRead = in.readInt();
    }

    public ResponseMessage() {
        mAvatar = new ResponseAvatar();
    }

    @Override
    public int getPmId() {
        return mPmId;
    }

    @Override
    public String getFromUserId() {
        return mFromUserId;
    }

    @Override
    public String getFromUserName() {
        return mFromUserName;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getMessageContent() {
        return mMessageContent;
    }

    @Override
    public String getToUserArray() {
        return mToUserArray;
    }

    @Override
    public long getDate() {
        return mDate * 1000;
    }

    @Override
    public boolean isMessageUnread() {
        return mMessageRead == 0;
    }

    @Override
    public boolean isShowSignature() {
        return mShowSignature != 0;
    }

    @Override
    public boolean isAllowSmilie() {
        return mAllowSmilie != 0;
    }

    @Override
    public String getAvatarUrl() {
        return mAvatar.getAvatarUrl();
    }

    @JsonProperty(value = "avatar_url")
    private void setAvatarUrl(final String avatarUrl) {
        mAvatar.setAvatarUrl(avatarUrl);
    }

    @Override
    public int getMessageReadStatus() {
        return mMessageRead;
    }

    // Don't do anything if this is called manually
    @Override
    public void setMessageReadStatus(final int messageRead) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mPmId);
        dest.writeString(mFromUserId);
        dest.writeString(mFromUserName);
        dest.writeString(mTitle);
        dest.writeString(mMessageContent);
        dest.writeString(mToUserArray);
        dest.writeLong(mDate);
        dest.writeInt(mShowSignature);
        dest.writeInt(mAllowSmilie);
        mAvatar.writeToParcel(dest, flags);
        dest.writeInt(mMessageRead);
    }

    /*
     * Message interface
     */
    // A ResponseMessage does not have a submessage - simply return null
    @Override
    public String getSubMessage() {
        return null;
    }

    // A ResponseMessage does not have a text data structure - simply return null
    @Override
    public TextDataStructure getTextDataStructure() {
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ResponseMessage)) {
            return false;
        }

        final ResponseMessage other = (ResponseMessage) o;
        return getPmId() == other.getPmId();
    }

    @Override
    public int hashCode() {
        return mPmId;
    }
}