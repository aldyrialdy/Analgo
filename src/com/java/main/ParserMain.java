package com.java.main;

import com.java.phpparser.*;
import java.util.*;

public class ParserMain {
	
	public static void main(String args[]) {
		PhpParser parser = new PhpParser("index2.php");
		parser.program();
		
	}

}
