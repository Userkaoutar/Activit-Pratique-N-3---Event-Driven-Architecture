package ma.enset.cqrs.query.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.cqrs.commonapi.queries.GetAccountIdQuery;
import ma.enset.cqrs.commonapi.queries.GetAllAccountQuery;
import ma.enset.cqrs.query.entities.Account;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/query/accounts")
@AllArgsConstructor
@Slf4j
public class AccountQueryController
{
    private QueryGateway queryGateway;

    @GetMapping(path = "/all")
    public List<Account> accountList(){
        return queryGateway.query(new GetAllAccountQuery(),
                ResponseTypes.multipleInstancesOf
                        ((Account.class)))
                .join();
    }
    @GetMapping(path = "/byId/{id}")
    public Account getAccount(@PathVariable String id){
        return queryGateway.query(new GetAccountIdQuery(id),
                        ResponseTypes.instanceOf(Account.class))
                .join();
    }

}
