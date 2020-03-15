package com.kata.axon.commandmodel;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import com.kata.axon.coreapi.commands.ConfirmOrderCommand;
import com.kata.axon.coreapi.commands.PlaceOrderCommand;
import com.kata.axon.coreapi.commands.ShipOrderCommand;
import com.kata.axon.coreapi.events.OrderConfirmedEvent;
import com.kata.axon.coreapi.events.OrderPlacedEvent;
import com.kata.axon.coreapi.events.OrderShippedEvent;
import com.kata.axon.coreapi.exceptions.UnconfirmedOrderException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class OrderAggregate {

  @AggregateIdentifier
  private String orderId;
  private boolean orderConfirmed;

  @CommandHandler
  public OrderAggregate(PlaceOrderCommand command) {
    apply(new OrderPlacedEvent(command.getOrderId(), command.getProduct()));
  }

  protected OrderAggregate() {
    // Required by Axon to build a default Aggregate prior to Event Sourcing
  }

  @CommandHandler
  public void handle(ConfirmOrderCommand command) {
    apply(new OrderConfirmedEvent(orderId));
  }

  @CommandHandler
  public void handle(ShipOrderCommand command) {
    if (!orderConfirmed) {
      throw new UnconfirmedOrderException();
    }

    apply(new OrderShippedEvent(orderId));
  }

  @EventSourcingHandler
  public void on(OrderPlacedEvent event) {
    this.orderId = event.getOrderId();
    this.orderConfirmed = false;
  }

  @EventSourcingHandler
  public void on(OrderConfirmedEvent event) {
    this.orderConfirmed = true;
  }

}