package util.runtime;

/**
 * An empty argument that produces no tokens.
 */
public class EmptyProgramArgument<T> 
    extends EmptyCommandBuilder implements ProgramArgument<T> {

    @Override
    public ProgramArgument<T> set(T arg) {
        return this;
    }

}
