import java.io.*;
import minipython.lexer.Lexer;
import minipython.parser.Parser;
import minipython.node.*;
import java.util.*;

public class ParserTest
{
  public static void main(String[] args)
  {
    try
    {
      Parser parser =
        new Parser(
        new Lexer(
        new PushbackReader(
        new FileReader(args[0].toString()), 1024)));

     Hashtable symtable =  new Hashtable();
     List<AFunction> functions = new ArrayList<AFunction>();

     Start ast = parser.parse();
     ast.apply(new myvisitor(symtable, functions));
     ast.apply(new mysecondvisitor(symtable, functions));

     /*       -- debugging --        */
     //System.out.println(symtable);
     //System.out.println(functions);  
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  }
}

