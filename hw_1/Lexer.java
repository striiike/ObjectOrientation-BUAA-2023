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

        char c = input.charAt(pos);
        if (c == '*') {
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
