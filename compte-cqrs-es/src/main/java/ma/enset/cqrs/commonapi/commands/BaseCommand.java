package ma.enset.cqrs.commonapi.commands;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Getter
public abstract  class BaseCommand<T>{
    @TargetAggregateIdentifier // c'est identifier de aggregate dans la quel on va effectuer la commande
    private T id;
    public BaseCommand(T id){
        this.id=id;
    }

}
