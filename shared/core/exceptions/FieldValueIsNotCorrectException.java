package shared.core.exceptions;

/**
 *Thrown out if constructor arguments in the wrong format.
 */
public class FieldValueIsNotCorrectException extends RuntimeException{
    public FieldValueIsNotCorrectException(){
        super(String.format("Значение было в неправильном формате!"));
    }
}
