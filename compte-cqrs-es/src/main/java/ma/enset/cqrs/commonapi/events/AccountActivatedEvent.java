package ma.enset.cqrs.commonapi.events;


import lombok.Getter;
import ma.enset.cqrs.commonapi.enums.AccountStatus;

@Getter

public class AccountActivatedEvent extends  BaseEvent<String>{

    private AccountStatus status;
    public AccountActivatedEvent(String id, AccountStatus status){
        super(id);
        this.status=status;
    }
}
