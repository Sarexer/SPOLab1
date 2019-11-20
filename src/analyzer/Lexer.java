package analyzer;

import analyzer.exceptions.LexerException;

import analyzer.tokens.Token;
import analyzer.tokens.TokenType;
import javafx.util.Pair;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import static analyzer.tokens.TokenType.EOF;

public class Lexer {
    private HashMap<String, Token> keywords = new HashMap<>();
    private Character currentChar;
    private LineNumberReader reader;
    private boolean isStringStart;
    private boolean wasStringLast;
    public ArrayList<ArrayList<ArrayList<String>>> tables = new ArrayList<>();


    public Lexer(Reader reader) {
        this.reader = new LineNumberReader(reader);
        init();
    }

    public ArrayList<Pair<Integer, Integer>> lex() throws LexerException {
        System.out.println("LEXING...");
        ArrayList<Token> tokens = new ArrayList<>();
        Token token = getNextToken();
        tokens.add(token);
        while (token.getType() != EOF) {
            token = getNextToken();
            tokens.add(token);
        }

        tokens = normalizeTokens(tokens);
        tokens = replaceIdToMethodId(tokens);

        initStaticTables();
        chainFillingDynamicTables(tokens);
        ArrayList<Pair<Integer, Integer>> pairs = descript(tokens);


        return pairs;
    }

    private ArrayList<Pair<Integer,Integer>> descript(ArrayList<Token> tokens){
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for (Token token : tokens) {
            TokenType type = token.getType();
            String tokenValue =  token.getValue()+"";
            if(type.equals(TokenType.EOF)){
                break;
            }

            for(int i =0;i<tables.size();i++){
                ArrayList<ArrayList<String>> table= tables.get(i);
                for(int j=0;j<table.size();j++){
                    ArrayList<String> row = table.get(j);
                    String value = row.get(0);

                    if(tokenValue != null && !tokenValue.equals("null")){
                        if(value.equals(tokenValue)){
                            pairs.add(new Pair<>(i,j));
                            break;
                        }
                    }else{
                        if(value.equals(type.getName())){
                            pairs.add(new Pair<>(i,j));
                            break;
                        }
                    }

                }
            }
        }
        return pairs;
    }
    /*private ArrayList<Pair<Integer, Integer>> descript(ArrayList<Token> tokens) {
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for (Token token : tokens) {
            TokenType type = token.getType();
            if (type.equals(EOF)) {
                break;
            }

            for (int i = 0; i < staticTables.size(); i++) {
                ArrayList<Object> table = tables.get(i);
                for (int j = 0; j < table.size(); j++) {
                    Object obj = table.get(j);
                    if (obj instanceof TokenType) {
                        TokenType tokenType = (TokenType) obj;
                        if (type.equals(tokenType)) {
                            pairs.add(new Pair<>(i, j));
                            break;
                        }
                    } else {
                        Object tokenValue = token.getValue();
                        if (tokenValue == null) {
                            break;
                        }
                        try {

                            if (obj.equals(token.getValue())) {
                                pairs.add(new Pair<>(i, j));
                                break;
                            }
                        } catch (Exception e) {

                        }
                    }

                }
            }
        }

        return pairs;
    }*/

    private void printPairs(ArrayList<Pair<Integer, Integer>> pairs) {
        for (Pair<Integer, Integer> pair : pairs) {
            System.out.print(String.format("(%d,%d)", pair.getKey(), pair.getValue()));
        }
    }

    private ArrayList<Token> normalizeTokens(ArrayList<Token> tokens) {
        ArrayList<Object> vars = new ArrayList<>();
        boolean isImport = false;
        boolean isClass = false;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.getType().equals(TokenType.IMPORT)) {
                isImport = true;
                continue;
            }
            if (token.getType().equals(TokenType.CLASS)) {
                isClass = true;
                continue;
            }

            if (isImport && token.getType().equals(TokenType.ID)) {
                tokens.set(i, new Token(TokenType.PACKAGEID, token.getValue(), token.getLineNumber()));
                continue;
            }
            if (isImport && token.getType().equals(TokenType.SEMI)) {
                isImport = false;
                continue;
            }

            if (isClass && token.getType().equals(TokenType.ID)) {
                tokens.set(i, new Token(TokenType.CLASSID, token.getValue(), token.getLineNumber()));
                continue;
            }
            if (isClass && token.getType().equals(TokenType.FBRACKET_L)) {
                isClass = false;
                continue;
            }

            if (token.getType().equals(TokenType.ID)) {
                Token nextToken = tokens.get(i + 1);
                if (nextToken.getType().equals(TokenType.ID)) {
                    vars.add(nextToken.getValue());
                    tokens.set(i, new Token(TokenType.CLASSID, token.getValue(), token.getLineNumber()));
                    continue;
                }
            }
            if (token.getType().equals(TokenType.ID)) {
                Token nextToken = tokens.get(i + 1);
                if (nextToken.getType().equals(TokenType.BRACKET_L)) {
                    nextToken = tokens.get(i + 2);
                    if (nextToken.getType().equals(TokenType.BRACKET_R)) {
                        tokens.set(i, new Token(TokenType.CLASSID, token.getValue(), token.getLineNumber()));
                        continue;
                    }
                }
            }
            if (token.getType().equals(TokenType.ID)) {
                Token nextToken = tokens.get(i + 1);
                if (nextToken.getType().equals(TokenType.DOT)) {
                    nextToken = tokens.get(i + 2);
                    if (nextToken.getType().equals(TokenType.ID)) {
                        if (!vars.contains(token.getValue())) {
                            tokens.set(i, new Token(TokenType.CLASSID, token.getValue(), token.getLineNumber()));
                        }
                        continue;
                    }
                }
            }

            if (token.getType().equals(TokenType.NEW)) {
                tokens.set(i + 1, new Token(TokenType.CLASSID, token.getValue(), token.getLineNumber()));
                continue;
            }
        }

        return tokens;
    }

    private ArrayList<Token> replaceIdToMethodId(ArrayList<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.getType().equals(TokenType.ID)) {
                Token nextToken = tokens.get(i + 1);
                if (nextToken.getType().equals(TokenType.PAREN_L)) {
                    tokens.set(i, new Token(TokenType.METHODID, token.getValue(), token.getLineNumber()));
                }
            }
        }
        return tokens;
    }

    private void initStaticTables() {
        ArrayList<Object> keywords = new ArrayList<>();
        keywords.add(TokenType.IMPORT);
        keywords.add(TokenType.PUBLIC);
        keywords.add(TokenType.PRIVATE);
        keywords.add(TokenType.CLASS);
        keywords.add(TokenType.TYPE_INT);
        keywords.add(TokenType.STATIC);
        keywords.add(TokenType.VOID);
        keywords.add(TokenType.NEW);
        keywords.add(TokenType.IF);
        keywords.add(TokenType.ELSE);
        /*keywords.add(TokenType.NO_OP);*/

        chainFillingStaticTables(keywords);

        ArrayList<Object> separators = new ArrayList<>();

        separators.add(TokenType.SEMI);
        separators.add(TokenType.DOT);
        separators.add(TokenType.COMA);
        separators.add(TokenType.PLUS);
        separators.add(TokenType.MINUS);
        separators.add(TokenType.DIV);
        separators.add(TokenType.MULT);
        separators.add(TokenType.COLON);
        separators.add(TokenType.ASSIGN);
        separators.add(TokenType.PAREN_L);
        separators.add(TokenType.PAREN_R);
        separators.add(TokenType.AND);
        separators.add(TokenType.FBRACKET_L);
        separators.add(TokenType.FBRACKET_R);
        separators.add(TokenType.OR);
        separators.add(TokenType.BRACKET_L);
        separators.add(TokenType.BRACKET_R);
        separators.add(TokenType.APOSTROPHE);
        separators.add(TokenType.EQUALS);

        chainFillingStaticTables(separators);
    }

    private void chainFillingStaticTables(ArrayList<Object> list) {
        ArrayList<ArrayList<String>> chainTable = new ArrayList<>();

        for (Object object : list) {
            TokenType type = (TokenType) object;
            addToChain(type.getName(), chainTable);
        }

        tables.add(chainTable);
    }

    private void chainFillingDynamicTables(ArrayList<Token> tokens) {
        ArrayList<ArrayList<String>> variables = new ArrayList<>();
        ArrayList<ArrayList<String>> constants = new ArrayList<>();
        ArrayList<ArrayList<String>> classes = new ArrayList<>();
        ArrayList<ArrayList<String>> functions = new ArrayList<>();
        ArrayList<ArrayList<String>> packages = new ArrayList<>();

        for (Token token : tokens) {
            TokenType type = token.getType();
            if (token.getValue() == null)
                continue;
            if (type.equals(TokenType.ID)) {
                String value = (String) token.getValue();
                addToChain(value, variables);
            } else if (type.equals(TokenType.CONST_INT) || type.equals(TokenType.CONST_STRING)) {
                String value = "";
                if (token.getValue() instanceof Integer) {
                    value = token.getValue() + "";
                } else {
                    value = token.getValue() + "";
                }
                addToChain(value, constants);
            } else if (type.equals(TokenType.CLASSID)) {
                String value = (String) token.getValue();
                addToChain(value, classes);
            } else if (type.equals(TokenType.METHODID)) {
                String value = (String) token.getValue();
                addToChain(value, functions);
            } else if (type.equals(TokenType.PACKAGEID)) {
                String value = (String) token.getValue();
                addToChain(value, packages);
            }

        }

        tables.add(variables);
        tables.add(constants);
        tables.add(classes);
        tables.add(functions);
        tables.add(packages);
    }

    private ArrayList<ArrayList<String>> addToChain(String value, ArrayList<ArrayList<String>> table) {
        if (table.size() == 0) {
            ArrayList<String> row = new ArrayList<>();
            row.add(value);
            table.add(row);

            return table;
        }
        for (int i = 0; i < table.size(); i++) {
            ArrayList<String> row = table.get(i);
            String str = row.get(0);

            if (value.equals(str)) {
                return table;
            }

            if (value.charAt(0) == str.charAt(0)) {
                if (row.size() == 2) {
                    int offset = Integer.valueOf(row.get(1));
                    i = offset - 1;
                } else {
                    ArrayList<String> newRow = new ArrayList<>();
                    newRow.add(value);
                    table.add(newRow);

                    row.add(Integer.toString(table.size() - 1));
                    return table;
                }
            }
        }
        ArrayList<String> newRow = new ArrayList<>();
        newRow.add(value);
        table.add(newRow);

        return table;
    }

    /*private void initDynamicTables(ArrayList<Token> tokens) {
        ArrayList<Object> variables = new ArrayList<>();
        ArrayList<Object> constants = new ArrayList<>();
        ArrayList<Object> classes = new ArrayList<>();
        ArrayList<Object> functions = new ArrayList<>();
        ArrayList<Object> packages = new ArrayList<>();


        for (Token token : tokens) {
            TokenType type = token.getType();
            if (type.equals(TokenType.ID)) {
                if (!variables.contains(token.getValue()))
                    variables.add(token.getValue());
            } else if (type.equals(TokenType.CONST_INT) || type.equals(TokenType.CONST_STRING)) {
                if (!constants.contains(token.getValue()))
                    constants.add(token.getValue());
            } else if (type.equals(TokenType.CLASSID)) {
                if (!classes.contains(token.getValue()))
                    classes.add(token.getValue());
            } else if (type.equals(TokenType.METHODID)) {
                if (!functions.contains(token.getValue()))
                    functions.add(token.getValue());
            } else if (type.equals(TokenType.PACKAGEID)) {
                if (!packages.contains(token.getValue()))
                    packages.add(token.getValue());
            }

        }

        staticTables.add(variables);
        staticTables.add(constants);
        staticTables.add(classes);
        staticTables.add(functions);
        staticTables.add(packages);
        System.out.println("");
    }*/


    private void init() {
        advance();

        keywords.put("import", new Token(TokenType.IMPORT));
        keywords.put("public", new Token(TokenType.PUBLIC));
        keywords.put("private", new Token(TokenType.PRIVATE));
        keywords.put("class", new Token(TokenType.CLASS));
        keywords.put("int", new Token(TokenType.TYPE_INT));
        keywords.put("static", new Token(TokenType.STATIC));
        keywords.put("void", new Token(TokenType.VOID));
        keywords.put("new", new Token(TokenType.NEW));
        keywords.put("if", new Token(TokenType.IF));
        keywords.put("else", new Token(TokenType.ELSE));

        keywords.put("nop", new Token(TokenType.NO_OP));
    }

    private void skipWhiteSpace() {
        while (currentChar != null && Character.isWhitespace(currentChar))
            advance();
    }

    private void advance() {
        try {
            int data = reader.read();
            currentChar = data == -1 ? null : (char) data;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Character peek() {
        try {
            reader.mark(5);
            int data = reader.read();
            Character c = data == -1 ? null : (char) data;
            reader.reset();
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getLineNumber() {
        return reader.getLineNumber() + 1;
    }


    private Token getNumber() {
        StringBuilder builder = new StringBuilder();
        while (currentChar != null && Character.isDigit(currentChar)) {
            builder.append(currentChar);
            advance();
        }

        return new Token(TokenType.CONST_INT, Integer.valueOf(builder.toString()));
    }

    private Token getId() {
        StringBuilder builder = new StringBuilder();

        while (currentChar != null && Character.isLetterOrDigit(currentChar)) {
            builder.append(currentChar);
            advance();
        }

        String value = builder.toString();
        if (keywords.containsKey(value.toLowerCase())) {
            Token t = keywords.get(value.toLowerCase());
            return new Token(t.getType(), t.getValue(), getLineNumber());
        }

        return new Token(TokenType.ID, value, getLineNumber());
    }

    private Token getString() {
        StringBuilder builder = new StringBuilder();
        while (currentChar != null && currentChar != '\"') {
            builder.append(currentChar);
            advance();
        }

        return new Token(TokenType.CONST_STRING, builder.toString(), getLineNumber());
    }

    private Token getNextToken() throws LexerException {
        while (currentChar != null) {

            if (wasStringLast) {
                wasStringLast = false;
                if (currentChar == '\"')
                    advance();

                return new Token(TokenType.APOSTROPHE, getLineNumber());
            }

            if (isStringStart) {
                wasStringLast = true;
                isStringStart = false;
                return getString();
            }

            if (Character.isWhitespace(currentChar)) {
                skipWhiteSpace();
                continue;
            }


            if (currentChar == '\"') {
                advance();
                isStringStart = !isStringStart;
                return new Token(TokenType.APOSTROPHE, getLineNumber());
            }

            if (Character.isDigit(currentChar))
                return getNumber();

            if (Character.isLetterOrDigit(currentChar))
                return getId();

            if (currentChar == '&' && peek() == '&') {
                advance();
                advance();
                return new Token(TokenType.AND, getLineNumber());
            }

            if (currentChar == '|' && peek() == '|') {
                advance();
                advance();
                return new Token(TokenType.OR, getLineNumber());
            }

            if (currentChar == '=' && peek() != null && peek() == '=') {
                advance();
                advance();
                return new Token(TokenType.EQUALS, getLineNumber());
            }

            if (currentChar == '=') {
                advance();
                return new Token(TokenType.ASSIGN, getLineNumber());
            }

            if (currentChar == '.') {
                advance();
                return new Token(TokenType.DOT, getLineNumber());
            }

            if (currentChar == ',') {
                advance();
                return new Token(TokenType.COMA, getLineNumber());
            }

            if (currentChar == ';') {
                advance();
                return new Token(TokenType.SEMI, getLineNumber());
            }

            if (currentChar == ':') {
                advance();
                return new Token(TokenType.COLON, getLineNumber());
            }

            if (currentChar == '+') {
                advance();
                return new Token(TokenType.PLUS, getLineNumber());
            }

            if (currentChar == '-') {
                advance();
                return new Token(TokenType.MINUS, getLineNumber());
            }

            if (currentChar == '*') {
                advance();
                return new Token(TokenType.MULT, getLineNumber());
            }

            if (currentChar == '/') {
                advance();
                return new Token(TokenType.DIV, getLineNumber());
            }

            if (currentChar == '(') {
                advance();
                return new Token(TokenType.PAREN_L, getLineNumber());
            }

            if (currentChar == ')') {
                advance();
                return new Token(TokenType.PAREN_R, getLineNumber());
            }

            if (currentChar == '[') {
                advance();
                return new Token(TokenType.BRACKET_L, getLineNumber());
            }

            if (currentChar == ']') {
                advance();
                return new Token(TokenType.BRACKET_R, getLineNumber());
            }

            if (currentChar == '{') {
                advance();
                return new Token(TokenType.FBRACKET_L, getLineNumber());
            }

            if (currentChar == '}') {
                advance();
                return new Token(TokenType.FBRACKET_R, getLineNumber());
            }


            if (currentChar == '>') {
                advance();
                return new Token(TokenType.GREATER_THAN, getLineNumber());
            }

            if (currentChar == '<') {
                advance();
                return new Token(TokenType.LESS_THAN, getLineNumber());
            }

            throw new LexerException("Unrecognized character " + currentChar, getLineNumber());
        }

        return new Token(TokenType.EOF, getLineNumber());
    }


}
