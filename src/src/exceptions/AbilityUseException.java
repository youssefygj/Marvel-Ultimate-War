package exceptions;

public class AbilityUseException extends GameActionException {

    AbilityUseException() {
        super();
    }

    AbilityUseException(String s) {
        super(s);
    }
}
