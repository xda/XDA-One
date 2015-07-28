package com.xda.one.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;

import com.xda.one.R;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentParser {

    public static final Map<Pattern, Integer> EMOTICONS_MAP = new LinkedHashMap<>();

    private static final HtmlSpanner SPANNER = new HtmlSpanner();

    private static TextProcessor sTextProcessor;

    static {
        SPANNER.registerHandler("mention", new XDATagHandlers.MentionHandler());
        SPANNER.registerHandler("td", new XDATagHandlers.QuoteTagHandler());
        SPANNER.registerHandler("xdaquote", new XDATagHandlers.QuoteTagHandler());
        SPANNER.registerHandler("img", new XDATagHandlers.ImageHandler());

        SPANNER.registerHandler("a", new XDATagHandlers.LinkHandler());

        addPattern(EMOTICONS_MAP, ":)", R.drawable.smile);
        addPattern(EMOTICONS_MAP, ":(", R.drawable.frown);
        addPattern(EMOTICONS_MAP, ":victory:", R.drawable.victory);
        addPattern(EMOTICONS_MAP, ":confused:", R.drawable.confused);
        addPattern(EMOTICONS_MAP, ":silly:", R.drawable.silly);
        addPattern(EMOTICONS_MAP, ":laugh:", R.drawable.laugh);
        addPattern(EMOTICONS_MAP, ":mad:", R.drawable.mad);
        addPattern(EMOTICONS_MAP, ":highfive:", R.drawable.highfive);
        addPattern(EMOTICONS_MAP, ":good:", R.drawable.good);
        addPattern(EMOTICONS_MAP, ":fingers-crossed:", R.drawable.fingers_crossed);
        addPattern(EMOTICONS_MAP, ":p", R.drawable.tongue);
        addPattern(EMOTICONS_MAP, ":cyclops:", R.drawable.cyclops);
        addPattern(EMOTICONS_MAP, ";)", R.drawable.wink);
        addPattern(EMOTICONS_MAP, ":crying:", R.drawable.crying);
        addPattern(EMOTICONS_MAP, ":D", R.drawable.biggrin);
        addPattern(EMOTICONS_MAP, ":cowboy:", R.drawable.cowboy);
        addPattern(EMOTICONS_MAP, ":o", R.drawable.redface);
        addPattern(EMOTICONS_MAP, ":angel:", R.drawable.angel);
        addPattern(EMOTICONS_MAP, ":rolleyes:", R.drawable.rolleyes);
        addPattern(EMOTICONS_MAP, ":cool:", R.drawable.cool);
        addPattern(EMOTICONS_MAP, ":eek:", R.drawable.eek);
        addPattern(EMOTICONS_MAP, ":svetius:", R.drawable.good);
    }

    private static TextProcessor getBBCodeProcessor(final Context context) {
        if (sTextProcessor == null) {
            InputStream inputStream = null;
            try {
                inputStream = context.getAssets().open("xda.xml");
                sTextProcessor = BBProcessorFactory.getInstance().create(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
        return sTextProcessor;
    }

    private static void addPattern(final Map<Pattern, Integer> map, String smile, int resource) {
        map.put(Pattern.compile(smile, Pattern.LITERAL), resource);
    }

    public static boolean addSmiles(final Context context, final Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : EMOTICONS_MAP.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end()) {
                        spannable.removeSpan(span);
                    } else {
                        set = false;
                        break;
                    }
                }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable parseAndSmilifyBBCode(final Context context, final CharSequence text) {
        final Spannable parsedText = parseBBCode(context, text);
        addSmiles(context, parsedText);
        return parsedText;
    }

    public static Spannable parseBBCode(final Context context, final CharSequence text) {
        return parseBBCode(ContentParser.getBBCodeProcessor(context), text);
    }

    private static Spannable parseBBCode(final TextProcessor processor,
                                         final CharSequence messageText) {
        final String htmlCode = processor.process(messageText).toString();
        final String unescapedHtml = StringEscapeUtils.unescapeHtml4(htmlCode);
        return SPANNER.fromHtml(unescapedHtml);
    }
}