package analyzer.tokens;

public enum TokenType {
    /**
     * ОПЕРАЦИИ
     */
    PLUS("PLUS"),
    MINUS("MINUS"),
    MULT("MULT"),
    DIV("DIV"),
    /**
     * КРУГЛЫЕ СКОБКИ
     */
    PAREN_L("L_PAREN"),
    PAREN_R("R_PAREN"),
    /**
     * КВАДРАТНЫЕ И ФИГУРНЫЕ
     */
    BRACKET_L("L_BRACKET"),
    BRACKET_R("R_BRACKET"),
    FBRACKET_L("L_FBRACKET"),
    FBRACKET_R("R_FBRACKET"),
    /**
     * КОНСТАНТЫ
     */
    CONST_INT("INTEGER_CONST"),
    CONST_BOOL("BOOL_CONST"),
    CONST_STRING("STRING_CONST"),
    /**
     * TYPES
     */
    TYPE_INT("INTEGER"),
    TYPE_BOOL("BOOL"),
    TYPE_STRING("STRING"),

    /**
     * COMMON
     */
    CLASSID("CLASSID"),
    METHODID("METHODID"),
    PACKAGEID("PACKAGEID"),
    IMPORT("IMPORT"),
    EOF("EOF"),
    ID("ID"),
    DOT("DOT"),
    ASSIGN("ASSIGN"),
    SEMI("SEMI"),
    VAR_DEF("VAR"),
    CONST_DEF("CONST"),
    COLON(":"),
    COMA(","),
    PROCEDURE("PROCEDURE"),
    IF("IF"),
    ELSE("ELSE"),
    THEN("THEN"),
    EQUALS("EQUALS"),
    LESS_THAN("LT"),
    GREATER_THAN("GT"),
    FUNCTION("FUNC"),
    APOSTROPHE("APOSTROPHE"),
    REPEAT("REPEAT"),
    UNTIL("UNTIL"),
    FOR("FOR"),
    TO("TO"),
    DO("DO"),
    WHILE("WHILE"),
    ARRAY("ARRAY"),
    OF("OF"),
    NO_OP("NO OP"),
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE"),
    CLASS("CLASS"),
    STATIC("STATIC"),
    VOID("VOID"),
    NEW("NEW"),
    AND("AND"),
    OR("OR");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
