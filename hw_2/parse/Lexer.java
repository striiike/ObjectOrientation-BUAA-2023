package parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

public class Lexer {

    private int pos = 0;
    private final String input;
    private String curToken;

    public Lexer(String input) {
        this.input = prePro(input);
        this.next();
    }

    // kill duplicate '+' and '-'
    // kill the '+' inside "**+"
    public String prePro(String input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            if (input.charAt(i) == '+' || input.charAt(i) == '-') {
                int curSym = (input.charAt(i) == '-') ? -1 : 1;
                while (input.charAt(i + 1) == '+' || input.charAt(i + 1) == '-') {
                    curSym *= (input.charAt(i + 1) == '-') ? -1 : 1;
                    i++;
                }
                sb.append((curSym == 1) ? "+" : "-");
            } else if (input.charAt(i) == '*'
                    && input.charAt(i + 1) == '*'
                    && input.charAt(i + 2) == '+') {
                sb.append(input.charAt(i));
                sb.append(input.charAt(i + 1));
                i += 2;
            } else {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }

        Pattern sinPattern = Pattern.compile("sin[(]");
        Matcher sinMatcher = sinPattern.matcher(input.substring(pos, min(pos + 4, input.length())));
        Pattern cosPattern = Pattern.compile("cos[(]");
        Matcher cosMatcher = cosPattern.matcher(input.substring(pos, min(pos + 4, input.length())));

        char c = input.charAt(pos);

        if (c == 'd') {
            pos++;
            curToken = c + String.valueOf(input.charAt(pos)) + "(";
            pos++;
        } else if (c == 'f' || c == 'g' || c == 'h') {
            curToken = c + "(";
            pos++;
        } else if (sinMatcher.find()) {
            curToken = "sin(";
            pos += 3;
        } else if (cosMatcher.find()) {
            curToken = "cos(";
            pos += 3;
        } else if (c == '*') {
            if (input.charAt(pos + 1) == '*') {
                pos++;
                curToken = "**";
            } else {
                curToken = "*";
            }
        } else if (Character.isDigit(c)) {
            curToken = getNumber();
        } else {
            curToken = String.valueOf(c);
        }
        pos++;

    }

    // without '+-'
    public String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }
        pos--;
        return String.valueOf(sb);

    }

    public String peek() {
        return this.curToken;
    }

}
