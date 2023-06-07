package shared.core.exceptions;

/**
 *Thrown out if arguments limits are not allowed.
 */
public class ArgumentLimitsException extends Exception{
    public ArgumentLimitsException(int downLimit) {
        super(String.format("Значение ID должно быть >%s", downLimit));
    }
    public ArgumentLimitsException(int downLimit, int upLimit) {
        super(String.format("Значение ID должно быть равно %s< и <%s", downLimit, upLimit));
    }
}
