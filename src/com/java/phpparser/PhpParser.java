package com.java.phpparser;

import java.util.ArrayList;
import java.lang.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileInputStream;
import java.nio.charset.Charset;

public class PhpParser {
	private String token = null;
	private ArrayList scriptTokens = null;
	private static int index = 0;
	private int tokenSize = 0;
	private TerminalSymbol term = null;

	private String[] stopword = { "_construct", "_destruct", "protected",
			"abstract", "continue", "declare", "foreach", "function",
			"default", "private", "public", "return", "static", "switch",
			"(string)", "(object)", "(float)", "(array)", "(bool)", "(int)",
			"object", "string", "while", "class", "const", "array", "break",
			"echo", "elseif", "final", "print", "float", "bool", "int", "xor",
			"<?php", "$this", "case", "else", "and", "new", "for", "do", "if",
			"or", "===", "!==", "<?", "?>", "==", "!=", "<<", ">>", "++", "--",
			"==", "!=", "<>", "<", ">", ">=", "<=", "&&", "||", "*=", "/=",
			"%=", "+=", "-=", "&=", "|=", "^=", "<<=", ">>=", "->", ".=", "&",
			"|", "^", "*", "/", "%", "+", "-", "!", "=", ";", "{", "}", "(",
			")", "\"", "." };

	public PhpParser(String filename) {

		this.term = new TerminalSymbol();
		// read script from file, parse, and save it to local variable
		// (scriptTokens)
		this.readFile(filename);
		this.tokenSize = scriptTokens.size();

		// initialize nextToken var to save the next word read from script
		this.index = 0;
		this.token = this.scriptTokens.get(index).toString();
		for (int i = 0; i < tokenSize; i++) {
			System.out.println(scriptTokens.get(i).toString());
		}
		// System.out.println("-----------------------------------\n");
		// move nextToken pointer to the first word of array of words
		// System.out.println(index+": "+this.token);
		// this.token=this.nextToken();
		// System.out.println(index+": "+this.token);
		// this.token=this.nextToken();
		// System.out.println(index+": "+this.token);
		// this.token=this.nextToken();
		// System.out.println(index+": "+this.token);
		// System.out.println("check number: "+this.isNumber("token"));
	}

	public void readFile(String filename) {

		String strScript = "";
		// This will reference one line at a time
		Charset encoding = Charset.defaultCharset();
		String line = null;
		TerminalSymbol sym = new TerminalSymbol();
		System.out.println("file-name: " + filename);
		try {
			InputStream in = new FileInputStream(filename);
			Reader reader = new InputStreamReader(in, encoding);
			// buffer for efficiency
			Reader buffer = new BufferedReader(reader);
			int r;
			int idx = 0, idxStr = 0;
			while ((r = reader.read()) != -1) {
				char ch = (char) r;
				strScript = strScript + ch;
			}

			// call the tokenizing method
			this.scriptTokens = this.tokenizing(strScript);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + filename + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + filename + "'");
		}
	}

	public ArrayList tokenizing(String script) {
		// ArrayList tokens=null;
		ArrayList<String> stopWordStr = new ArrayList<String>();
		ArrayList<Integer> stopWordPos = new ArrayList<Integer>();
		;
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuffer temp = new StringBuffer(); // save the temporary string of
												// single token

		/*
		 * listing the stopword position in the script, this special stopword
		 * will also become a token
		 */
		for (int x = 0; x < stopword.length; x++) {
			int i = 0;
			while ((i = (script.indexOf(stopword[x], i) + 1)) > 0) {
				boolean contain = false;
				for (int y = 0; y < stopWordPos.size(); y++) {
					if (i >= stopWordPos.get(y)
							&& i <= (stopWordPos.get(y) + (stopWordStr.get(y)
									.toString().length()))) {
						contain = true;
					}
				}
				if (contain == false) {
					// check if reserve word is part of another string or not
					int charpivot = (int) script.charAt(i);
					if ((charpivot >= 97 && charpivot <= 122)
							|| charpivot == 95) {
						if (i == 0 || (int) script.charAt(i - 1) == 32
								|| (int) script.charAt(i - 1) == 10
								|| (int) script.charAt(i - 1) == 40) {
							stopWordStr.add(stopword[x]);
							stopWordPos.add(i - 1);
						}
					} else {
						stopWordStr.add(stopword[x]);
						stopWordPos.add(i - 1);
					}
				}
			}
		}
		// System.out.println("list of stop word");
		// for(int i=0; i<stopWordStr.size();i++){
		// System.out.println(stopWordStr.get(i).toString()+": "+stopWordPos.get(i));
		// }

		System.out.println(" ");
		System.out.println("TOKENS");
		System.out.println("=========");
		boolean isquote = false; // give a flag to the string between quotes
		int count = 0;

		for (int a = 0; a < script.length(); a++) {
			int r = (int) script.charAt(a);
			boolean isStopWord = false;
			boolean flag1 = false;
			// if character is double quotes
			if (r == 34 || r == 39) {
				if (isquote == false) {
					isquote = true;
				} else {
					isquote = false;
				}
			}
			// the character doesn't meet quotes
			if (isquote == false) {
				int idx = 0; // index of stopword
				// check if character is in index position of stopword, if true,
				// that character is
				// first character of stopword/token
				for (int i = 0; i < stopWordStr.size(); i++) {
					// add stop word into list of tokens
					if (a == (int) stopWordPos.get(i)) {
						isStopWord = true;
						idx = i;
						// count++;
						break;
					}
				}
				if (isStopWord == true) {
					if (!temp.toString().equals("")) {
						tokens.add(temp.toString());
						temp = new StringBuffer();
						count++;
					}
					// add stopword into tokens
					int len = stopWordStr.get(idx).length();
					tokens.add(stopWordStr.get(idx));

					a = a + len - 1;
					isStopWord = false;
					count++;
				} else { // otherwise collect char from another string and make
							// it a token
					if (r == 32 || r == 10 || r == 9) { // check of char is
														// space, line feed, or
														// tab
						if (!temp.toString().equals("")) {
							tokens.add(temp.toString());
							temp = new StringBuffer();
							count++;
						}
					} else {
						temp.append((char) r);
					}
				}
			} else {
				temp.append((char) r);
			}
		}
		return tokens;
	}

	public String nextToken() {
		String nextoken = null;
		index++;
		if (index < tokenSize) {
			nextoken = scriptTokens.get(index).toString();
		}
		return nextoken;
	}

	public void program() {
		if (this.token.equalsIgnoreCase("<?php")
				|| this.token.equalsIgnoreCase("<?")) {
			this.token = this.nextToken();
			phpScript();
			if (this.token.equalsIgnoreCase("?>")) {
				this.token = this.nextToken();
				if (this.token != null) {
					this.program();
				} else {
					System.out.println("Syntax is correct!!");
				}
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		}

		else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	public void phpScript() {
		ArrayList phpscript = null;
		if (this.token.equalsIgnoreCase("")) {

		} else if (this.token.equalsIgnoreCase("")) {

		}
	}
	
	//tambahan property
	public void property() {
		if (this.token.equalsIgnoreCase("public")
				|| this.token.equalsIgnoreCase("private")
				|| this.token.equalsIgnoreCase("protected")
				|| this.token.equalsIgnoreCase("final")
				|| this.token.equalsIgnoreCase("static")) {
			finalProperty();
			visibilityProperty();
			staticProperty();
			return;
		}
		else{
			return;
		}
//		System.out.println("Syntax error!!");
//		System.exit(1);
	}

	public void finalProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("final")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	public void visibilityProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("public")
				|| this.token.equalsIgnoreCase("private")
				|| this.token.equalsIgnoreCase("protected")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	public void staticProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("static")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	public void abstractProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("abstract")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}
	
	//variableDeclaration
	public void variableDeclaration(){
		simpleVarName();
		simpleVariableNameCont();
	}
	
	//SimpleVariableNameCont
	public void simpleVariableNameCont() {
		// TODO Auto-generated method stub
		if(this.token.equalsIgnoreCase("=")){
			this.token = this.nextToken();
			this.variableValue();
			if(this.token.equalsIgnoreCase(";")){
				this.nextToken();
			}
			else{
				System.out.println("syntax error!!");
				System.exit(1);
			}
		}
		else if(this.token.equalsIgnoreCase(";")){
			this.token = this.nextToken();
		}
		else if(this.token.equalsIgnoreCase("[")){
//			this.dimension();
		}
		else{
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}
	
	//variableValue
	public void variableValue() {
		// TODO Auto-generated method stub
		if(this.token.equalsIgnoreCase("array")){
			this.token = this.nextToken();
			if (this.token.equalsIgnoreCase("(")) {
				this.token = this.nextToken();
//				this.arrayValue();
				if (this.token.equalsIgnoreCase("(")) {
					this.token = this.nextToken();
				}
				else{
					System.out.println("syntax error!!");
					System.exit(1);
				}
			}
			else{
				System.out.println("syntax error!!");
				System.exit(1);
			}
		}
		else if(this.token.contains("\"") || this.token.contains("\'") || isNumber()){
			Values();
		}
		else{
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}
	
	//Values
	private void Values() {
		// TODO Auto-generated method stub
		//belum
	}
	
	//memberClassCalling
	public void memberClassCall(){
		if(this.token.equals("$this")){
			this.token=this.nextToken();
			if(this.token.equals("->")){
				this.token=this.nextToken();
				if(this.isIdentifier()){
					this.token = this.nextToken();
					this.functionArgExpression();
					return;
				}
			}
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	
	//basicExpression
	public void basicExpression(){
		//cek nilai first dari basicExpression
		
		if(this.token.charAt(0)=='$'){ //cek variabel
			if(this.token.equals("$this")){ //cek member class
				this.memberClassCall();
			}
			//masuk fungsi variableName()
		}
		else if(this.isNumber()){ //cek token adalah angka
			this.number();
		}
		else if(this.isString()) { //cek token adalah string
			this.string();
		}
		else if(this.isIdentifier()){
			//if(){
				
			//}
		}
			
	}
	
	//functionArgExpression
	public void functionArgExpression(){
		if(this.token.equals("(")){
			this.token = this.nextToken();
			//parameterList()
			this.parameterList();
			if(this.token.equals(")")) {
				this.token = this.nextToken();
				return;
			}
			else{
				System.out.println("Syntax error!!");
				System.exit(1);
			}
		}
		return;
	}
	
	public void parameterList(){
		
		return;
	}
	
	//function functionCallStatement
	public void functionCallStatement(){
		this.functionCallExpression();
		if(this.token.equals(";")){
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	
	//function call expression	
	public void functionCallExpression(){
		//check the current token is identifier (function name) or not
		this.functionName();
		if(this.token.equals("(")){
			this.token = this.nextToken();
			//call function parameterlist()
			this.parameterList();
			if(this.token.equals(")")) {
				return;
			}
		}
		
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	//function functionName()
	public void functionName(){
		if(this.isIdentifier()) {
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	
	//directives
	public void directives(){
		if(this.token.equals("ticks")){
			this.token = this.nextToken();
			if(this.token.equals("=")){
				if(isDigit()) {
					this.token= this.nextToken();
					return;
				}
			}
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	//simpleVarName
	public void simpleVarName(){
		//this.token="$var";
		if(this.token.charAt(0)=='$'){
			//get the identifier name after '$' character
			this.token = this.token.substring(1);
			//check if it is identifier or not
			if(this.isIdentifier()) {
				this.token=this.nextToken();
				return;
			}
			System.out.println("Syntax error!!");
			System.exit(1);
		}
	}
	public void string(){
		if(this.isString()){
			this.token=this.nextToken();
			return;
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	
	public void number(){
		if(this.isNumber()){
			this.token=this.nextToken();
			return;
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}
	
	// check is identifier
	public boolean isIdentifier() {
		if ((int) token.charAt(0) != 95
				&& !((int) token.charAt(0) >= 65 && (int) token.charAt(0) <= 90)
				&& !((int) token.charAt(0) >= 97 && (int) token.charAt(0) <= 122)) {
			return false;
		}
		for (int i = 1; i < token.length(); i++) {
			if ((int) token.charAt(i) != 95
					&& !((int) token.charAt(i) >= 65 && (int) token.charAt(i) <= 90)
					&& !((int) token.charAt(i) >= 97 && (int) token.charAt(i) <= 122)
					&& !((int) token.charAt(i) >= 48 && (int) token.charAt(i) <= 57)) {
				return false;
			}
		}
		return true;
	}

	// check is String
	public boolean isString() {
		if ((int) this.token.charAt(0) == 34
				&& (int) this.token.charAt(this.token.length() - 1) == 34) {
			return true;
		} else if ((int) this.token.charAt(0) == 39
				&& (int) this.token.charAt(this.token.length() - 1) == 39) {
			return true;
		}
		return false;
	}

	// check is number
	public boolean isNumber() {
		if (isDigit()) {
			this.token = this.nextToken();
			if (this.token.equals(".")) {
				this.token = this.nextToken();
				if (isDigit()) {
					return true;
				} else {
					System.out.println("syntax error!!");
					return false;
				}
			} else {
				return true;
			}
		}
		return false;
	}
	
	// check is String
	public boolean isDigit() {
		if ((int) this.token.charAt(0) >= 48
				&& (int) this.token.charAt(0) <= 57) {
			// this.token=this.nextToken();
			return true;
		}
		return false;
	}
	/* method dibawah ini digunakan untuk melakukan pengecekan pada operator*/
	// is prefix operator
	public boolean isPrefixOp(String op) {
		if (isCastingOp(op) || isSuffixOp(op) || isOtherOp(op)) {
			return true;
		}
		return false;
	}

	// is other operator
	public boolean isOtherOp(String op) {
		for (int i = 0; i < this.term.otherOp.length; i++) {
			if (this.term.otherOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is casting operator
	public boolean isCastingOp(String op) {
		for (int i = 0; i < this.term.castOp.length; i++) {
			if (this.term.castOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is suffix operator
	public boolean isSuffixOp(String op) {
		if (isIncrementOp(op) || isDecrementOp(op)) {
			return true;
		}
		return false;
	}

	// is suffix operator
	public boolean isIncrementOp(String op) {
		for (int i = 0; i < this.term.incrOp.length; i++) {
			if (this.term.incrOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is suffix operator
	public boolean isDecrementOp(String op) {
		for (int i = 0; i < this.term.dcrOp.length; i++) {
			if (this.term.dcrOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is assignment operator
	public boolean isAssignmentOp(String op) {
		for (int i = 0; i < this.term.assignOp.length; i++) {
			if (this.term.assignOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is comparison operator
	public boolean isArithmeticOp(String op) {
		if (isAddOp(op) || isMultiOp(op)) {
			return true;
		}
		return false;
	}

	// is additive operator
	public boolean isAddOp(String op) {
		for (int i = 0; i < this.term.addOp.length; i++) {
			if (this.term.addOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is multiplicative operator
	public boolean isMultiOp(String op) {
		for (int i = 0; i < this.term.multiOp.length; i++) {
			if (this.term.multiOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is bitwise operator
	public boolean isBitwiseOp(String op) {
		for (int i = 0; i < this.term.bitwiseOp.length; i++) {
			if (this.term.bitwiseOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is shift operator
	public boolean isShiftOp(String op) {
		for (int i = 0; i < this.term.shiftOp.length; i++) {
			if (this.term.shiftOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is comparison operator
	public boolean isComparisonOp(String op) {
		if (isEqualityOp(op) || isRelationalOp(op)) {
			return true;
		}

		return false;
	}

	// is equality operator
	public boolean isEqualityOp(String op) {
		for (int i = 0; i < this.term.equalOp.length; i++) {
			if (this.term.equalOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is relational operator
	public boolean isRelationalOp(String op) {
		for (int i = 0; i < this.term.relationOp.length; i++) {
			if (this.term.relationOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is logic operator
	public boolean isLogicOp(String op) {
		for (int i = 0; i < this.term.logicOp.length; i++) {
			if (this.term.logicOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}
	/*
	 * public String firstCharacter(){ String firstStr=null;
	 * 
	 * 
	 * return firstStr; }
	 * 
	 * public String follow(ArrayList string){ String followStr=null;
	 * 
	 * 
	 * return followStr; }
	 */

	public boolean searchTerminal(String[] listTerm, String term) {
		for (int i = 0; i < listTerm.length; i++) {
			if (listTerm[i].equals(term)) {
				return true;
			}
		}
		return false;
	}
}