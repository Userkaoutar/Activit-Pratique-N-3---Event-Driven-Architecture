package ma.enset.cqrs.query.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.cqrs.commonapi.enums.OperationType;
import ma.enset.cqrs.commonapi.events.AccountActivatedEvent;
import ma.enset.cqrs.commonapi.events.AccountCreatedEvent;
import ma.enset.cqrs.commonapi.events.AccountCreditedEvent;
import ma.enset.cqrs.commonapi.events.AccountDebitedEvent;
import ma.enset.cqrs.commonapi.queries.GetAccountIdQuery;
import ma.enset.cqrs.commonapi.queries.GetAllAccountQuery;
import ma.enset.cqrs.query.entities.Account;
import ma.enset.cqrs.query.entities.Operation;
import ma.enset.cqrs.query.repository.AccountRepository;
import ma.enset.cqrs.query.repository.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional // lorsque l'état d'un événemenet est changer donc automatiquement, sera enregistrer
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    @EventHandler
    public void on(AccountCreatedEvent event){
        log.info("****************************");
        log.info("AccountCreatedEvent received");
        log.info("****************************");
        Account account=new Account();
        account.setId(event.getId());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());
        account.setBalance(event.getInitialBalance());
        account.setOperations(null);
      accountRepository.save(account);
    }


    @EventHandler
    public void on(AccountActivatedEvent event){
        log.info("****************************");
        log.info("AccountActivatedEvent received");
        log.info("****************************");
        Account account = accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event){
        log.info("****************************");
        log.info("AccountDebitedEvent received");
        log.info("****************************");
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation= new Operation();
        operation.setType(OperationType.DEBIT);
        operation.setAccount(account);
        operation.setAmount(event.getAmount());
        operation.setDate(new Date()); // a ne pas faire, ilfaut enregister au moment de l'enregistrement pas lecture de l'evenement.
        operationRepository.save(operation);
        account.setBalance(account.getBalance()-event.getAmount());
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountCreditedEvent event){
        log.info("****************************");
        log.info("AccountCreditedEvent received");
        log.info("****************************");
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation= new Operation();
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operation.setAmount(event.getAmount());
        operation.setDate(new Date()); // a ne pas faire, il faut enregister au moment de l'enregistrement pas lecture de l'evenement.
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> on(GetAllAccountQuery query){
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }
}
