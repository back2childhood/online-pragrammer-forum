package com.nowcoder.mycommunity.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // replacement character
    private static final String REPLACEMENT = "**";

    // root node
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // add to trie
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("load keyword.txt failed : " + e.getMessage());
        }
    }

    // add a sensitive keyword to the prefix tree
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); ++i) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // initiate son node
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // point to the sub node, and move to next cycle
            tempNode = subNode;

            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * Pass in the original string and return the filtered string
     *
     * @param : the original string
     * @return : the filtered string
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // pointer1
        TrieNode tempNode = rootNode;
        // pointer2,3
        int begin = 0, position = 0;
        // result
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c) && position != text.length() - 1) {
                // if pointer1 is at the root node, add this symbol to the result and move pointer2 to the next step
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // Pointer 3 goes one step down regardless of whether the symbol is at the beginning or in the middle
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // Strings beginning from begin are not sensitive words
                sb.append(text.charAt(begin));
                // move to the next position
                position = ++begin;
                // redirect to the root node
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // find the sensitive word, replace it with the REPLACEMENT
                sb.append(REPLACEMENT);

                begin = ++position;
                tempNode = rootNode;
            } else {
                // check the next character
                if (position < text.length() - 1) {
                    position++;
                } else {
                    position = begin;
                }
            }
        }

        //
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // judge whether it is a symbol
    private boolean isSymbol(Character c) {
        // c < 0x2e80 || c > 0x9fff
        // east asian's writing
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }

    // trie
    private class TrieNode {
        // keyword end sign
        private boolean isKeywordSign = false;

        // child node
        // key is child nodes' value, values is child nodes
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordSign;
        }

        public void setKeywordEnd(boolean keywordSign) {
            isKeywordSign = keywordSign;
        }

        // add new child node
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // get child node
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
