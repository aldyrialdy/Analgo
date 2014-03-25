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
		System.out.println("-----------------------------------\n");
		// move nextToken pointer to the first word of array of words
		System.out.println(index + ": " + this.token);
		this.token = this.nextToken();
		System.out.println(index + ": " + this.token);
		this.token = this.nextToken();
		System.out.println(index + ": " + this.token);
		this.token = this.nextToken();
		System.out.println(index + ": " + this.token);
		this.additiveExpress();
		// System.out.println("check number: "+this.isNumber());
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
				System.out.println("syntax error!! program");
				System.exit(1);
			}
		}

		else {
			System.out.println("syntax error!! program");
			System.exit(1);
		}
	}

	// phpScript
	/*
	 * $ untuk rule <Basic-Expression> dan <Variable-Declaration> abstract untuk
	 * rule <Class-Declaration> function untuk rule <Function-Declaration> echo
	 * dan print untuk rule <Text-Display-Statement> String, number, _ ,", '
	 * untuk rule <Compound-Statement>
	 */
	public void phpScript() {
		// ArrayList phpscript = null;
		if (this.token.charAt(0) == '$'
				|| this.token.equalsIgnoreCase("abstract")
				|| this.token.equalsIgnoreCase("function")
				|| this.token.equalsIgnoreCase("echo")
				|| this.token.equalsIgnoreCase("print") || isString()
				|| isNumber() || this.token.equalsIgnoreCase("_")
				|| this.token.contains("\"") || this.token.contains("\'")) {
			structuredScript();
			phpScript();
		} else if (this.token.equalsIgnoreCase("?>")) {
			return;
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// structuredScript
	public void structuredScript() {
		// TODO Auto-generated method stub
		// if (this.token.equalsIgnoreCase("abstract")) {
		// classDeclaration();
		// structuredScriptList();
		// } else if (this.token.equalsIgnoreCase("function")) {
		// functionDeclaration();
		// structuredScriptList();
		// } else if (this.token.equalsIgnoreCase("echo")
		// || this.token.equalsIgnoreCase("print")) {
		// textDisplayStatement();
		// structuredScriptList();
		// } else if (isString() || isNumber() ||
		// this.token.equalsIgnoreCase("_")
		// || this.token.contains("\"") || this.token.contains("\'")
		// || this.token.equalsIgnoreCase("$this")) {
		// compoundStatement();
		// structuredScriptList();
		// } else if (this.token.contains("$")) {
		//
		// }
	}

	// property - aldy
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
		} else {
			return;
		}
		// System.out.println("Syntax error!!");
		// System.exit(1);
	}

	// finalProperty - aldy
	public void finalProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("final")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	// visibilityProperty - aldy
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

	// staticProperty - aldy
	public void staticProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("static")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	// abstractProperty - aldy
	public void abstractProperty() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("abstract")) {
			this.token = this.nextToken();
		} else {
			return;
		}
	}

	// variableDeclaration - aldy
	public void variableDeclaration() {
		simpleVarName();
		simpleVariableNameCont();
		if (this.token.equalsIgnoreCase(";")) {
			this.token = this.nextToken();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// SimpleVariableNameCont - aldy
	public void simpleVariableNameCont() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("=")) {
			this.token = this.nextToken();
			this.variableValue();
		}
	}

	// variableValue - aldy
	public void variableValue() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("array")) {
			this.token = this.nextToken();
			if (this.token.equalsIgnoreCase("(")) {
				this.token = this.nextToken();
				this.arrayValues();
				if (this.token.equalsIgnoreCase(")")) {
					this.token = this.nextToken();
				} else {
					System.out.println("syntax error!!");
					System.exit(1);
				}
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else if (this.token.contains("\"") || this.token.contains("\'")
				|| isNumber()) {
			Values();
		} else if (this.token.equalsIgnoreCase("[")) {
			// this.dimension();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// arrayValues - aldy
	public void arrayValues() {
		// TODO Auto-generated method stub
		if (this.getTokenAt(this.getCurrentIndex() + 1).equalsIgnoreCase("=")
				&& this.getTokenAt(this.getCurrentIndex() + 2)
						.equalsIgnoreCase(">")) {
			this.indexAndValues();
			this.arrayValuesList();
		} else if (this.getTokenAt(this.getCurrentIndex() + 1)
				.equalsIgnoreCase(",")) {
			this.Values();
			this.arrayValuesList();
		}
	}

	// arrayValuesList - aldy
	public void arrayValuesList() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase(",")) {
			this.token = this.nextToken();
			this.arrayValues();
		}
	}

	public void indexAndValues() {
		// TODO Auto-generated method stub
		this.indexName();
		if (this.token.equalsIgnoreCase("=")){
			this.token = this.nextToken();
			if(this.token.equalsIgnoreCase(">")){
				this.token = this.nextToken();
				this.Values();
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
	
	public void indexName() {
		// TODO Auto-generated method stub
		if(this.isString() || this.isNumber()){
			this.token = this.nextToken();
		}
		else{
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// Values - aldy
	public void Values() {
		// TODO Auto-generated method stub
		if (this.isString()) {
			this.token = this.nextToken();
		} else if (this.isNumber()) {
			this.token = this.nextToken();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// compoundStatement - aldy
	public void compoundStatement() {
		this.Statement();
		this.compoundStatementNext();
	}

	// compoundStatementNext - aldy
	public void compoundStatementNext() {
		// TODO Auto-generated method stub
		if (this.token.contains("$")
				|| searchTerminal(this.term.jumpStatement, this.token)
				|| this.token.equalsIgnoreCase("declare")
				|| searchTerminal(this.term.flowControl, this.token)
				|| this.token.equalsIgnoreCase(";") || isIdentifier()) {
			this.compoundStatement();
		}
	}

	// Statement - aldy
	public void Statement() {
		// TODO Auto-generated method stub
		if (this.token.charAt(0) == '$' || this.isNumber() || this.isString()
				|| this.isIdentifier()) { // cek variabel
			this.expressionStatement();
		} else if (searchTerminal(this.term.jumpStatement, this.token)) {
			this.jumpStatement();
		} else if (this.token.equalsIgnoreCase("declare")) {
			this.declareStatement();
		} else if (searchTerminal(this.term.flowControl, this.token)) {
			this.flowControlStatement();
		} else if (this.token.equalsIgnoreCase(";")) {
			this.nullStatement();
		} else if (this.getTokenAt(this.getCurrentIndex() + 1)
				.equalsIgnoreCase("-")
				&& this.getTokenAt(this.getCurrentIndex() + 2)
						.equalsIgnoreCase(">")) {
			this.objectInstantiationStatement();
		} else if (this.getTokenAt(this.getCurrentIndex() + 1)
				.equalsIgnoreCase("=")
				|| this.getTokenAt(this.getCurrentIndex() + 1)
						.equalsIgnoreCase("::")) {
			this.classMemberCallingStatement();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// objectInstantiationStatement - aldy
	public void objectInstantiationStatement() {
		// TODO Auto-generated method stub
		// this.objectName();
		if (this.token.equalsIgnoreCase("=")) {
			this.token = this.nextToken();
			if (this.token.equalsIgnoreCase("new")) {
				this.token = this.nextToken();
				// this.className();
				if (this.token.equalsIgnoreCase("(")) {
					this.token = this.nextToken();
					this.parameterList();
					if (this.token.equalsIgnoreCase(")")) {
						this.token = this.nextToken();
						if (this.token.equalsIgnoreCase(";")) {
							this.token = this.nextToken();
						} else {
							System.out.println("syntax error!!");
							System.exit(1);
						}
					} else {
						System.out.println("syntax error!!");
						System.exit(1);
					}
				} else {
					System.out.println("syntax error!!");
					System.exit(1);
				}
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// classMemberCallingStatement - aldy
	public void classMemberCallingStatement() {
		// TODO Auto-generated method stub
		// this.classMemberCallingExpression();
		if (this.token.equalsIgnoreCase(";")) {
			this.token = this.nextToken();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// expressionStatement - aldy
	public void expressionStatement() {
		// TODO Auto-generated method stub
		this.expression();
		if (this.token.equalsIgnoreCase(";")) {
			this.token = this.nextToken();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// jumpStatement - aldy
	public void jumpStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("continue")) {
			this.continueStatement();
		} else if (this.token.equalsIgnoreCase("break")) {
			this.breakStatement();
		} else if (this.token.equalsIgnoreCase("return")) {
			this.returnStatement();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// declareStatement - aldy
	public void declareStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("declare")) {
			this.token = this.nextToken();
			if (this.token.equalsIgnoreCase("(")) {
				this.token = this.nextToken();
				this.directives();
				if (this.token.equalsIgnoreCase(")")) {
					this.token = this.nextToken();
					this.declareStatementNext();
				} else {
					System.out.println("syntax error!!");
					System.exit(1);
				}
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// declareStatementNext - aldy
	public void declareStatementNext() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase(";")) {
			this.token = this.nextToken();
			compoundStatement();
		} else if (this.token.equalsIgnoreCase("{")) {
			this.token = this.nextToken();
			compoundStatement();
			if (this.token.equalsIgnoreCase("}")) {
				this.token = this.nextToken();
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// flowControlStatement
	public void flowControlStatement() {
		// TODO Auto-generated method stub
		// belum
	}

	// nullStatement - aldy
	public void nullStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase(";")) {
			this.token = this.nextToken();
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// continueStatement - aldy
	public void continueStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("continue")) {
			this.token = this.nextToken();
			this.digitConstantaNext();
			if (this.token.equalsIgnoreCase(";")) {
				this.token = this.nextToken();
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// returnStatement - aldy
	public void returnStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("return")) {
			this.token = this.nextToken();
			this.returnStatementNext();
			if (this.token.equalsIgnoreCase(";")) {
				this.token = this.nextToken();
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// returnStatementNext - aldy
	public void returnStatementNext() {
		// TODO Auto-generated method stub
		if (this.token.charAt(0) == '$' || this.isNumber() || this.isString()
				|| this.isIdentifier()) {
			expression();
		}
	}

	// breakStatement - aldy
	public void breakStatement() {
		// TODO Auto-generated method stub
		if (this.token.equalsIgnoreCase("break")) {
			this.token = this.nextToken();
			this.digitConstantaNext();
			if (this.token.equalsIgnoreCase(";")) {
				this.token = this.nextToken();
			} else {
				System.out.println("syntax error!!");
				System.exit(1);
			}
		} else {
			System.out.println("syntax error!!");
			System.exit(1);
		}
	}

	// digitConstantaNext - aldy
	public void digitConstantaNext() {
		// TODO Auto-generated method stub
		if (this.isDigit()) {
			// this.digitConstanta();
		}
	}

	// memberClassCalling - mahar
	public void memberClassCall() {
		if (this.token.equals("$this")) {
			this.token = this.nextToken();
			if (this.token.equals("->")) {
				this.token = this.nextToken();
				if (this.isIdentifier()) {
					this.token = this.nextToken();
					this.functionArgExpression();
					return;
				}
			}
		}
		System.out.println("Syntax error!!");
		System.exit(1);
	}

	// functionArgExpression - mahar
	public void functionArgExpression() {
		if (this.token.equals("(")) {
			this.token = this.nextToken();
			// parameterList()
			this.parameterList();
			if (this.token.equals(")")) {
				this.token = this.nextToken();
				return;
			} else {
				System.out.println("Syntax error!!");
				System.exit(1);
			}
		}
		return;
	}

	public void parameterList() {

		return;
	}

	// function expression - mahar
	public void expression() {
		// cek token berikutnya dengan operator
		if (this.isAddOp(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.additiveExpress();
		} else if (this.isMultiOp(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.multiplicativeExpress();
		} else if (this
				.isEqualityOp(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.equalityExpress();
		} else if (this
				.isRelationalOp(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.relationalExpress();
		} else if (this.isShiftOp(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.shiftExpress();
		} else if (this
				.isBitwiseAnd(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.bitwiseAndExpress();
		} else if (this
				.isBitwiseOr(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.bitwiseOrExpress();
		} else if (this
				.isBitwiseXor(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.bitwiseAndExpress();
		} else if (this
				.isLowBooleanOr(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.lowBooleanOrExpress();
		} else if (this
				.isHighBooleanOr(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.highBooleanOrExpress();
		} else if (this
				.isLowBooleanAnd(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.lowBoolanAndExpress();
		} else if (this
				.isHighBooleanAnd(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.highBoolanAndExpress();
		} else if (this
				.isBooleanXor(this.getTokenAt(this.getCurrentIndex() + 1))) {
			this.booleanXorExpress();
		}
	}

	// additiveExpress - mahar
	public void additiveExpress() {
		this.multiplicativeExpress();
		// this.token = this.nextToken();
		this.additiveExpress2();
		return;
	}

	// additiveExpress2 - mahar
	public void additiveExpress2() {
		System.out.println("enter additiveExpress2: " + this.token);
		if (this.isAddOp(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.additiveExpress();
		}
		return;
	}

	// multiplicativeExpress - mahar
	public void multiplicativeExpress() {
		this.prefixExpress();
		this.multiplicativeExpress2();
		return;
	}

	// multiplicativeExpress2 - mahar
	public void multiplicativeExpress2() {
		System.out.println("enter multiplicativeExpress2: " + this.token);
		if (this.isMultiOp(this.token)) {

			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.multiplicativeExpress();
		}
		return;
	}

	// EqualityExpress - mahar
	public void equalityExpress() {
		this.relationalExpress();
		this.equalityExpress2();
		return;
	}

	// EqualityExpress2 - mahar
	public void equalityExpress2() {
		System.out.println("enter EqualityExpress2: " + this.token);
		if (this.isEqualityOp(this.token)) {

			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.equalityExpress();
		}
		return;
	}

	// relationalExpress - mahar
	public void relationalExpress() {
		this.shiftExpress();
		this.relationalExpress2();

	}

	// relationalExpress2 - mahar
	public void relationalExpress2() {
		System.out.println("enter relationalExpress2: " + this.token);
		if (this.isRelationalOp(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.relationalExpress();
		}
		return;
	}

	// shiftExpress - mahar
	public void shiftExpress() {
		this.additiveExpress();
		this.shiftExpress2();
	}

	// shiftExpress2 - mahar
	public void shiftExpress2() {
		System.out.println("enter shiftExpress2: " + this.token);
		if (this.isShiftOp(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.shiftExpress();
		}
		return;
	}

	// bitwiseAndExpress - mahar
	public void bitwiseAndExpress() {
		this.equalityExpress();
		this.bitwiseAndExpress2();
	}

	// bitwiseAndExpress2 - mahar
	public void bitwiseAndExpress2() {
		System.out.println("enter bitwiseAndExpress2: " + this.token);
		if (this.isBitwiseAnd(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.bitwiseAndExpress();
		}
		return;
	}

	// bitwiseOrExpress - mahar
	public void bitwiseOrExpress() {
		this.bitwiseAndExpress();
		this.bitwiseOrExpress2();
	}

	// bitwiseOrExpress2 - mahar
	public void bitwiseOrExpress2() {
		System.out.println("enter bitwiseOrExpress2: " + this.token);
		if (this.isBitwiseOr(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.bitwiseOrExpress();
		}
		return;
	}

	// bitwiseXorExpress - mahar
	public void bitwiseXorExpress() {
		this.bitwiseAndExpress();
		this.bitwiseXorExpress2();
	}

	// bitwiseXorExpress2 - mahar
	public void bitwiseXorExpress2() {
		System.out.println("enter bitwiseXorExpress2: " + this.token);
		if (this.isBitwiseXor(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.bitwiseXorExpress();
		}
		return;
	}

	// lowBooleanOrExpress - mahar
	public void lowBooleanOrExpress() {
		this.booleanXorExpress();
		this.lowBooleanOrExpress2();
	}

	// lowBooleanOrExpress2 - mahar
	public void lowBooleanOrExpress2() {
		System.out.println("enter lowBooleanOrExpress2: " + this.token);
		if (this.isLowBooleanOr(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.lowBooleanOrExpress();
		}
		return;
	}

	// highBooleanOrExpress - mahar
	public void highBooleanOrExpress() {
		this.highBoolanAndExpress();
		this.highBooleanOrExpress2();
	}

	// highBooleanOrExpress2 - mahar
	public void highBooleanOrExpress2() {
		System.out.println("enter highBooleanOrExpress2: " + this.token);
		if (this.isHighBooleanOr(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.highBooleanOrExpress();
		}
		return;
	}

	// lowBoolanAndExpress - mahar
	public void lowBoolanAndExpress() {

	}

	// lowBoolanAndExpress2 - mahar
	public void lowBoolanAndExpress2() {

	}

	public void highBoolanAndExpress() {

	}

	public void booleanXorExpress() {

	}

	// prefixExpress - mahar
	public void prefixExpress() {
		this.suffixExpress();
		this.prefixExpress2();
		return;
	}

	// prefixExpress2 - mahar
	public void prefixExpress2() {
		System.out.println("enter prefixExpress2: " + this.token);
		if (this.isPrefixOp(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.prefixExpress();
		}
		return;
	}

	// prefixExpress - mahar
	public void suffixExpress() {
		this.basicExpression();
		this.suffixExpress2();
		return;
	}

	// prefixExpress2 - mahar
	public void suffixExpress2() {
		System.out.println("enter suffixExpress2: " + this.token);
		if (this.isSuffixOp(this.token)) {
			this.token = this.nextToken();
			System.out.println("go to next token: " + this.token);
			this.suffixExpress();
		}
		return;
	}

	// basicExpression - mahar
	public void basicExpression() {
		// cek nilai first dari basicExpression

		if (this.token.charAt(0) == '$') { // cek variabel
			if (this.token.equals("$this")) { // cek member class
				this.memberClassCall();
				return;
			}
			// masuk fungsi variableName()
			System.out.println("cek simpleVarName");
			this.simpleVarName();
		} else if (this.isNumber()) { // cek token adalah angka
			this.number();
		} else if (this.isString()) { // cek token adalah string
			this.string();
		} else if (this.isIdentifier()) { // cek token adalah identifier
			this.functionCallExpression();
		}

	}

	// function functionCallStatement - mahar
	public void functionCallStatement() {
		this.functionCallExpression();
		if (this.token.equals(";")) {
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!! functionCallStatement");
		System.exit(1);
	}

	// function call expression - mahar
	public void functionCallExpression() {
		// check the current token is identifier (function name) or not
		this.functionName();
		if (this.token.equals("(")) {
			this.token = this.nextToken();
			// call function parameterlist()
			this.parameterList();
			if (this.token.equals(")")) {
				return;
			}
		}

		System.out.println("Syntax error!! functionCallExpression");
		System.exit(1);
	}

	// function functionName() - mahar
	public void functionName() {
		if (this.isIdentifier()) {
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!! functionName");
		System.exit(1);
	}

	// directives - mahar
	public void directives() {
		if (this.token.equals("ticks")) {
			this.token = this.nextToken();
			if (this.token.equals("=")) {
				if (isDigit()) {
					this.token = this.nextToken();
					return;
				}
			}
		}
		System.out.println("Syntax error!! directives");
		System.exit(1);
	}

	// simpleVarName - mahar
	public void simpleVarName() {
		// this.token="$var";
		if (this.token.charAt(0) == '$') {
			// get the identifier name after '$' character
			this.token = this.token.substring(1);
			// check if it is identifier or not
			if (this.isIdentifier()) {
				this.token = this.nextToken();
				return;
			}
			System.out.println("Syntax error!! simpleVarName");
			System.exit(1);
		}
	}

	public void string() {
		if (this.isString()) {
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!! string");
		System.exit(1);
	}

	public void number() {
		if (this.isNumber()) {
			this.token = this.nextToken();
			return;
		}
		System.out.println("Syntax error!! number");
		System.exit(1);
	}

	// check is identifier - mahar
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

	// check is String - mahar
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

	// check is number - mahar
	public boolean isNumber() {
		if (isDigit()) {
			this.token = this.nextToken();
			if (this.token.equals(".")) {
				this.token = this.nextToken();
				if (isDigit()) {
					return true;
				} else {
					System.out.println("syntax error!! isNumber");
					return false;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	// check is String - mahar
	public boolean isDigit() {
		if ((int) this.token.charAt(0) >= 48
				&& (int) this.token.charAt(0) <= 57) {
			// this.token=this.nextToken();
			return true;
		}
		return false;
	}

	/* method dibawah ini digunakan untuk melakukan pengecekan pada operator */
	// is prefix operator - mahar
	public boolean isPrefixOp(String op) {
		if (isCastingOp(op) || isSuffixOp(op) || isOtherOp(op)) {
			return true;
		}
		return false;
	}

	// is other operator - mahar
	public boolean isOtherOp(String op) {
		for (int i = 0; i < this.term.otherOp.length; i++) {
			if (this.term.otherOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is casting operator - mahar
	public boolean isCastingOp(String op) {
		for (int i = 0; i < this.term.castOp.length; i++) {
			if (this.term.castOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is suffix operator - mahar
	public boolean isSuffixOp(String op) {
		if (isIncrementOp(op) || isDecrementOp(op)) {
			return true;
		}
		return false;
	}

	// is suffix operator - mahar
	public boolean isIncrementOp(String op) {
		for (int i = 0; i < this.term.incrOp.length; i++) {
			if (this.term.incrOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is suffix operator - mahar
	public boolean isDecrementOp(String op) {
		for (int i = 0; i < this.term.dcrOp.length; i++) {
			if (this.term.dcrOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is assignment operator - mahar
	public boolean isAssignmentOp(String op) {
		for (int i = 0; i < this.term.assignOp.length; i++) {
			if (this.term.assignOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is comparison operator - mahar
	public boolean isArithmeticOp(String op) {
		if (isAddOp(op) || isMultiOp(op)) {
			return true;
		}
		return false;
	}

	// is additive operator - mahar
	public boolean isAddOp(String op) {
		for (int i = 0; i < this.term.addOp.length; i++) {
			if (this.term.addOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is multiplicative operator - mahar
	public boolean isMultiOp(String op) {
		for (int i = 0; i < this.term.multiOp.length; i++) {
			if (this.term.multiOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is bitwise operator - mahar
	public boolean isBitwiseOp(String op) {
		for (int i = 0; i < this.term.bitwiseOp.length; i++) {
			if (this.term.bitwiseOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is shift operator - mahar
	public boolean isShiftOp(String op) {
		for (int i = 0; i < this.term.shiftOp.length; i++) {
			if (this.term.shiftOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is comparison operator - mahar
	public boolean isComparisonOp(String op) {
		if (isEqualityOp(op) || isRelationalOp(op)) {
			return true;
		}

		return false;
	}

	// is equality operator - mahar
	public boolean isEqualityOp(String op) {
		for (int i = 0; i < this.term.equalOp.length; i++) {
			if (this.term.equalOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is relational operator - mahar
	public boolean isRelationalOp(String op) {
		for (int i = 0; i < this.term.relationOp.length; i++) {
			if (this.term.relationOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is logic operator - mahar
	public boolean isLogicOp(String op) {
		for (int i = 0; i < this.term.logicOp.length; i++) {
			if (this.term.logicOp[i].equals(op)) {
				return true;
			}
		}
		return false;
	}

	// is bitwise and - mahar
	public boolean isBitwiseAnd(String op) {
		if (op.equals("&")) {
			return true;
		}
		return false;
	}

	// is bitwise or - mahar
	public boolean isBitwiseOr(String op) {
		if (op.equals("|")) {
			return true;
		}
		return false;
	}

	// is bitwise xor - mahar
	public boolean isBitwiseXor(String op) {
		if (op.equals("^")) {
			return true;
		}
		return false;
	}

	// is low boolean or - mahar
	public boolean isLowBooleanOr(String op) {
		if (op.equals("or")) {
			return true;
		}
		return false;
	}

	// is high boolean or - mahar
	public boolean isHighBooleanOr(String op) {
		if (op.equals("||")) {
			return true;
		}
		return false;
	}

	// is low boolean and - mahar
	public boolean isLowBooleanAnd(String op) {
		if (op.equals("and")) {
			return true;
		}
		return false;
	}

	// is high boolean or - mahar
	public boolean isHighBooleanAnd(String op) {
		if (op.equals("&&")) {
			return true;
		}
		return false;
	}

	// is low boolean or - mahar
	public boolean isBooleanXor(String op) {
		if (op.equals("xor")) {
			return true;
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
	public String getTokenAt(int idx) {
		return this.scriptTokens.get(idx).toString();
	}

	public int getCurrentIndex() {
		return this.index;
	}

	public boolean searchTerminal(String[] listTerm, String term) {
		for (int i = 0; i < listTerm.length; i++) {
			if (listTerm[i].equals(term)) {
				return true;
			}
		}
		return false;
	}
}