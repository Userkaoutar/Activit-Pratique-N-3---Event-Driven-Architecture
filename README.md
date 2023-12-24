# Activit-Pratique-N-3---Event-Driven-Architecture

# Event Driven Micro Services Architecture with CQRS and Event Sourcing - Use case With Axon

### Architecture Micro Services
![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/c5d091c6-3e4f-4ccc-9a7d-feb4bdef6a86)


### Event Sourcing
- Event sourcing stocke chaque changement d'état en tant qu'événement immuable, formant un historique complet du système. Ces événements deviennent la source unique de vérité, permettant un audit facile, la relecture des changements de données et potentiellement même le retour aux états passés. 
image
![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/464a2b59-50cd-4fc2-b53c-61abf09f22b7)

### CQRS
C'est un modèle architectural qui sépare les opérations de lecture et d'écriture dans un système.

Dans une architecture traditionnelle, les opérations de lecture et d'écriture utilisent la même base de données. Cela peut entraîner des problèmes de performances et de cohérence, car les opérations de lecture peuvent interférer avec les opérations d'écriture.

CQRS résout ce problème en utilisant deux bases de données distinctes : une base de données pour les opérations de lecture et une base de données pour les opérations d'écriture. Cela permet de séparer les deux types d'opérations et d'améliorer les performances et la cohérence.
![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/416888a0-040e-40ed-bd07-9f9438373013)

### Application  : 

Créer une application permettant de gérer des comptes bancaires conformément aux motifs CQRS (Command Query Responsibility Segregation) et Event Sourcing en utilisant les frameworks AXON et Spring Boot , tout en permettant de : 
- Ajouter un compte
- Activer un compte
- Créditer un compte
- Débiter un compte
- Consulter un compte
- Consulter les comptes
- Consulter les opérations d'un compte
- Suivre en temps réel l'état d'un compte
  
  ![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/3d4621dc-9e53-451e-ba50-31530bc275c6)



###  Commands et Events 

- BaseCommand 
La classe BaseCommand est un élément central dans une architecture axée sur les événements et le modèle CQRS. En définissant un modèle de base pour toutes les commandes, elle favorise la cohérence et l'immutabilité des objets de commande. L'annotation @TargetAggregateIdentifier indique l'identifiant de l'agrégat cible, renforçant ainsi la logique de gestion des événements. En résumé, cette classe fournit une structure uniforme pour la gestion des commandes, contribuant à une implémentation efficace du modèle CQRS dans une architecture orientée événements.
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

- CreateAccountCommand

La classe CreateAccountCommand représente une commande spécifique pour créer un compte . Héritant de la classe abstraite BaseCommand, elle inclut l'identifiant, le solde initial et la devise comme paramètres.
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
- DebitAccountCommand
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
- CreditAccountCommand
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

- Commands Controllers
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
- BaseEvent 
La classe abstraite BaseEvent constitue un élément clé dans une architecture basée sur les événements. En tant que modèle de base pour tous les événements, elle inclut un identifiant et adopte le principe d'immutabilité des objets d'événement.
```bash
public abstract class BaseEvent<EventId> {
    @Getter
    private EventId id;

    public BaseEvent(EventId id){
        this.id = id;
    }

}
```
- AccountCreatedEvent
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
- AccountCreditedEvent
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
- AccountDebitedEvent
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

- Base de données
![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/f9aa554d-a976-413a-8080-c319e7c37a8a)


- Test
  
![image](https://github.com/Userkaoutar/Activit-Pratique-N-3---Event-Driven-Architecture/assets/101696114/5c68e4b5-a15c-4dff-bb34-f67af159957e)


- Aggregate

La classe AccountAggregate est annotée avec @Aggregate, signalant son rôle en tant qu'agrégat dans une architecture orientée événements. Elle encapsule les données d'un compte, dont l'identifiant (accountId), le solde, la devise, et le statut du compte (AccountStatus). L'annotation @AggregateIdentifier indique que l'identifiant de l'agrégat est représenté par la propriété accountId, jouant un rôle crucial dans la gestion des événements et le modèle CQRS.
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


La méthode annotée @CommandHandler dans la classe AccountAggregate réagit à la commande CreateAccountCommand émise sur le bus de commande. Cette méthode est invoquée lorsqu'une nouvelle demande de création de compte est reçue. La logique de gestion commence par vérifier si le solde initial spécifié dans la commande est inférieur à zéro. Si tel est le cas, elle lance une exception indiquant que la création du compte est impossible avec un solde initial négatif.
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
- Operation

La classe Operation est une entité JPA qui représente une opération financière associée à un compte. Elle est annotée avec @Entity et comprend des attributs tels que l'identifiant, la date, le montant, le type d'opération, et une référence au compte associé. Les annotations Project Lombok, telles que @Data, @NoArgsConstructor, et @AllArgsConstructor, simplifient la génération de méthodes boilerplate. La clé primaire (id) est automatiquement générée, la date est annotée pour tenir compte uniquement du composant de date, et le type d'opération est stocké en tant que chaîne. La relation Many-to-One avec l'entité Account est établie via l'annotation @ManyToOne et @JoinColumn.
```bash
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
- Account

La classe Account est une entité persistante annotée avec @Entity. Elle modélise un compte financier avec des attributs tels que l'identifiant, la devise, le solde, et le statut du compte. Les annotations de Project Lombok simplifient la génération de code. L'identifiant est spécifié avec @Id, et l'enum AccountStatus est stocké en tant que chaîne. La relation One-to-Many avec l'entité Operation est établie via @OneToMany.
```bash
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

    
