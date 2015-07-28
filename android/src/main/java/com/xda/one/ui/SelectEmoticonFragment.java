package com.xda.one.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import com.xda.one.R;
import com.xda.one.parser.ContentParser;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class SelectEmoticonFragment extends DialogFragment {

    private EditText mEditText;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final String string = (String) v.getTag();
            final int start = mEditText.getSelectionStart();
            mEditText.getText().insert(start, string);
            dismiss();
        }
    };

    public SelectEmoticonFragment(final EditText txt) {
        mEditText = txt;
    }

    public static SelectEmoticonFragment createInstance(final EditText editText) {
        return new SelectEmoticonFragment(editText);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_emoticon_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle("Insert Smilie");

        final GridView gridView = (GridView) view.findViewById(R.id.select_emoticon_grid);
        gridView.setAdapter(new EmoticonAdapter(getActivity(), mOnClickListener));
    }

    private static class EmoticonAdapter extends BaseAdapter {

        private final ArrayList<Map.Entry<Pattern, Integer>> mEmoticons;

        private final LayoutInflater mLayoutInflater;

        private final View.OnClickListener mOnClickListener;

        public EmoticonAdapter(final Context context, final View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
            mEmoticons = new ArrayList<>(ContentParser.EMOTICONS_MAP.entrySet());
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mEmoticons.size();
        }

        @Override
        public Map.Entry<Pattern, Integer> getItem(final int position) {
            return mEmoticons.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        @Override
        public View getView(final int position, final View convertView,
                            final ViewGroup parent) {
            final View view = convertView == null
                    ? mLayoutInflater.inflate(R.layout.select_emoticon_grid_item, parent, false)
                    : convertView;
            final Map.Entry<Pattern, Integer> entry = getItem(position);
            final ImageButton imageButton = (ImageButton) view;
            imageButton.setImageResource(entry.getValue());
            imageButton.setTag(entry.getKey().pattern());
            imageButton.setOnClickListener(mOnClickListener);
            return view;
        }
    }
}