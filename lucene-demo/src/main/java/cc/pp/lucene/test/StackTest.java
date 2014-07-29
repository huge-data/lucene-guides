package cc.pp.lucene.test;

import java.util.Stack;

public class StackTest {

	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {

		Stack<String> stack = new Stack<String>();
		System.out.println("now the stack is " + isEmpty(stack));
		stack.push("11");
		stack.push("22");
		stack.push("33");
		stack.push("44");
		stack.push("55");
		System.out.println("now the stack is " + isEmpty(stack));
		System.out.println(stack.peek());
		System.out.println(stack.pop());
		System.out.println(stack.pop());
		System.out.println(stack.search("22"));
	}

	public static String isEmpty(Stack<String> stack) {
		return stack.empty() ? "empty" : "not empty";
	}
}
