package project1.handlers;

import project1.enums.Token;
import project1.enums.TokenType;

public class TokenizerNoComments extends Tokenizer {

    public TokenizerNoComments(String buffer) {
        super(buffer);
    }

    @Override
    public Token nextToken() {
        Token token = super.nextToken();
        while (token.getTokenType().equals(TokenType.COMMENT))
            token = super.nextToken();
        return token;
    }
}
