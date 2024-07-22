package org.example.service;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class TextFixtures {

    public static Elements buildElements(String baseDomainUrl, String newUrl) {
        var attributes = new Attributes();
        attributes.add("href", newUrl);
        return new Elements(new Element(Tag.valueOf("a[href]"), baseDomainUrl, attributes));
    }
}
