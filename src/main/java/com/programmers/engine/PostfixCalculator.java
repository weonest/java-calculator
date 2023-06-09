package com.programmers.engine;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PostfixCalculator {

    StringBuilder postFix = new StringBuilder();

    //OOP를 잘 지킨걸까?
    public double calculate(String postfix) {
        Stack<Double> stack = new Stack<>();
        char cur = ' ';
        double value1, value2;

        for (int i = 0; i < postfix.length(); i++) {
            cur = postfix.charAt(i);
            if (Character.isDigit(cur)) {
                stack.push(Double.valueOf(cur) - 48);
            } else {
                value2 = Double.valueOf(stack.pop());
                value1 = Double.valueOf(stack.pop());

                switch (cur) {
                    // 스택에서 거꾸로 뽑기 때문에 value2를 먼저
                    case '+':
                        stack.push(value1 + value2);
                        break;
                    case '-':
                        stack.push(value1 - value2);
                        break;
                    case '*':
                        stack.push(value1 * value2);
                        break;
                    case '/':
                        stack.push(value1 / value2);
                        break;
                }
            }
        }
        return stack.pop();
    }


    // 메서드를 분리해야 하는지?
    public double infixToPostfix(String infix) {
        char cur = ' ';
        Stack<Character> opStack = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            cur = infix.charAt(i);

            if (Character.isDigit(cur)) {
                postFix.append(cur);
            } else if (opStack.isEmpty()) {
                opStack.push(cur);
            } else {
                if (cur == '(') {
                    opStack.push(cur);
                    continue;
                }

                if (cur == ')') {
                    boolean flag = true;
                    while (flag) {
                        if (!test(opStack, (Character c) -> c == '(', opStack.pop())) {
                            flag = false;
                        }
                    }
                    continue;
                }

                if (compareOperator(opStack.peek(), cur ) > 0) {
                    opStack.push(cur);
                } else {
                    test(opStack, (Integer data) -> data > 0, compareOperator(opStack.peek(), cur)); // peek으로 확인 후 검사
                    opStack.push(cur);
                }
            }
        }
        if (!opStack.isEmpty()) stackPop(opStack, postFix, ' ');

        return calculate(postFix.toString());
    }

    public void stackPop(Stack<Character> opStack, StringBuilder postFix, char oper) {
        while (!opStack.isEmpty()) {
            oper = opStack.pop();
            if (oper == '(') return;
            postFix.append(oper);
        }
    }

    public <T> boolean test(Stack<Character> opStack, Predicate<T> predicate, T data) {
        while (!opStack.isEmpty()) { //람다

            if (predicate.test(data)) {
                return false;
            }
            if (data instanceof Character) {
                postFix.append(data);
                return true;
            }
            postFix.append(opStack.pop());
        }
        return true;
    }


    public int getPriority(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '/':
            case '*':
                return 2;
            case '(':
            case ')':
                return 0;

            default:
                return -1; // 여기까지 도달 안함
        }
    }

    private int compareOperator(char stackOp, char curOp) {
        int stackOpPriority = getPriority(stackOp);
        int curOpPriority = getPriority(curOp);

        if (stackOpPriority < curOpPriority) {
            return 1;
        } else if (stackOpPriority == curOpPriority) {
            return 0;
        } else {
            return -1;
        }

    }


}
