package com.training.studyfx.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownToHtml {
    
    private static final String CSS_STYLES = """
        <style>
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                line-height: 1.6;
                color: #2e7d32;
                margin: 0;
                padding: 12px;
                background: transparent;
                font-size: 14px;
                overflow: hidden;
                -webkit-user-select: text;
                user-select: text;
            }
            
            html, body {
                overflow: hidden;
                height: auto;
                width: 100%;
            }
            
            h1, h2, h3, h4, h5, h6 {
                color: #1565c0;
                margin: 16px 0 8px 0;
                font-weight: 600;
            }
            
            h1 { font-size: 18px; }
            h2 { font-size: 16px; }
            h3 { font-size: 15px; }
            h4, h5, h6 { font-size: 14px; }
            
            p {
                margin: 8px 0;
                text-align: justify;
            }
            
            strong {
                color: #1976d2;
                font-weight: 600;
            }
            
            em {
                color: #388e3c;
                font-style: italic;
            }
            
            code {
                background: #f5f5f5;
                border: 1px solid #e0e0e0;
                border-radius: 4px;
                padding: 2px 6px;
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                font-size: 13px;
                color: #d32f2f;
            }
            
            pre {
                background: #f8f8f8;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                padding: 12px;
                overflow-x: auto;
                margin: 12px 0;
            }
            
            pre code {
                background: transparent;
                border: none;
                padding: 0;
                font-size: 13px;
                color: #263238;
            }
            
            ul, ol {
                margin: 8px 0;
                padding-left: 20px;
            }
            
            li {
                margin: 4px 0;
                line-height: 1.5;
            }
            
            ul li::marker {
                color: #4caf50;
                font-weight: bold;
            }
            
            blockquote {
                border-left: 4px solid #4caf50;
                margin: 12px 0;
                padding-left: 12px;
                color: #555;
                font-style: italic;
            }
            
            table {
                border-collapse: collapse;
                width: 100%;
                margin: 12px 0;
            }
            
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            
            th {
                background-color: #f2f2f2;
                font-weight: 600;
            }
            
            a {
                color: #1976d2;
                text-decoration: none;
            }
            
            a:hover {
                text-decoration: underline;
            }
            
            .emoji {
                font-size: 16px;
            }
        </style>
        """;
    
    public static String convertToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return createHtmlDocument("Không có nội dung");
        }
        
        String html = markdown;
        
        // Xử lý headers (# ## ###)
        html = processHeaders(html);
        
        // Xử lý code blocks (```)
        html = processCodeBlocks(html);
        
        // Xử lý inline code (`code`)
        html = processInlineCode(html);
        
        // Xử lý bold (**text**)
        html = processBold(html);
        
        // Xử lý italic (*text*)
        html = processItalic(html);
        
        // Xử lý bullet lists
        html = processBulletLists(html);
        
        // Xử lý numbered lists
        html = processNumberedLists(html);
        
        // Xử lý line breaks
        html = processLineBreaks(html);
        
        return createHtmlDocument(html);
    }
    
    private static String createHtmlDocument(String content) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                %s
            </head>
            <body>
                %s
            </body>
            </html>
            """, CSS_STYLES, content);
    }
    
    private static String processHeaders(String text) {
        // H1-H6 headers
        Pattern pattern = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int level = matcher.group(1).length();
            String content = matcher.group(2);
            matcher.appendReplacement(sb, String.format("<h%d>%s</h%d>", level, content, level));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private static String processCodeBlocks(String text) {
        // Code blocks with ```
        Pattern pattern = Pattern.compile("```([\\s\\S]*?)```", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String code = matcher.group(1).trim();
            matcher.appendReplacement(sb, String.format("<pre><code>%s</code></pre>", escapeHtml(code)));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private static String processInlineCode(String text) {
        // Inline code with `
        Pattern pattern = Pattern.compile("`([^`]+)`");
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String code = matcher.group(1);
            matcher.appendReplacement(sb, String.format("<code>%s</code>", escapeHtml(code)));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private static String processBold(String text) {
        // Bold with **text**
        Pattern pattern = Pattern.compile("\\*\\*([^*]+)\\*\\*");
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String boldText = matcher.group(1);
            matcher.appendReplacement(sb, String.format("<strong>%s</strong>", boldText));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private static String processItalic(String text) {

        Pattern pattern = Pattern.compile("(?<!\\*)\\*([^*]+)\\*(?!\\*)");
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String italicText = matcher.group(1);
            matcher.appendReplacement(sb, String.format("<em>%s</em>", italicText));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private static String processBulletLists(String text) {
        // Split by paragraphs
        String[] paragraphs = text.split("\n\n");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i];
            
            if (paragraph.contains("\n*") || paragraph.startsWith("*")) {
                // This is a bullet list
                result.append("<ul>\n");
                String[] lines = paragraph.split("\n");
                
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("*") && line.length() > 1) {
                        String content = line.substring(1).trim();
                        result.append(String.format("<li>%s</li>\n", content));
                    }
                }
                result.append("</ul>\n");
            } else {
                result.append(paragraph);
            }
            
            // Add paragraph break
            if (i < paragraphs.length - 1) {
                result.append("\n\n");
            }
        }
        
        return result.toString();
    }
    
    private static String processNumberedLists(String text) {
        // Similar to bullet lists but with numbers
        Pattern pattern = Pattern.compile("^(\\d+\\.)\\s+(.+)$", Pattern.MULTILINE);
        
        if (pattern.matcher(text).find()) {
            String[] paragraphs = text.split("\n\n");
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i];
                Matcher matcher = pattern.matcher(paragraph);
                
                if (matcher.find()) {
                    result.append("<ol>\n");
                    String[] lines = paragraph.split("\n");
                    
                    for (String line : lines) {
                        Matcher lineMatcher = pattern.matcher(line);
                        if (lineMatcher.find()) {
                            String content = lineMatcher.group(2);
                            result.append(String.format("<li>%s</li>\n", content));
                        }
                    }
                    result.append("</ol>\n");
                } else {
                    result.append(paragraph);
                }
                
                if (i < paragraphs.length - 1) {
                    result.append("\n\n");
                }
            }
            
            return result.toString();
        }
        
        return text;
    }
    
private static String processLineBreaks(String text) {
    // Chuyển đổi các dòng trống liên tiếp thành thẻ đoạn văn
    text = text.replaceAll("\n\n+", "</p><p>");

    // Chuyển đổi các dòng trống đơn thành thẻ xuống dòng
    text = text.replaceAll("(?<!>)\n(?!<)", "<br>");

    // Bao bọc nội dung trong thẻ đoạn văn nếu chưa có thẻ nào
    if (!text.startsWith("<")) {
        text = "<p>" + text + "</p>";
    }

    return text;
}
    
    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
} 