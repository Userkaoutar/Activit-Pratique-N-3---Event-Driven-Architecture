package ma.enset.cqrs.commonapi.events;

import lombok.Getter;
import ma.enset.cqrs.commonapi.enums.AccountStatus;

@Getter
public class AccountCreatedEvent extends BaseEvent<String> {

    private double initialBalance;
    private String currency;
    private AccountStatus status;


    public AccountCreatedEvent(String id, double initialBalance, String currency, AccountStatus status) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
        this.status=status;
    }



}
