package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.model.response.container.ResponseMessageContainer;
import com.xda.one.model.augmented.container.AugmentedMessageContainer;

import static com.xda.one.api.retrofit.RetrofitPrivateMessageClient.getClient;
import static com.xda.one.ui.MessagePagerFragment.MessageContainerType;

public class MessageLoader extends AsyncLoader<AugmentedMessageContainer> {

    private final int mPage;

    private final MessageContainerType mContainerType;

    private final PrivateMessageClient mMessageClient;

    public MessageLoader(final Context context, int page, final MessageContainerType type) {
        super(context);

        mPage = page;
        mContainerType = type;
        mMessageClient = getClient(getContext());
    }

    @Override
    public AugmentedMessageContainer loadInBackground() {
        final ResponseMessageContainer responseMessageContainer;
        switch (mContainerType) {
            case INBOX:
                responseMessageContainer = mMessageClient.getInboxMessages(mPage);
                break;
            case OUTBOX:
                responseMessageContainer = mMessageClient.getSentMessages(mPage);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (responseMessageContainer == null) {
            return null;
        }
        return new AugmentedMessageContainer(getContext(), responseMessageContainer);
    }

    @Override
    public void releaseResources(AugmentedMessageContainer data) {
    }
}