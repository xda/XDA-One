package com.xda.one.model.augmented.container;

import android.content.Context;

import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.interfaces.container.MessageContainer;
import com.xda.one.model.augmented.AugmentedMessage;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AugmentedMessageContainer implements MessageContainer {

    private final MessageContainer mMessageContainer;

    private final List<AugmentedMessage> mAugmentedMessages;

    public AugmentedMessageContainer(final Context context, final MessageContainer container) {
        mMessageContainer = container;
        mAugmentedMessages = new ArrayList<>();

        for (final Message message : mMessageContainer.getMessages()) {
            mAugmentedMessages.add(new AugmentedMessage(context, message));
        }
    }

    @Override
    public List<AugmentedMessage> getMessages() {
        return mAugmentedMessages;
    }

    @Override
    public int getTotalPages() {
        return mMessageContainer.getTotalPages();
    }

    @Override
    public int getMessagesPerPage() {
        return mMessageContainer.getMessagesPerPage();
    }

    @Override
    public int getCurrentPage() {
        return mMessageContainer.getCurrentPage();
    }
}