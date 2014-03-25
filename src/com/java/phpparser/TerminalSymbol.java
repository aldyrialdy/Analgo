package com.java.phpparser;

public class TerminalSymbol{
	public String[] keyWord = {"break","clone","die","empty","endswitch","final","global",
			"include_once","list","private","return","try","xor","abstract","callable",
			"const","do","enddeclare","endwhile","finally","goto","instanceof","namespace",
			"protected","static","unset","yield","and","case","continue","echo","endfor","eval",
			"for","if","insteadof","new","public","switch","use","array","catch","declare","else",
			"endforeach","exit","foreach","implements","interface","or","require","throw","var","",
			"as","class","default","elseif","endif","extends","function","include","isset","print",
			"require_once","trait","while"};
	public String[] letter = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
			"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	public String[] number = {"1","2","3","4","5","6","7","8","9","0"};
	public String[] specialChar = {"~","!","@","#","$","%","^","&","*","(",")","_","+","{","}","|","[","]",":",";","<",">","?",",","." };
	//public String[] escapeChar = {"\n","\r","\t","\v","\e","\f","\\","\$","\*"};
	public String[] aritmaOp = {"*","/","%","+","-"};
	public String[] multiOp = {"*","/","%"};
	public String[] addOp = {"+","-"};
	//public String[] comparisonOp = {"==","===","!=","<>","!==","<",">",">=","<="};
	public String[] relationOp = {"==","===","!=","<>","!=="};
	public String[] equalOp = {"<",">",">=","<="};
	public String[] logicOp = {"and","or","xor","!","&&","||"};
	public String[] assignOp = {"=","*=","/=","%=","+=","-=","&=","|=","^=","<<=",">>=",".="};
	public String[] strOp = {"."};
	public String[] btwsOp = {"&","|","^","<<",">>"};
	//public String[] suffixOp = {"++","--"};
	public String[] incrOp = {"++"};
	public String[] dcrOp = {"--"};
	public String[] arrayOp = {"+","==","===","!=","<>","!=="};
	public String[] shiftOp = {"<<",">>"};
	public String[] bitwiseOp = {"<<",">>","&","^","|"};
	public String[] castOp = {"(string)","(object)","(float)","(array)","(bool)","(int)"};
	public String[] otherOp = {"!","-","~",",","@","instanceof","clone","new","include","require"}; 
	public String[] concatOp = {".",","};
	
	//adding terminal symbol to compare first nonterminal
	public String[] prefix = {"++","--","!","-","~","<Casting-Operator>","`","@","instanceof","clone","new","include","require"};
	public String[] suffix = {"++","--"};
	public String[] assignment = {"=","+=","-=","*=","/=","%=","<<=",">>=","&=","^=","|="};
	public String[] additive= {"+","-","."};
	public String[] multiplicative = {"*","/","^"};
	public String[] bitwise = {">>","<<","&","^","|"};
	public String[] shift = {">>","<<"};
	public String[] comparison = {"<",">","<=",">=","==","!=","<>","===","!=="};
	public String[] equality = {"==","!=","<>","===","!=="};
	public String[] relational = {"<",">","<=",">="};
	public String[] logical = {"and","or","xor","!","&&","||"};
	public String pluses = "++";
	public String minuses = "--";
	public String stringOps = ".";
	public String[] castOps = {"(int)","(float)","(string)","(array)","(object)","(bool)"};
	public String highBoolAndOps = "&&";
	public String lowBoolAndOps = "and";
	public String highBoolOrOps = "||";
	public String lowBoolOrOps = "||";
	public String[] booleanVal ={"true", "false"};
	public String underscore = "_";
	
	public TerminalSymbol(){

	}
}
