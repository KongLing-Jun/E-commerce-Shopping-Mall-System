package com.thinking.backendmall.common;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {
    private static final Safelist SAFE_LIST = Safelist.relaxed()
            .addTags("span")
            .addAttributes(":all", "style");

    private HtmlSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        return Jsoup.clean(html, SAFE_LIST);
    }
}
