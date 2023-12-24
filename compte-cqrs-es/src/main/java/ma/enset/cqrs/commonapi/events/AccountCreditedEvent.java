package ma.enset.cqrs.commonapi.events;

import lombok.Getter;

@Getter // objet immuable
public class AccountCreditedEvent extends BaseEvent<String>{
    private double amount;
    private String currency;
    public AccountCreditedEvent(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
