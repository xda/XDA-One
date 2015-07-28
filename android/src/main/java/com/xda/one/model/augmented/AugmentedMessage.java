package com.xda.one.model.augmented;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.response.ResponseMessage;
import com.xda.one.parser.ContentParser;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.util.StringUtils;

public class AugmentedMessage implements Message {

    public static final Parcelable.Creator<AugmentedMessage> CREATOR
            = new Parcelable.Creator<AugmentedMessage>() {
        @Override
        public AugmentedMessage createFromParcel(final Parcel in) {
            return new AugmentedMessage(in);
        }

        @Override
        public AugmentedMessage[] newArray(final int size) {
            return new AugmentedMessage[size];
        }
    };

    private static final int MAX_LENGTH = 100;

    private final Message mMessage;

    private int mMessageRead;

    private String mSubMessage;

    private Spanned mMessageContent;

    private TextDataStructure mTextDataStructure;

    public AugmentedMessage(final Context context, final Message message) {
        mMessage = message;

        // Augmentation time
        mMessageRead = message.getMessageReadStatus();

        final CharSequence messageText = mMessage.getMessageContent();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        final Spanned spanned = ContentParser.parseBBCode(context, messageText);
        setMessageContent(spanned);

        final String sub = StringUtils.trimCharSequence(spanned.toString(), MAX_LENGTH);
        mSubMessage = StringUtils.removeWhiteSpaces(sub);
    }

    private AugmentedMessage(final Parcel in) {
        mMessage = new ResponseMessage(in);

        mMessageRead = in.readInt();

        final Spanned spanned = Html.fromHtml(in.readString());
        setMessageContent(spanned);

        mSubMessage = in.readString();
    }

    @Override
    public int getPmId() {
        return mMessage.getPmId();
    }

    @Override
    public String getFromUserId() {
        return mMessage.getFromUserId();
    }

    @Override
    public String getFromUserName() {
        return mMessage.getFromUserName();
    }

    @Override
    public String getTitle() {
        return mMessage.getTitle();
    }

    @Override
    public long getDate() {
        return mMessage.getDate();
    }

    @Override
    public boolean isMessageUnread() {
        return mMessageRead == 0;
    }

    @Override
    public String getToUserArray() {
        return mMessage.getToUserArray();
    }

    @Override
    public boolean isShowSignature() {
        return mMessage.isShowSignature();
    }

    @Override
    public boolean isAllowSmilie() {
        return mMessage.isAllowSmilie();
    }

    @Override
    public int getMessageReadStatus() {
        return mMessageRead;
    }

    @Override
    public void setMessageReadStatus(int messageRead) {
        mMessageRead = messageRead;
    }

    @Override
    public String getAvatarUrl() {
        return mMessage.getAvatarUrl();
    }

    // Class specific stuff starts here
    @Override
    public Spanned getMessageContent() {
        return mMessageContent;
    }

    private void setMessageContent(final Spanned messageContent) {
        mMessageContent = messageContent;

        // While setting the message, also update the data structure
        mTextDataStructure = new TextDataStructure(messageContent);
    }

    @Override
    public String getSubMessage() {
        return mSubMessage;
    }

    @Override
    public TextDataStructure getTextDataStructure() {
        return mTextDataStructure;
    }

    private Message getMessage() {
        return mMessage;
    }

    // Parcelable starts here
    @Override
    public int describeContents() {
        return mMessage.describeContents();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        mMessage.writeToParcel(dest, flags);

        dest.writeInt(mMessageRead);
        dest.writeString(Html.toHtml(mMessageContent));
        dest.writeString(mSubMessage);
    }

    // Equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AugmentedMessage)) {
            return false;
        }

        final AugmentedMessage other = (AugmentedMessage) o;
        return getMessage().equals(other.getMessage());
    }

    @Override
    public int hashCode() {
        return mMessage.hashCode() + 41;
    }
}
