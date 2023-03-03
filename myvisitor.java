import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor extends DepthFirstAdapter 
{
	private Hashtable symtable;	
	private List<AFunction> functions;

	myvisitor(Hashtable symtable, List<AFunction> functions) 
	{
		this.symtable = symtable;
		this.functions = functions;
	}

	public void inAFunction(AFunction node) 
	{
		TId id = ((TId)((AIdExpression) node.getExpression()).getId());
		String fName = id.toString();
		int line = id.getLine();
		
		int [] minMax = getMinMax(node);
		int minArgs = minMax[0];
		int maxArgs = minMax[1];
		boolean exists = false;
		boolean error = false;
		
		for(AFunction fun:functions)
		{
			fun = (AFunction) fun.clone();
			if(((AIdExpression)fun.getExpression()).getId().toString().equals(fName))
			{
				if(!checkArgSize(line, minArgs, fun))
				{
					System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
					error = true;
				}
				else if(!checkArgSize(line, maxArgs, fun))
				{
					System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
					error = true;
				}
				exists = true;
			}
		}

		if(!exists || !error && exists)
		{	
			functions.add(node);	
		}
	}

	public void inAArgument(AArgument node)
    {
		AArgument copyArg = (AArgument)node.clone();
        TId id = ((TId)((AIdExpression) copyArg.getExpression()).getId());
        String fName = id.toString();
        
        if(copyArg.getEqualsValue().size()!=0)
        {
            AEqualsValue val = (AEqualsValue)copyArg.getEqualsValue().get(0);
            AEqualsStatement moreId = new AEqualsStatement(new AIdExpression(id), ((PExpression)val.getExpression()));
            symtable.put(fName, moreId);
        }
        else
        {
            AEqualsStatement moreId = new AEqualsStatement(new AIdExpression(id), new ANoneExpression(new TNone(id.getLine(), id.getPos())));
			symtable.put(fName, moreId);
        }
    }
    
    public void inAMoreIdentifiers(AMoreIdentifiers node) 
    {
		AMoreIdentifiers copyMoreIds = (AMoreIdentifiers)node.clone();
        TId id = ((TId)((AIdExpression) copyMoreIds.getExpression()).getId());
        String fName = id.toString();
    
        if(copyMoreIds.getEqualsValue().size()!=0)
        {
            AEqualsValue val = (AEqualsValue)copyMoreIds.getEqualsValue().get(0);
            AEqualsStatement moreId = new AEqualsStatement(copyMoreIds.getExpression(), ((PExpression)val.getExpression()));
            symtable.put(fName, moreId);
        }
        else
        {
            AEqualsStatement moreId = new AEqualsStatement(copyMoreIds.getExpression(), new ANoneExpression(new TNone(id.getLine(), id.getPos())));
            symtable.put(fName, moreId);
        }
    }

	public boolean checkArgSize(int line, int numOfArgs, AFunction node)
	{

		int [] minMax = getMinMax(node);
		int minArgs = minMax[0];
		int maxArgs = minMax[1];
		
		if(numOfArgs == minArgs || numOfArgs == maxArgs)
		{
			System.out.println("Line " + line + ":  Found " + numOfArgs + " arguments, must be different than {" + minArgs + ", " + maxArgs + "}");
			return false;
		}

		return true;
	}

	public int[] getMinMax(AFunction node)
	{
		int[] minMax = new int[2];
		boolean isDefault;
		AArgument arg;
		TypedLinkedList arguments;
		int minArgs = 0;
		int maxArgs = 0;

		arguments = ((TypedLinkedList)node.getArgument());
		
		if(arguments.size() != 0)
		{
			maxArgs++;
			arg = ((AArgument)arguments.get(0));
			TypedLinkedList moreIdentifiers = ((TypedLinkedList)arg.getMoreIdentifiers());
			isDefault = arg.getEqualsValue().size() == 0;

			if(!isDefault) //if it is a default value, it can be skipped during the function call.
			{
				minArgs++;
			}

			for(int i=0; i<moreIdentifiers.size(); i++)
			{
				maxArgs++;
				isDefault = ((AMoreIdentifiers)moreIdentifiers.get(i)).getEqualsValue().size() == 0;
				if(!isDefault)
				{
					minArgs++;
				}
			}
		}

		minArgs = maxArgs - minArgs;
		minMax[0] = minArgs;
		minMax[1] = maxArgs;

		return minMax;
	}
}
