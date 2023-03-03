import minipython.node.*;
import java.util.*;

public class ArithmeticData
{
    private List<Token> tokens;
    private List<PExpression> expressions;

    ArithmeticData(List<Token> tokens, List<PExpression> expressions)
    {
        this.tokens = tokens;
        this.expressions = expressions;
    }

    ArithmeticData()
    {
        tokens =  new ArrayList<Token>();
        expressions =  new ArrayList<PExpression>();
    }

    public List<Token> getTokens()
    {
        return tokens;
    }

    
    public List<PExpression> getExpressions()
    {
        return expressions;
    }

    public void setTokens(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    
    public void setExpressions(List<PExpression> expressions)
    {
        this.expressions = expressions;
    }

    public void addTokens(List<Token> tokens)
    {
        this.tokens.addAll(tokens);
    }

    public void addExpressions(List<PExpression> expressions)
    {
        this.expressions.addAll(expressions);
    }

    public void addAll(ArithmeticData data)
    {
        this.tokens.addAll(data.getTokens());
        this.expressions.addAll(data.getExpressions());
    }
}