package de.idrinth.load.validator;

public final class AssertionFailed extends Exception
{
    private final String label;
    public AssertionFailed(String label, String error)
    {
        super(error);
        this.label = label;
    }
    public AssertionFailed(String label, Throwable cause) {
        super(cause.getMessage(), cause);
        this.label = label;
    }
    @Override
    public String toString()
    {
        return label + ": "+getMessage();
    }
}
