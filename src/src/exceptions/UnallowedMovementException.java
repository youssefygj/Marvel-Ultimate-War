package exceptions;

public class UnallowedMovementException extends GameActionException{
    UnallowedMovementException(){super();}
    UnallowedMovementException(String s){super(s);}
}
