package exceptions;

public class LeaderAbilityAlreadyUsedExcecption extends GameActionException {
    LeaderAbilityAlreadyUsedExcecption() {
        super();
    }

    LeaderAbilityAlreadyUsedExcecption(String s) {
        super(s);
    }
}
