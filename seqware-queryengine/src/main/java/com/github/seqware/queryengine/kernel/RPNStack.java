package com.github.seqware.queryengine.kernel;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.system.exporters.QueryVCFDumper;
import java.io.Serializable;
import java.util.*;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.log4j.Logger;

/**
 * A stack of arguments and operations in Reverse Polish Notation (RPN,
 * http://en.wikipedia.org/wiki/Reverse_Polish_notation).
 *
 * @author jbaran
 */
public class RPNStack implements Serializable {

    private List<Object> stack;
    private Map<Parameter, Integer> parameters = new HashMap<Parameter, Integer>();

    /**
     * Operations for combining query constraints.
     */
    public enum Operation {

        /**
         * Takes two truth value arguments that both need to evaluate to true
         * for the result to be true.
         */
        AND,
        /**
         * Takes two truth value arguments where one of it needs to evaluate to
         * true for the result to be true.
         */
        OR,
        /**
         * Takes one truth value argument and inverts its value.
         */
        NOT,
        /**
         * Takes two arguments of any kind and compares whether they are equal.
         */
        EQUAL,
        /**
         * Takes two numeric arguments and compares whether one is strictly
         * lesser than.
         */
        LESSERTHAN,
        /**
         * Takes two numeric arguments and compares whether one is lesser than
         * or equal to.
         */
        LESSERTHANOREQ,
        /**
         * Takes two numeric arguments and compares whether one is strictly
         * greater than.
         */
        GREATERTHAN,
        /**
         * Takes two numeric arguments and compares whether one is strictly
         * greater than or equal to.
         */
        GREATERTHANOREQ
    }

    public static class Constant implements Serializable {

        private final Object constant;

        public Constant(Object constant) {
            this.constant = constant;
        }

        public Object getValue() {
            return this.constant;
        }
    }

    public abstract static class Parameter implements Serializable {

        private String name;

        protected Parameter(String name) {
            this.name = name;
        }

        public final String getName() {
            return name;
        }

        public String getUniqueName() {
            return this.getClass().getName() + "://" + name;
        }

        @Override
        public int hashCode() {
            return this.getUniqueName().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Parameter)) {
                return false;
            }

            return this.getUniqueName().equals(((Parameter) o).getUniqueName());
        }
    }

    /**
     * Represents attributes of features that are passed as parameter.
     */
    public static class FeatureAttribute extends Parameter {

        public FeatureAttribute(String name) {
            super(name);
        }
    }

    /**
     * Represents tags whose occurrence is probed.
     */
    public static class TagOccurrence extends Parameter {

        private String tagSetRowKey;

        public TagOccurrence(String tagSetRowKey, String key) {
            super(key);
            this.tagSetRowKey = tagSetRowKey;
        }

        public String getTagSetRowKey() {
            return tagSetRowKey;
        }
    }

    /**
     * Represents tags whose value is retrieved
     */
    public static class TagValue extends Parameter {

        private String tagSetRowKey;
        private Object value;

        public TagValue(String tagSetRowKey, String key) {
            super(key);
            this.tagSetRowKey = tagSetRowKey;
        }

        public String getTagSetRowKey() {
            return tagSetRowKey;
        }

        public Object getValue() {
            return this.value;
        }
    }

    /**
     * Represents tags whose occurrence is tested -- including the tags
     * children!
     */
    public static class TagHierarchicalOccurrence extends Parameter {

        private String tagSetRowKey;

        public TagHierarchicalOccurrence(String tagSetRowKey, String name) {
            super(name);
            this.tagSetRowKey = tagSetRowKey;
        }

        public String getTagSetRowKey() {
            return this.tagSetRowKey;
        }
    }

    /**
     * Represents tag/value pairs that should be present.
     */
    public static class TagValuePresence extends Parameter {

        private String tagSetRowKey;
        private Tag.ValueType type;
        private Object value;

        public TagValuePresence(String tagSetRowKey, String name, Tag.ValueType type, Object value) {
            super(name);
            this.tagSetRowKey = tagSetRowKey;
            this.type = type;
            this.value = value;
        }

        public String getTagSetRowKey() {
            return tagSetRowKey;
        }

        @Override
        public final String getUniqueName() {
            return this.getClass().getName() + "://" + this.getName() + "#" + getValue();
        }

        public Tag.ValueType getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }
    }

    public RPNStack(Object... arguments) {
        this(Arrays.asList(arguments));
    }

    public RPNStack(List arguments) {
        this.stack = arguments;

        for (int i = 0; i < arguments.size(); i++) {
            Object argument = arguments.get(i);

            if (argument instanceof Constant) {
                this.stack.set(i, ((Constant) argument).getValue());
            } else if (argument instanceof Operation) {
                // Do nothing.
            } else if (argument instanceof Parameter) {
                this.parameters.put((Parameter) argument, i);
            } else {
                throw new UnsupportedOperationException("An RPNStack can only be populated with Constant, Parameter, or Operation instances. You provided a " + argument.getClass().getName());
            }
        }
    }

    /**
     * Sets a parameter to a concrete value.
     *
     * @param parameter Parameter whose value is to be set.
     * @param value Value of the parameter.
     */
    public void setParameter(Parameter parameter, Object value) {
        this.stack.set(this.parameters.get(parameter), value);
    }

    /**
     * Returns the parameters of the argument stack, i.e., everything that is
     * not a constant and considered to be a variable.
     *
     * @return
     */
    public Set<Parameter> getParameters() {
        return this.parameters.keySet();
    }

    /**
     * RPN evaluation.
     *
     * @return Evaluation result when interpreting the stack contents in RPN.
     */
    public Object evaluate() {
        List<Object> rpnStack = new LinkedList<Object>(this.stack);
        List<Object> operationArguments = new LinkedList<Object>();

        while (!rpnStack.isEmpty()) {
            Object object = rpnStack.remove(0);

            if (object instanceof Operation) {
                Operation op = (Operation) object;
                Object a, b;

                switch (op) {
                    case AND:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.and(a, b));
                        break;
                    case OR:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.or(a, b));
                        break;
                    case NOT:
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.not(a));
                        break;
                    case EQUAL:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.equal(a, b));
                        break;
                    case LESSERTHAN:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.lesser(a, b, false));
                        break;
                    case LESSERTHANOREQ:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.lesser(a, b, true));
                        break;
                    case GREATERTHAN:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.lesser(b, a, false));
                        break;
                    case GREATERTHANOREQ:
                        b = operationArguments.remove(0);
                        a = operationArguments.remove(0);
                        operationArguments.add(0, this.lesser(b, a, true));
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported operation: " + op);
                }
            } else {
                operationArguments.add(0, object);
            }
        }

        return operationArguments.remove(0);
    }

    /**
     * Carries out a Boolean AND.
     */
    private boolean and(Object a, Object b) {
        if (a instanceof Boolean && b instanceof Boolean) {
            return ((Boolean) a) && ((Boolean) b);
        }
        return false;
    }

    /**
     * Ugly and inefficient, just testing this out to get the tests to compile
     * Carries out a numeric GREATERTHAN.
     */
    private boolean lesser(Object a, Object b, boolean allowEq) {
        // if one of the values in a comparison is null, return false
        if (a == null || b == null){
            return false;
        }
        
        if (a instanceof Long || b instanceof Long) {
            if (allowEq) {
                return (Long.valueOf(a.toString())).longValue() <= (Long.valueOf(b.toString())).longValue();
            }
            return (Long.valueOf(a.toString())).longValue() < (Long.valueOf(b.toString())).longValue();
        }
        if (a instanceof Float || b instanceof Float) {
            if (allowEq) {
                return (Float.valueOf(a.toString())).floatValue() <= (Float.valueOf(b.toString())).floatValue();
            }
            return (Float.valueOf(a.toString())).floatValue() < (Float.valueOf(b.toString())).floatValue();
        }
        if (a instanceof Integer || b instanceof Integer) {
            if (allowEq) {
                return (Integer.valueOf(a.toString())).intValue() <= (Integer.valueOf(b.toString())).intValue();
            }
            return (Integer.valueOf(a.toString())).intValue() < (Integer.valueOf(b.toString())).intValue();
        }
        if (a instanceof Double || b instanceof Double) {
            if (allowEq) {
                return (Double.valueOf(a.toString())).doubleValue() <= (Double.valueOf(b.toString())).doubleValue();
            }
            return (Double.valueOf(a.toString())).doubleValue() < (Double.valueOf(b.toString())).doubleValue();
        }
        throw new UnsupportedOperationException("This comparison has yet to be implemented. Sorry.");
    }

    /**
     * Carries out a Boolean OR.
     */
    private boolean or(Object a, Object b) {
        if (a instanceof Boolean && b instanceof Boolean) {
            return ((Boolean) a) || ((Boolean) b);
        }

        return false;
    }

    /**
     * Carries out a Boolean NOT.
     */
    private boolean not(Object a) {
        if (a instanceof Boolean) {
            return !((Boolean) a);
        }

        return false;
    }

    /**
     * Carries out an exact match.
     */
    private boolean equal(Object a, Object b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.equals(b);
    }

    /**
     * Compiles the given query string that adheres to the syntax of
     * SeqWareQueryLanguage.g and returns a RPNStack object representing the
     * query.
     *
     * @param query A query string.
     * @return An RPNStack that represents the query.
     */
    public static RPNStack compileQuery(String query) throws RecognitionException {
        Logger.getLogger(RPNStack.class.getName()).info("Compile query: " + query);
        SeqWareQueryLanguageLexer lexer = new SeqWareQueryLanguageLexer(new ANTLRStringStream(query));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SeqWareQueryLanguageParser parser = new SeqWareQueryLanguageParser(tokenStream);
        SeqWareQueryLanguageParser.query_return parsedInput = parser.query();
        Tree tree = (CommonTree) parsedInput.getTree();

        List<Object> arguments = new ArrayList<Object>();
        abstractSyntaxTreeTraversal(tree, arguments);

        return new RPNStack(arguments);
    }

    /**
     * Traverses an abstract syntax tree of a query and returns the arguments
     * for a RPNStack.
     *
     * @param node Node within the abstract syntax tree -- initially the root
     * node.
     * @param arguments List of arguments that can be passed to an RPNStack --
     * initially empty.
     */
    private static void abstractSyntaxTreeTraversal(Tree node, List<Object> arguments) {
        for (int i = 0; i < node.getChildCount(); i++) {
            abstractSyntaxTreeTraversal(node.getChild(i), arguments);
        }

        String text = node.getText();
        switch (node.getType()) {
            // Boolean operators:
            case SeqWareQueryLanguageParser.AND:
                arguments.add(Operation.AND);
                break;
            case SeqWareQueryLanguageParser.OR:
                arguments.add(Operation.OR);
                break;

            // Other binary operators:
            case SeqWareQueryLanguageParser.EQUALS:
                arguments.add(Operation.EQUAL);
                break;
            case SeqWareQueryLanguageParser.NOTEQUALS:
                arguments.add(Operation.EQUAL);
                arguments.add(Operation.NOT);
                break;

            // singular negation
            case SeqWareQueryLanguageParser.NOT:
                arguments.add(Operation.NOT);
                break;

            // Constants:
            case SeqWareQueryLanguageParser.FLOAT:
                arguments.add(new Constant(Float.parseFloat(text)));
                break;
            case SeqWareQueryLanguageParser.INT:
                arguments.add(new Constant(Integer.parseInt(text)));
                break;
            case SeqWareQueryLanguageParser.STRING:
                arguments.add(new Constant(text.replaceFirst("^\"", "").replaceFirst("\"$", "")));
                break;
            case SeqWareQueryLanguageParser.NAMED_CONSTANT:
                if (text.equals("STRAND_UNKNOWN")) {
                    arguments.add(new Constant(Feature.Strand.UNKNOWN));
                } else if (text.equals("NOT_STRANDED")) {
                    arguments.add(new Constant(Feature.Strand.NOT_STRANDED));
                } else if (text.equals("NEGATIVE_STRAND")) {
                    arguments.add(new Constant(Feature.Strand.NEGATIVE));
                } else if (text.equals("POSITIVE_STRAND")) {
                    arguments.add(new Constant(Feature.Strand.POSITIVE));
                } else {
                    throw new IllegalArgumentException("The following constant is not handled by the parser: " + text);
                }
                break;

            // Variables:
            case SeqWareQueryLanguageParser.ID:
                arguments.add(new FeatureAttribute(text));
                break;

            case SeqWareQueryLanguageParser.NAMED_FUNCTION: {
                Constant functionKey = (Constant) arguments.remove(arguments.size() - 1);
                Constant functionTagSet = (Constant) arguments.remove(arguments.size() - 1);

                if (text.equals("tagValue")) {
                    arguments.add(new TagValue((String) functionTagSet.getValue(), (String) functionKey.getValue()));
                } else {
                    throw new IllegalArgumentException("A two parameter function call of the following name is not known: " + text);
                }
            }
            break;

            // Functions:
            case SeqWareQueryLanguageParser.NAMED_TWO_PARAM_PREDICATE: {
                Constant functionKey = (Constant) arguments.remove(arguments.size() - 1);
                Constant functionTagSet = (Constant) arguments.remove(arguments.size() - 1);

                if (text.equals("tagOccurrence")) {
                    arguments.add(new TagOccurrence((String) functionTagSet.getValue(), (String) functionKey.getValue()));
                } else if (text.equals("tagHierarchicalOccurrence")) {
                    throw new IllegalArgumentException("A tagHierarchialOccurence is not supported yet: " + text);
                    // TODO I don't know how to get the row key.
                    // arguments.add(new TagHierarchicalOccurrence(Compression.getSequenceOntologyAccessionSurrogate((String)functionArgument)), ...);
                } else {
                    throw new IllegalArgumentException("A two parameter predicate call of the following name is not known: " + text);
                }
            }
            break;

            // Functions:
            case SeqWareQueryLanguageParser.NAMED_THREE_PARAM_PREDICATE: {
                Constant functionValue = (Constant) arguments.remove(arguments.size() - 1);
                Constant functionKey = (Constant) arguments.remove(arguments.size() - 1);
                Constant functionTagSet = (Constant) arguments.remove(arguments.size() - 1);

                if (text.equals("tagValuePresence")) {
                    // TODO: we can ignore type for now until we measure efficiency loss
                    arguments.add(new TagValuePresence((String) functionTagSet.getValue(), (String) functionKey.getValue(), null, functionValue.getValue()));
                } else {
                    throw new IllegalArgumentException("A three parameter predicate call of the following name is not known: " + text);
                }
            }
            break;

            // Not implemented (yet):
            case SeqWareQueryLanguageParser.GT:
                arguments.add(Operation.GREATERTHAN);
                break;
            case SeqWareQueryLanguageParser.GTEQ:
                arguments.add(Operation.GREATERTHANOREQ);
                break;
            case SeqWareQueryLanguageParser.LT:
                arguments.add(Operation.LESSERTHAN);
                break;
            case SeqWareQueryLanguageParser.LTEQ:
                arguments.add(Operation.LESSERTHANOREQ);
                break;
            default:
                break;
        }
    }
}
