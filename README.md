
# Event Driven Micro Services Architecture with CQRS and Event Sourcing - Use case With Axon

### Objectif : 

Créer une application permettant de gérer des comptes bancaires conformément aux motifs CQRS (Command Query Responsibility Segregation) et Event Sourcing en utilisant les frameworks AXON et Spring Boot , tout en permettant de : 
##### - Ajouter un compte
##### - Activer un compte
##### - Créditer un compte
##### - Débiter un compte
##### - Consulter un compte
##### - Consulter les comptes
##### - Consulter les opérations d'un compte
##### - Suivre en temps réel l'état d'un compte

### Event driven architecture cqrs and event sourcing

L'architecture orientée événements, le modèle CQRS (Command Query Responsibility Segregation) et la gestion des événements sont des concepts fondamentaux dans le domaine du développement logiciel moderne. L'Event-Driven Architecture (EDA) repose sur le principe de communication asynchrone entre les différents composants d'un système, où chaque action génère un événement qui est capturé et traité par des écouteurs appropriés. Cela favorise la scalabilité, la résilience et la flexibilité des applications, permettant une réactivité accrue aux changements de l'environnement. Le modèle CQRS, quant à lui, préconise la séparation des responsabilités de lecture et d'écriture, optimisant ainsi les performances et la maintenabilité du système. Enfin, l'Event Sourcing implique la persistance de l'état d'une application sous la forme d'une séquence d'événements plutôt que d'une simple représentation d'état actuel. Cette approche offre une traçabilité complète de l'historique des actions, facilitant le débogage, la compréhension du comportement et la gestion des transactions. En combinant ces trois concepts, les développeurs peuvent concevoir des systèmes logiciels robustes, évolutifs et réactifs, répondant aux défis complexes des environnements informatiques contemporains.

### Application
<img width="668" alt="app" src="https://github.com/Taghla-Ladkhan/Event-Driven-Micro-Services-Architecture/assets/101521160/fa069103-9965-4483-87b1-1a83379c6375">



### Création des ' Commands and Events '

#### BaseCommand 

```bash
public abstract class BaseCommand<IDType> {
    @TargetAggregateIdentifier
    @Getter // because the commands are immutable objects

    private IDType id;

    public BaseCommand(IDType id) {
        this.id = id;
    }   
}
```

#### CreateAccountCommand
```bash
public class CreateAccountCommand extends BaseCommand<String>{

    private double initialBalance;
    private String currency;

    public CreateAccountCommand(String id, double initialBalance, String currency) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
    }

}
```
#### DebitAccountCommand
```bash
public class DebitAccountCommand extends BaseCommand<String>{

    private double amount;
    private String currency;

    public DebitAccountCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
```
#### CreditAccountCommand
```bash
public class CreditAccountCommand extends BaseCommand<String>{
    private double amount;
    private String currency;

    public CreditAccountCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
```

#### Commands Controllers
```bash
@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {

    private CommandGateway commandGateway;

    @RequestMapping("/create")
    public CompletableFuture<String> createAccount(@RequestBody CreatAccountRequestDTO request){
        CompletableFuture<String> createAccountCommandResponse = commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getInitialBalance(),
                request.getCurrency()
        ));

        return createAccountCommandResponse;
    }
}
```
#### Events 
Dans le package packages/events, nous allons travailler avec la même logique que dans le package Commands mais avec quelques modifications (des objets simples pas des annotations). 
Ce package contient les classes suivantes :
#### BaseEvent 
```bash
public abstract class BaseEvent<EventId> {
    @Getter
    private EventId id;

    public BaseEvent(EventId id){
        this.id = id;
    }

}
```
#### AccountCreatedEvent
```bash
public class AccountCreatedEvent extends BaseEvent<String>{

    @Getter
    private double initialBalance;
    @Getter
    private String currency;

    public AccountCreatedEvent(String id, double initialBalance, String currency) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
    }
}

```
#### AccountCreditedEvent
```bash
public class AccountCreditedEvent extends BaseEvent<String>{
    private double amount;
    private String currency;
    public AccountCreditedEvent(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
```
#### AccountDebitedEvent
```bash
public class AccountDebitedEvent extends BaseEvent<String>{
    private double amount;
    private String currency;
    public AccountDebitedEvent(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
```

#### Properties
![image](https://github.com/Taghla-Ladkhan/Event-Driven-Micro-Services-Architecture/assets/101521160/13dba9e2-978f-4160-b175-44c41df410fb)




#### Base de données
![image](https://github.com/Taghla-Ladkhan/Event-Driven-Micro-Services-Architecture/assets/101521160/66a4560f-09e4-4952-bbd1-1e8e4b08297d)




 #### Test
 ![pos](https://github.com/Taghla-Ladkhan/Event-Driven-Micro-Services-Architecture/assets/101521160/91b100bd-bd27-4043-984f-43bf6b827adc)


#### Aggregate
```bash
@Aggregate
public class AccountAggregate {

    @AggregateIdentifier // id is  presented targetAggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;

    private AccountStatus status;
}

```
C'est obligatoire d'avoir un constructeur sans paramètres. Par la suite on  ajoute dans cette class un handler qui va être éxécuté au moment de la création du compte. 

#### La fonction de décision

```bash
    @CommandHandler // subscribe sur le bus de commande
    public AccountAggregate(CreateAccountCommand command) {
        if(command.getInitialBalance()<0){
            throw new RuntimeException("Impossible ....");
        }
        // ON
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                command.getCurrency()
        ));
        // Axon va charger de l'ajouter dans la base de donnees.
    }
```
#### Query 
##### Operation

```bash
package me.elaamiri.accountcqrseventsourcing.query.entities;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date date;
    private double amount;

    @Enumerated(EnumType.STRING)
    private OperationType type;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
```
##### Account
```bash
package me.elaamiri.accountcqrseventsourcing.query.entities;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Account {
    @Id
    private String id;
    private String currency;
    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @OneToMany(mappedBy = "account")
    private Collection<Operation> operations;
}

```

    
