package com.programmers.engine;

import com.programmers.model.UserEquation;
import com.programmers.util.Operator;

import java.util.*;
import java.util.function.Predicate;

public class PostfixCalculator {
    private final Deque<Character> opStack = new ArrayDeque<>();

    private double calculate(List<String> postfix) {

        Deque<Double> deque = new ArrayDeque<>();
        String cur = "";
        double value1, value2;

        for (int i = 0; i < postfix.size(); i++) {
            cur = postfix.get(i);
            if (!Operator.contains(cur)) {
                deque.push(Double.valueOf(cur));
                continue;
            }
            value2 = Double.valueOf(deque.pop());
            value1 = Double.valueOf(deque.pop());
            deque.push(Operator.operate(cur, value1, value2));

        }
        return deque.pop();
    }

    public double infixToPostfix(UserEquation equation) {
        String infix = equation.getEquation();
        List<String> postfix = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < infix.length(); i++) {
            char cur = ' ';
            cur = infix.charAt(i);

            if (Character.isDigit(cur)) {
                sb.append(cur);
                if (i == infix.length() - 1) postfix.add(sb.toString());
                continue;
            }
            if (opStack.isEmpty()) {
                addAndReset(postfix, sb);
                opStack.push(cur);
                continue;
            }
            if (!opStack.isEmpty()) {
                addAndReset(postfix, sb);
                if (cur == '(') {
                    opStack.push(cur);
                    continue;
                }
                // 열린 소괄호를 만날때까지 모든 연산자 pop();
                if (cur == ')') {
                    popUntilMeetOpenBracket(postfix);
                    continue;
                }

                if (getOperatorPriority(opStack.peek(), cur) > 0) {
                    opStack.push(cur);
                    continue;
                }

                if (getOperatorPriority(opStack.peek(), cur) <= 0){
                    popLowerPriorityOperator(postfix, (Integer data) -> data > 0, getOperatorPriority(opStack.peek(), cur));
                    opStack.push(cur);
                }
            }
        }
        if (!opStack.isEmpty()) stackPop(postfix, ' ');

        return calculate(postfix);
    }

    private void addAndReset(List<String> postfix, StringBuilder sb) {
        if (sb.length() > 0) {
            postfix.add(sb.toString());
            sb.setLength(0);
        }
    }

    private void stackPop(List<String> list, char oper) {
        while (!opStack.isEmpty()) {
            oper = opStack.pop();
            if (oper == '(') return;
            list.add(String.valueOf(oper));
        }
    }

    private void popUntilMeetOpenBracket(List<String> postfix) {
        boolean flag = true;
        while (flag) {
            if (!popLowerPriorityOperator(postfix, (Character c) -> c == '(', opStack.peek())) {
                flag = false;
            }
        }
    }

    private <T> boolean popLowerPriorityOperator(List<String> list, Predicate<T> predicate, T data) {
        while (!opStack.isEmpty()) {
            if (predicate.test(data)) {
                opStack.pop();
                return false;
            }
            if (data instanceof Character) {
                list.add(String.valueOf(opStack.pop()));
                return true;
            }
            list.add(String.valueOf(opStack.pop()));
        }
        return false;
    }

    private int getOperatorPriority(char target, char cur) {
        int targetOperator = Operator.getPriority(target);
        int curOperator = Operator.getPriority(cur);

        return compareTo(targetOperator, curOperator);
    }

    private int compareTo(int targetOperator, int curOperator) {
        if (targetOperator < curOperator) {
            return 1;
        } else if (targetOperator == curOperator) {
            return 0;
        } else {
            return -1;
        }
    }

}
