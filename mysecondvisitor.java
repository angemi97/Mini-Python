import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class mysecondvisitor extends DepthFirstAdapter 
{
	private Hashtable symtable;	
	private List<AFunction> functions;

	mysecondvisitor(Hashtable symtable, List<AFunction> functions) 
	{
		this.symtable = symtable;
		this.functions = functions;
	}

	public void inAEqualsStatement(AEqualsStatement node) 
	{
		TId lId = ((TId)((AIdExpression) node.getL()).getId());	
		String fName = lId.toString();

		if(node.getR() instanceof AIdExpression)
		{
			if(symtable.containsKey(((TId)((AIdExpression) node.getR()).getId()).toString()))
			{
				if(symtable.get(((TId)((AIdExpression) node.getR()).getId()).toString()) instanceof AEqualsStatement)
				{
					AEqualsStatement old = (AEqualsStatement)symtable.get(((TId)((AIdExpression) node.getR()).getId()).toString());
					PExpression equals = old.getR();
					AEqualsStatement update = new AEqualsStatement((AIdExpression) node.getL(), equals);
					symtable.put(fName, update);
				}
			}
		}
		else if(node.getR() instanceof ATypeExpression)
		{
			TId id = ((AIdExpression)((ATypeExpression)node.getR()).getExpression()).getId();
			String typeName = id.toString();
	
			if(symtable.containsKey(typeName))
			{
				symtable.put(fName, node.getR());
			}
		}
		else
		{
			symtable.put(fName, node);
		}
	}

	public void inAArrayAssignStatement(AArrayAssignStatement node)
	{
		TId id = ((AIdExpression)node.getL()).getId();
		String arrName = id.toString();

		symtable.put(arrName, node);
	}

	public void inAIdExpression(AIdExpression node)
	{
		if(!(node.parent() instanceof AFunction))
		{
			TId id = node.getId();
			String fName = id.toString();
			int line = id.getLine();
			if(node.parent() instanceof AFunctionCall)
			{
				boolean exists = false;
				for(AFunction fun:functions)
				{
					fun = (AFunction) fun.clone();
					if(fName.equals(((AIdExpression)fun.getExpression()).getId().toString()))
					{
						exists = true;
					}
				}
	
				if(!exists)
				{
					System.out.println("Line " + line + ": " +" Function " + fName +" is not defined");
				}
			}
			else
			{
				if(!symtable.containsKey(fName))
				{
					System.out.println("Line " + line + ": " +" Variable " + fName +" is not defined");
				}
			}
		}
	}

	public void inADivEqualsStatement(ADivEqualsStatement node)
	{
		TId lId = ((TId)((AIdExpression) node.getL()).getId());	
		
		if(symtable.containsKey(lId.toString()))
		{
			String fName = lId.toString();
			AEqualsStatement old = ((AEqualsStatement)symtable.get(lId.toString()));
			PExpression oldExp = old.getR();

			ADivExpression divExp = new ADivExpression(new AParexprparExpression(old.getR()), node.getR());
			ArithmeticData data = checkExpression(divExp);
			
			if(!checkArithmetics(data))
			{
				AEqualsStatement update = new AEqualsStatement(node.getL(), divExp);
				symtable.put(fName, update);
			}
			else
			{
				AEqualsStatement update = new AEqualsStatement(node.getL(), oldExp);
				symtable.put(fName, update);
			}
		}
	}

	public void inAMinusEqualsStatement(AMinusEqualsStatement node)
	{
		TId lId = ((TId)((AIdExpression) node.getL()).getId());

		if(symtable.containsKey(lId.toString()))
		{
			String fName = lId.toString();
			AEqualsStatement old = ((AEqualsStatement)symtable.get(lId.toString()));
			PExpression oldExp = old.getR();

			ASubtractionExpression divExp = new ASubtractionExpression(new AParexprparExpression(old.getR()), node.getR());
			ArithmeticData data = checkExpression(divExp);
			
			if(!checkArithmetics(data))
			{
				AEqualsStatement update = new AEqualsStatement(node.getL(), divExp);
				symtable.put(fName, update);
			}
			else
			{
				AEqualsStatement update = new AEqualsStatement(node.getL(), oldExp);
				symtable.put(fName, update);
			}
		}
	}

	public void inAPlusplusExpression(APlusplusExpression node)
	{
		if(node.getExpression() instanceof AIdExpression)
		{
			TId id = ((TId)((AIdExpression) node.getExpression()).getId());
			if(symtable.containsKey(id.toString()))
			{
				String fName = id.toString();
				AEqualsStatement old = ((AEqualsStatement)symtable.get(id.toString()));
				PExpression oldExp = (PExpression)old.getR().clone();
	
				AAdditionExpression plplExp = new AAdditionExpression(new AParexprparExpression((PExpression)old.getR().clone()), new ANumberExpression(new TNumber("1", id.getLine(), id.getPos())));

				ArithmeticData data = checkExpression(plplExp);
				if(!checkArithmetics(data))
				{
					AEqualsStatement update = new AEqualsStatement(node.getExpression(), plplExp);
					symtable.put(fName, update);
				}
				else
				{
					AEqualsStatement update = new AEqualsStatement(node.getExpression(), oldExp);
					symtable.put(fName, update);
				}
			}
		}
		else
		{
			AAdditionExpression plplExp = new AAdditionExpression(new AParexprparExpression((PExpression)node.getExpression().clone()), new ANumberExpression(new TNumber("1")));
			checkArithmetics(checkExpression(plplExp));
		}
	}

	public void inAMinusminusExpression(AMinusminusExpression node)
	{
		if(node.getExpression() instanceof AIdExpression)
		{
			TId id = ((TId)((AIdExpression) node.getExpression()).getId());
			if(symtable.containsKey(id.toString()))
			{
				String fName = id.toString();
				AEqualsStatement old = ((AEqualsStatement)symtable.get(id.toString()));
				PExpression oldExp = (PExpression)old.getR().clone();
	
				ASubtractionExpression plplExp = new ASubtractionExpression(new AParexprparExpression((PExpression)old.getR().clone()), new ANumberExpression(new TNumber("1", id.getLine(), id.getPos())));

				ArithmeticData data = checkExpression(plplExp);
				if(!checkArithmetics(data))
				{
					AEqualsStatement update = new AEqualsStatement(node.getExpression(), plplExp);
					symtable.put(fName, update);
				}
				else
				{
					AEqualsStatement update = new AEqualsStatement(node.getExpression(), oldExp);
					symtable.put(fName, update);
				}
			}
		}
		else
		{
			ASubtractionExpression plplExp = new ASubtractionExpression(new AParexprparExpression((PExpression)node.getExpression().clone()), new ANumberExpression(new TNumber("1")));
			checkArithmetics(checkExpression(plplExp));
		}
	}



	public void inAAdditionExpression(AAdditionExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inASubtractionExpression(ASubtractionExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inAMultiplicationExpression(AMultiplicationExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inADivExpression(ADivExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inAModExpression(AModExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inAExponentialExpression(AExponentialExpression node)
	{
		ArithmeticData data = checkParent(node);
		if(data.getTokens().size() > 0) checkArithmetics(data);
	}

	public void inAFunStatement(AFunStatement node)
	{
		AFunctionCall funCall = (AFunctionCall)node.getFunctionCall();
		funCall = (AFunctionCall)funCall.clone();
		TId id = ((TId)((AIdExpression) funCall.getExpression()).getId());	
        String fName = id.toString();
        int line = id.getLine();
		boolean found = false;
		boolean foundCorrect = false;
		int numOfArgs = 0;
		
		for(AFunction fun: functions)
		{
			fun = (AFunction)fun.clone();
			String funName = ((AIdExpression)fun.getExpression()).getId().toString();
			if(fName.equals(funName))
			{
				numOfArgs = 0; 
				found = true;

				if(funCall.getArglist().size() != 0)
				{
					numOfArgs++;
					if(((AArglist)funCall.getArglist().get(0)).getR().size() != 0) numOfArgs = numOfArgs +  ((AArglist)funCall.getArglist().get(0)).getR().size();
				}
				
				if(checkArgSize(line, numOfArgs, fun, true))
				{
					foundCorrect = true;
					symtable.put(fName, runFunction(fun, funCall));
				}
			}
		}

		if(found)
		{
			if(!foundCorrect)
			{
				for(AFunction fun: functions)
				{
					fun = (AFunction)fun.clone();
					String funName = ((AIdExpression)fun.getExpression()).getId().toString();
					if(fName.equals(funName))
					{
						checkArgSize(line, numOfArgs, fun, false);
					}
				}
			}
		}
	}

	public void inAFunExpression(AFunExpression node)
	{
		if(!(node.parent().clone() instanceof AArglist))
		{
			AFunctionCall funCall = (AFunctionCall)node.getFunctionCall();
			funCall = (AFunctionCall)funCall.clone();
			TId id = ((TId)((AIdExpression) funCall.getExpression()).getId());	
			String fName = id.toString();
			int line = id.getLine();
			boolean found = false;
			boolean foundCorrect = false;
			int numOfArgs = 0;
			
			for(AFunction fun: functions)
			{
				fun = (AFunction)fun.clone();
				String funName = ((AIdExpression)fun.getExpression()).getId().toString();
				if(fName.equals(funName))
				{
					numOfArgs = 0; 
					found = true;
	
					if(funCall.getArglist().size() != 0)
					{
						numOfArgs++;
						if(((AArglist)funCall.getArglist().get(0)).getR().size() != 0) numOfArgs = numOfArgs +  ((AArglist)funCall.getArglist().get(0)).getR().size();
					}
					
					if(checkArgSize(line, numOfArgs, fun, true))
					{
						foundCorrect = true;
						symtable.put(fName, runFunction(fun, funCall));
					}
				}
			}
	
			if(found)
			{
				if(!foundCorrect)
				{
					for(AFunction fun: functions)
					{
						fun = (AFunction)fun.clone();
						String funName = ((AIdExpression)fun.getExpression()).getId().toString();
						if(fName.equals(funName))
						{
							checkArgSize(line, numOfArgs, fun, false);
						}
					}
				}
			}
		}
	}

	public void inAFuncExpression(AFuncExpression node)
	{
		if(!(node.parent().clone() instanceof AArglist))
		{
			AFunctionCall funCall = (AFunctionCall)node.getFunctionCall();
			funCall = (AFunctionCall)funCall.clone();
			TId id = ((TId)((AIdExpression) funCall.getExpression()).getId());	
			String fName = id.toString();
			int line = id.getLine();
			boolean found = false;
			boolean foundCorrect = false;
			int numOfArgs = 0;
			
			for(AFunction fun: functions)
			{
				fun = (AFunction)fun.clone();
				String funName = ((AIdExpression)fun.getExpression()).getId().toString();
				if(fName.equals(funName))
				{
					numOfArgs = 0; 
					found = true;
	
					if(funCall.getArglist().size() != 0)
					{
						numOfArgs++;
						if(((AArglist)funCall.getArglist().get(0)).getR().size() != 0) numOfArgs = numOfArgs +  ((AArglist)funCall.getArglist().get(0)).getR().size();
					}
					
					if(checkArgSize(line, numOfArgs, fun, true))
					{
						foundCorrect = true;
						symtable.put(fName, runFunction(fun, funCall));
					}
				}
			}
	
			if(found)
			{
				if(!foundCorrect)
				{
					for(AFunction fun: functions)
					{
						fun = (AFunction)fun.clone();
						String funName = ((AIdExpression)fun.getExpression()).getId().toString();
						if(fName.equals(funName))
						{
							checkArgSize(line, numOfArgs, fun, false);
						}
					}
				}
			}
		}
	}

	public ArithmeticData checkParent(PExpression node)
	{
		ArithmeticData arithmeticData = new ArithmeticData();
		
		if((node.parent() instanceof PStatement) && (!(node.parent() instanceof AReturnStatement)))
		{
			if(!(node.parent().parent() instanceof AFunction))
			{
				arithmeticData = checkExpression(node);
				arithmeticData.setTokens(arithmeticData.getTokens());
				arithmeticData.setExpressions(arithmeticData.getExpressions());
			}
		}

		if(node.parent() instanceof AArglist)
		{
			arithmeticData = checkExpression(node);
			arithmeticData.setTokens(arithmeticData.getTokens());
			arithmeticData.setExpressions(arithmeticData.getExpressions());
		}

		if(node.parent() instanceof PComparison)
		{
			arithmeticData = checkExpression(node);
			arithmeticData.setTokens(arithmeticData.getTokens());
			arithmeticData.setExpressions(arithmeticData.getExpressions());
		}

		return arithmeticData;
	}

	public ArithmeticData checkExpression(PExpression node)
	{
		List<Token> tokens =  new ArrayList<Token>();
		List<PExpression> expressions =  new ArrayList<PExpression>();
		ArithmeticData arithmeticData = new ArithmeticData(); 
		/**
		 * ----------- ALL TOKENS HERE -----------
		 */

		if(node instanceof AIdExpression)
		{
			TId id = ((TId)((AIdExpression) node).getId());
			if(!tokens.contains(id))
			{
				tokens.add(id);
				arithmeticData.addTokens(tokens);
			}	
		}

		if(node instanceof AParexprparExpression)
		{
			arithmeticData.addAll(checkExpression(((AParexprparExpression)node).getExpression()));
		}

		if(node instanceof AFunExpression || node instanceof AFuncExpression)
		{
			AFunctionCall funCall;

			if(node instanceof AFunExpression) funCall = (AFunctionCall)((AFunExpression)node).getFunctionCall();
			else funCall = (AFunctionCall)((AFuncExpression)node).getFunctionCall();

			funCall = (AFunctionCall)funCall.clone();
			TId id = ((TId)((AIdExpression) funCall.getExpression()).getId());	
			String fName = id.toString();
			int line = id.getLine();
			boolean found = false;
			boolean foundCorrect = false;
			int numOfArgs = 0;
			
			for(AFunction fun: functions)
			{
				fun = (AFunction)fun.clone();
				String funName = ((AIdExpression)fun.getExpression()).getId().toString();
				if(fName.equals(funName))
				{
					numOfArgs = 0; 
					found = true;
	
					if(funCall.getArglist().size() != 0)
					{
						numOfArgs++;
						if(((AArglist)funCall.getArglist().get(0)).getR().size() != 0) numOfArgs = numOfArgs +  ((AArglist)funCall.getArglist().get(0)).getR().size();
					}
					
					if(checkArgSize(line, numOfArgs, fun, true))
					{
						foundCorrect = true;
						PStatement ret = runFunction(fun, funCall);

						ret = (PStatement) ret.clone();
						if(ret instanceof AReturnStatement)
						{
							PExpression retExp = (PExpression)((AReturnStatement)ret).getExpression().clone();
							arithmeticData.addAll(checkExpression(retExp));
						}
						else
						{
							arithmeticData.addAll(checkExpression(new ANoneExpression(new TNone(id.getLine(), id.getPos()))));
						}
					}
				}
			}
	
			if(found)
			{
				if(!foundCorrect)
				{
					for(AFunction fun: functions)
					{
						fun = (AFunction)fun.clone();
						String funName = ((AIdExpression)fun.getExpression()).getId().toString();
						if(fName.equals(funName))
						{
							checkArgSize(line, numOfArgs, fun, false);
						}
					}
				}
			}
		}

		if(node instanceof ANoneExpression)
		{
			TNone id = ((TNone)((ANoneExpression) node).getNone());
			if(!tokens.contains(id))
			{
				tokens.add(id);
				arithmeticData.addTokens(tokens);
				expressions.add(node);
				arithmeticData.addExpressions(expressions);
			}	
		}

		if(node instanceof AStringExpression)
		{
			TString id = ((TString)((AStringExpression) node).getString());
			if(!tokens.contains(id))
			{
				tokens.add(id);
				arithmeticData.addTokens(tokens);
			}	
		}

		if(node instanceof ANumberExpression)
		{
			TNumber id = ((TNumber)((ANumberExpression) node).getNumber());
			if(!tokens.contains(id))
			{
				tokens.add(id);
				arithmeticData.addTokens(tokens);
			}	
		}

		if(node instanceof ATypeExpression)
		{
			TId id = ((TId)((AIdExpression)((ATypeExpression) node).getExpression()).getId());
			if(!tokens.contains(id))
			{
				expressions.add(node);
				arithmeticData.addExpressions(expressions);
			}	
		}

		/**
		 * ----------- ALL ARITHMETICS HERE -----------
		 */

		if(node instanceof AAdditionExpression)
		{
			((AAdditionExpression)node).setL(checkElement(((AAdditionExpression)node).getL()));
			((AAdditionExpression)node).setR(checkElement(((AAdditionExpression)node).getR()));
			PExpression lExp = (PExpression)((AAdditionExpression)node).getL();
			PExpression rExp = (PExpression)((AAdditionExpression)node).getR();

			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);
			
			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}


		if(node instanceof ASubtractionExpression)
		{
			((ASubtractionExpression)node).setL(checkElement(((ASubtractionExpression)node).getL()));
			((ASubtractionExpression)node).setR(checkElement(((ASubtractionExpression)node).getR()));
			PExpression lExp = (PExpression)((ASubtractionExpression)node).getL();
			PExpression rExp = (PExpression)((ASubtractionExpression)node).getR();
			
			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);
			
			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}

		if(node instanceof AMultiplicationExpression)
		{
			((AMultiplicationExpression)node).setL(checkElement(((AMultiplicationExpression)node).getL()));
			((AMultiplicationExpression)node).setR(checkElement(((AMultiplicationExpression)node).getR()));
			PExpression lExp = (PExpression)((AMultiplicationExpression)node).getL();
			PExpression rExp = (PExpression)((AMultiplicationExpression)node).getR();
			
			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);

			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}

		if(node instanceof ADivExpression)
		{
			((ADivExpression)node).setL(checkElement(((ADivExpression)node).getL()));
			((ADivExpression)node).setR(checkElement(((ADivExpression)node).getR()));
			PExpression lExp = (PExpression)((ADivExpression)node).getL();
			PExpression rExp = (PExpression)((ADivExpression)node).getR();
			
			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);

			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}

		if(node instanceof AModExpression)
		{
			((AModExpression)node).setL(checkElement(((AModExpression)node).getL()));
			((AModExpression)node).setR(checkElement(((AModExpression)node).getR()));
			PExpression lExp = (PExpression)((AModExpression)node).getL();
			PExpression rExp = (PExpression)((AModExpression)node).getR();
			
			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);

			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}

		if(node instanceof AExponentialExpression)
		{
			((AExponentialExpression)node).setL(checkElement(((AExponentialExpression)node).getL()));
			((AExponentialExpression)node).setR(checkElement(((AExponentialExpression)node).getR()));
			PExpression lExp = (PExpression)((AExponentialExpression)node).getL();
			PExpression rExp = (PExpression)((AExponentialExpression)node).getR();
			
			ArithmeticData lData = checkExpression(lExp);
			ArithmeticData rData = checkExpression(rExp);

			tokens.addAll(lData.getTokens());
			tokens.addAll(rData.getTokens());

			expressions.addAll(lData.getExpressions());
			expressions.addAll(rData.getExpressions());
			expressions.add(node);

			arithmeticData.addTokens(tokens); 
			arithmeticData.addExpressions(expressions);
		}
		
		return arithmeticData;
	}

	public boolean checkArithmetics(ArithmeticData data)
	{
		boolean additionOnly = true;
		boolean multOnly = true;
		int line = 0;
		
		for(PExpression expression:data.getExpressions())
		{
			if(expression instanceof ANoneExpression) 
			{
				line = ((TNone)((ANoneExpression)expression).getNone()).getLine();
				System.out.println("Line " + line + ":  None expression in arithmetics");
			}
			
			else if(expression instanceof ATypeExpression) 
			{
				line = ((TId)((AIdExpression)((ATypeExpression)expression).getExpression()).getId()).getLine();
				System.out.println("Line " + line + ":  Type expression in arithmetics");
			}

			else if(expression instanceof ASubtractionExpression)
			{
				additionOnly = false;
				multOnly = false;
			}
			else if(expression instanceof AMultiplicationExpression)
			{
				additionOnly = false;
			}
			else if(expression instanceof ADivExpression)
			{
				additionOnly = false;
				multOnly = false;
			}
			else if(expression instanceof AModExpression)
			{
				additionOnly = false;
				multOnly = false;
			}
			else if(expression instanceof AExponentialExpression)
			{
				additionOnly = false;
				multOnly = false;
			}
			else if(expression instanceof AAdditionExpression)
			{
				multOnly = false;
			}
		}
		
		return checkTokens(data.getTokens(), additionOnly, multOnly);
	}

	public boolean checkTokens(List<Token> tokenIds, boolean addOnly, boolean multOnly)
	{
		boolean foundString = false;
		boolean foundNumber = false;
		boolean foundDouble = false;
		boolean foundError = false;
		int line = 0;
		
		for(Token token:tokenIds)
		{
			if(token instanceof TString)
			{
				foundString = true;
				line = token.getLine();
			}
			if(token instanceof TNumber)
			{
				foundNumber = true;
				if(token.getText().contains(".")) foundDouble = true;
				line = token.getLine();
			}
			while(token instanceof TId)
			{
				if(symtable.containsKey(token.toString()))
				{
					if(symtable.get(token.toString()) instanceof AEqualsStatement)
					{
						ArithmeticData d = checkExpression(((AEqualsStatement)symtable.get(token.toString())).getR());

						if(d.getTokens().size() >= 1)
						{
							if(d.getTokens().get(0) instanceof TString) 
							{
								foundString = true;
								line = token.getLine();
								break;
							}

							if(d.getTokens().get(0) instanceof TNumber) 
							{
								foundNumber = true;
								if(d.getTokens().get(0).getText().contains(".")) foundDouble = true;
								line = token.getLine();
								break;
							}

							if(d.getTokens().get(0) instanceof TNone)
							{
								break;
							}

							if(d.getTokens().get(0) instanceof TId)
							{
								if(!symtable.containsKey(d.getTokens().get(0).toString()))
								{
									break;
								}
								else
								{
									token = d.getTokens().get(0);
								}
							}
						}
					}
				}
				else
				{
					break;
				}
			}
		}

		if(addOnly)
		{
			if(foundNumber && foundString)
			{
				foundError = true;
				System.out.println("Line " + line + ":  Found string(s) and number(s) in addition");
			}
		}
		else if(multOnly)
		{
			if(foundDouble)
			{
				foundError = true;
				System.out.println("Line " + line + ":  strings can only be multiplied with integers");
			}
			if(!foundNumber && foundString)
			{
				foundError = true;
				System.out.println("Line " + line + ":  strings can only be multiplied with integers");
			}
		}
		else
		{
			if(foundString)
			{
				foundError = true;
				System.out.println("Line " + line + ":  strings can only be used in additions");
			}
		}
		
		return foundError;
	}

	public boolean checkArgSize(int line, int numOfArgs, AFunction node, boolean arithmetics)
	{
		boolean isCorrect = true; //checks if argsize is correct for the defined function.
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

		if(numOfArgs < minArgs || numOfArgs > maxArgs)
		{
			isCorrect = false;
			if(!arithmetics) System.out.println("Line " + line + ":  Found " + numOfArgs + " arguments, expected [" + minArgs + ", " + maxArgs + "]");
		}

		return isCorrect;
	}

	public PStatement runFunction(AFunction fun, AFunctionCall funCall)
	{
		fun = (AFunction) fun.clone();
		funCall = (AFunctionCall) funCall.clone();
		PStatement returnStatement = (PStatement)fun.getStatement().clone();
		ArithmeticData data = new ArithmeticData();
		if(funCall.getArglist().size()>0)
		{
			AArglist argFunCall = (AArglist)funCall.getArglist().get(0);
			AArgument argFun = (AArgument)fun.getArgument().get(0);

			AIdExpression search = (AIdExpression)argFun.getExpression().clone();
			PExpression assign = (PExpression)argFunCall.getL().clone();
			
			if(assign instanceof AFunExpression || assign instanceof AFuncExpression)
			{
				PStatement statement;
				if(assign instanceof AFunExpression)
				{
					statement = runFunction(fun, (AFunctionCall)((AFunExpression)assign).getFunctionCall());
				} 
				else
				{
					statement = runFunction(fun, (AFunctionCall)((AFuncExpression)assign).getFunctionCall());
				}
				
				if(statement instanceof AReturnStatement)
				{
					assign = (PExpression)((AReturnStatement)statement).getExpression().clone();
					data.addAll(checkExpression(assign));
				}
				else
				{
					int line = ((TId)search.getId()).getLine();
					System.out.println("Line " + line + ": no return statement found");
					assign = new ANoneExpression(new TNone(line, ((TId)search.getId()).getPos()));
					data.addAll(checkExpression(assign));
				}
			}

			AEqualsStatement update = new AEqualsStatement(search, assign);
			symtable.put(search.getId().toString(), update);
			if(argFunCall.getR().size()>0)
			{
				for(int i=0; i<argFunCall.getR().size(); i++)
				{
					AMoreIdentifiers moreIds = (AMoreIdentifiers)argFun.getMoreIdentifiers().get(i);
					assign = (PExpression)((AArglist)argFunCall.clone()).getR().get(i);
					search = (AIdExpression)((AMoreIdentifiers)moreIds.clone()).getExpression();
					
					if(assign instanceof AFunExpression || assign instanceof AFuncExpression)
					{
						PStatement statement;
						if(assign instanceof AFunExpression)
						{
							statement = runFunction(fun, (AFunctionCall)((AFunExpression)assign).getFunctionCall());
						} 
						else
						{
							statement = runFunction(fun, (AFunctionCall)((AFuncExpression)assign).getFunctionCall());
						}

						if(statement instanceof AReturnStatement)
						{
							assign = ((AReturnStatement)statement).getExpression();
							data.addAll(checkExpression(assign));
						}
						else
						{
							int line = ((TId)search.getId()).getLine();
							System.out.println("Line " + line + ": no return statement found");
							assign = new ANoneExpression(new TNone(line, ((TId)search.getId()).getPos()));
							data.addAll(checkExpression(assign));
						}
					}

					update = new AEqualsStatement(search, assign);
					symtable.put(search.getId().toString(), update);
				}
			}
		}
		
		if(fun.getStatement() instanceof AReturnStatement)
		{
			PExpression returnExp = ((AReturnStatement)fun.getStatement()).getExpression();
			data.addAll(checkExpression(returnExp));
			
			checkArithmetics(data);
		}
		else if(fun.getStatement() instanceof APrintStatement)
		{
			APrintStatement print = (APrintStatement)fun.getStatement();
			checkArithmetics(checkExpression(print.getL()));
			if(print.getR().size() > 0)
			{
				for(int i=0; i<print.getR().size(); i++)
				{
					checkArithmetics(checkExpression((PExpression)print.getR().get(i)));
				}
			}
		}
		return returnStatement;
	}

	public PExpression checkElement(PExpression exp)
	{
		exp = (PExpression)exp.clone();
		if(exp instanceof AIdExpression)
		{
			AIdExpression id = (AIdExpression)exp;
			if(symtable.containsKey(id.getId().toString()))
			{
				if(symtable.get(id.getId().toString()) instanceof AEqualsStatement)
				{
					AEqualsStatement old = (AEqualsStatement) symtable.get(id.getId().toString());

					if((PExpression)old.getR().clone() instanceof AStringExpression)
					{
						((AStringExpression)old.getR()).getString().setLine(id.getId().getLine());
					}
					if((PExpression)old.getR().clone() instanceof ANumberExpression)
					{
						((ANumberExpression)old.getR()).getNumber().setLine(id.getId().getLine());
					}
					if((PExpression)old.getR().clone() instanceof ANoneExpression)
					{
						((ANoneExpression)old.getR()).getNone().setLine(id.getId().getLine());
					}
					
					return (PExpression)old.getR().clone();
				}
			}
		}
		return exp;
	}
}
