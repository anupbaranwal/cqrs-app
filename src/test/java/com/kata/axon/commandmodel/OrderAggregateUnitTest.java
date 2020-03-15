package com.kata.axon.commandmodel;

import com.kata.axon.coreapi.commands.ConfirmOrderCommand;
import com.kata.axon.coreapi.commands.PlaceOrderCommand;
import com.kata.axon.coreapi.commands.ShipOrderCommand;
import com.kata.axon.coreapi.events.OrderConfirmedEvent;
import com.kata.axon.coreapi.events.OrderPlacedEvent;
import com.kata.axon.coreapi.events.OrderShippedEvent;
import com.kata.axon.coreapi.exceptions.UnconfirmedOrderException;
import java.util.UUID;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderAggregateUnitTest {

  private FixtureConfiguration<OrderAggregate> fixture;

  @BeforeEach
  public void setUp() {
    fixture = new AggregateTestFixture<>(OrderAggregate.class);
  }

  @Test
  @DisplayName("should publish order place event if order is placed")
  public void shouldPublishOrderPlaceEventIfOrderIsPlaced() {
    String orderId = UUID.randomUUID().toString();
    String product = "Sofa set";
    fixture.givenNoPriorActivity()
        .when(new PlaceOrderCommand(orderId, product))
        .expectEvents(new OrderPlacedEvent(orderId, product));
  }

  @Test
  @DisplayName("should publish order confirm event if confirm order command is triggered, given the order is already placed")
  public void shouldPublishOrderConfirmEventIfConfirmOrderCommandIsTriggeredGivenTheOrderIsAlreadyPlaced() {
    String orderId = UUID.randomUUID().toString();
    String product = "Dining table";
    fixture.given(new OrderPlacedEvent(orderId, product))
        .when(new ConfirmOrderCommand(orderId))
        .expectEvents(new OrderConfirmedEvent(orderId));
  }

  @Test
  public void givenOrderPlacedEvent_whenShipOrderCommand_thenShouldThrowUnconfirmedOrderException() {
    String orderId = UUID.randomUUID().toString();
    String product = "Mac Airbook";
    fixture.given(new OrderPlacedEvent(orderId, product))
        .when(new ShipOrderCommand(orderId))
        .expectException(UnconfirmedOrderException.class);
  }

  @Test
  public void givenOrderPlacedEventAndOrderConfirmedEvent_whenShipOrderCommand_thenShouldPublishOrderShippedEvent() {
    String orderId = UUID.randomUUID().toString();
    String product = "Speakers";
    fixture.given(new OrderPlacedEvent(orderId, product), new OrderConfirmedEvent(orderId))
        .when(new ShipOrderCommand(orderId))
        .expectEvents(new OrderShippedEvent(orderId));
  }

}