package EmpireLisp;

/**
 * @author Tyrerexus
 * @date 11/22/17
 */
@SuppressWarnings({"JavaDoc", "Convert2Lambda"})
public abstract class ProcedureBinaryOperator<T1 extends Expression, T2 extends Expression> extends ExpressionPrimitive {
    private Class<T1> type1;
    private Class<T2> type2;

    ProcedureBinaryOperator(Class<T1> type1, Class<T2> type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    public abstract Expression operate(T1 arg1, T2 arg2);
    public abstract String getType1Name();
    public abstract String getType2Name();

    @SuppressWarnings("unchecked")
    @Override
    public void apply(Environment environment, Expression arguments, IEvalCallback callback) throws LispException {
        if (arguments instanceof ExpressionPair) {
            ExpressionPair firstPair = (ExpressionPair) arguments;

            firstPair.left.eval(environment, new IEvalCallback() {
                @Override
                public void evalCallback(Expression uncheckedValueA) throws LispException {
                    if (type1.isInstance(uncheckedValueA)) {
                        T1 valueA = (T1) uncheckedValueA;

                        if (firstPair.right instanceof ExpressionPair) {
                            ExpressionPair secondPair = (ExpressionPair) firstPair.right;

                            if (!secondPair.left.isNil() && secondPair.right.isNil()) {
                                secondPair.left.eval(environment, new IEvalCallback() {
                                    @Override
                                    public void evalCallback(Expression uncheckedValueB) throws LispException {
                                        if (type2.isInstance(uncheckedValueB)) {
                                            T2 valueB = (T2) uncheckedValueB;
                                            callback.evalCallback(operate(valueA, valueB));
                                        } else {
                                            throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType2Name(), uncheckedValueB.toString()));
                                        }
                                    }
                                });
                            }
                            else {
                                int argsReceived = ((ExpressionPair) arguments).toList().size();
                                throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedAmountOfArguments(2, argsReceived));
                            }
                        }
                        else {
                            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
                        }
                    }
                    else {
                        throw new LispException(LispException.ErrorType.ARITY_MISS_MATCH, LispException.ErrorMessages.expectedType(getType1Name(), uncheckedValueA.toString()));
                    }
                }
            });
        }
        else {
            throw new LispException(LispException.ErrorType.INVALID_ARGUMENTS, LispException.ErrorMessages.ARGUMENTS_MUST_BE_IN_LIST);
        }
    }
}
