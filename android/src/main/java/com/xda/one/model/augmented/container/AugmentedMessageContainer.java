package com.xda.one.model.augmented.container;

import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.interfaces.container.MessageContainer;
import com.xda.one.api.model.response.container.ResponseMessageContainer;
import com.xda.one.model.augmented.AugmentedMessage;
import com.xda.one.util.Utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AugmentedMessageContainer implements MessageContainer {

    private final MessageContainer mMessageContainer;

    private final List<AugmentedMessage> mAugmentedMessages;

    public AugmentedMessageContainer(final Context context, final ResponseMessageContainer
            container) {
        mMessageContainer = container;
        mAugmentedMessages = new ArrayList<>();

        final List<? extends Message> list = mMessageContainer.getMessages();
        if (!Utils.isCollectionEmpty(list)) {
            for (final Message responseMessage : list) {
                mAugmentedMessages.add(new AugmentedMessage(context, responseMessage));
            }
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