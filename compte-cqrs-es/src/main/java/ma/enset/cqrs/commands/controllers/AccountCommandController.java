package ma.enset.cqrs.commands.controllers;


import lombok.AllArgsConstructor;
import ma.enset.cqrs.commonapi.commands.CreateAccountCommand;
import ma.enset.cqrs.commonapi.commands.CreditAccountCommand;
import ma.enset.cqrs.commonapi.commands.DebitAccountCommand;
import ma.enset.cqrs.commonapi.dto.CreateAccountRequestDTO;

import ma.enset.cqrs.commonapi.dto.CreditAccountRequestDTO;
import ma.enset.cqrs.commonapi.dto.DebitedAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {

    private  CommandGateway commandGateway;
    private EventStore eventStore;

    /**Created Account**/
    @PostMapping(path = "/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO request) {
        return commandGateway.send(new CreateAccountCommand(
              UUID.randomUUID().toString(),
              request.getInitialBalance(),
              request.getCurrency()
      ));
    }
    /**credited Account**/
    @PutMapping(path = "/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO request) {
        return commandGateway.send(new CreditAccountCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
    }
    /**debit Account**/
    @PutMapping(path = "/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitedAccountRequestDTO request) {
        return commandGateway.send(new DebitAccountCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
    }
    @ExceptionHandler(Exception.class)
      public ResponseEntity<String> exceptionHandler(Exception exception) {
        ResponseEntity<String> entity = new ResponseEntity<>(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return entity;
    }
    @GetMapping("/eventStore/{accountId}")
    public Stream eventStore(@PathVariable String accountId) {
        return eventStore.readEvents(accountId).asStream();
    }

}
