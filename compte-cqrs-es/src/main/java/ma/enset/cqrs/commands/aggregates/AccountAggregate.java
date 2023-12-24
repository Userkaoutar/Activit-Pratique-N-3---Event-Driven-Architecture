package ma.enset.cqrs.commands.aggregates;

import ma.enset.cqrs.commonapi.commands.CreateAccountCommand;
import ma.enset.cqrs.commonapi.commands.CreditAccountCommand;
import ma.enset.cqrs.commonapi.commands.DebitAccountCommand;
import ma.enset.cqrs.commonapi.enums.AccountStatus;
import ma.enset.cqrs.commonapi.events.AccountActivatedEvent;
import ma.enset.cqrs.commonapi.events.AccountCreatedEvent;

import ma.enset.cqrs.commonapi.events.AccountCreditedEvent;
import ma.enset.cqrs.commonapi.events.AccountDebitedEvent;
import ma.enset.cqrs.commonapi.exceptions.AmountNegativeException;
import ma.enset.cqrs.commonapi.exceptions.BallanceNotSufficientException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;

    private AccountStatus status;
    public AccountAggregate() {
        // required by AXON
    }
    @CommandHandler // subscribe sur le bus de commande
    public AccountAggregate(CreateAccountCommand command) {
        if(command.getInitialBalance()<0){
            throw new RuntimeException("Impossible ....");
        }
        // ON
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                command.getCurrency(),
                AccountStatus.CREATED
        ));
        // Axon va charger de l'ajouter dans la base de donnees.
    }
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = event.getStatus();
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED
        ));
    }
    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.status=event.getStatus();
    }
    @CommandHandler
    public void handle(CreditAccountCommand command){
      if(command.getAmount()<0){
          throw new AmountNegativeException("Amount Should not be negative value");
      }
      else {
          AggregateLifecycle.apply(new AccountCreditedEvent(
                  command.getId(),
                  command.getAmount(),
                  command.getCurrency()
          ));
      }
    }
    @EventSourcingHandler // lorsque nous avons recu un event de type accountCreditedEvent va changer la balance de compte
    public void on(AccountCreditedEvent accountCreditedEvent){
        this.balance+=accountCreditedEvent.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command){
        if(command.getAmount()<0){
            throw new AmountNegativeException("Amount Should not be negative value");
        }
        if(this.balance<command.getAmount()){
            throw  new BallanceNotSufficientException("Balance not Sufficient Exception => "+ balance);
        }
        else {
            AggregateLifecycle.apply(new AccountDebitedEvent(
                    command.getId(),
                    command.getAmount(),
                    command.getCurrency()
            ));
        }
    }
    @EventSourcingHandler // lorsque nous avons recu un event de type accountCreditedEvent va changer la balance de compte
    public void on(AccountDebitedEvent accountDebitedEvent){
        this.balance-=accountDebitedEvent.getAmount();
    }
}
