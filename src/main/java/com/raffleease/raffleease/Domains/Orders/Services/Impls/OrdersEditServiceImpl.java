package com.raffleease.raffleease.Domains.Orders.Services.Impls;

import com.raffleease.raffleease.Domains.Carts.Services.ReservationsService;
import com.raffleease.raffleease.Domains.Orders.DTOs.*;
import com.raffleease.raffleease.Domains.Orders.Mappers.OrdersMapper;
import com.raffleease.raffleease.Domains.Orders.Model.Order;
import com.raffleease.raffleease.Domains.Orders.Model.OrderItem;
import com.raffleease.raffleease.Domains.Orders.Services.OrdersService;
import com.raffleease.raffleease.Domains.Orders.Services.OrdersEditService;
import com.raffleease.raffleease.Domains.Payments.Model.Payment;
import com.raffleease.raffleease.Domains.Payments.Model.PaymentStatus;
import com.raffleease.raffleease.Domains.Tickets.Model.Ticket;
import com.raffleease.raffleease.Domains.Tickets.Services.TicketsQueryService;
import com.raffleease.raffleease.Domains.Tickets.Services.TicketsService;
import com.raffleease.raffleease.Exceptions.CustomExceptions.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.raffleease.raffleease.Domains.Orders.Model.OrderStatus.*;
import static com.raffleease.raffleease.Domains.Payments.Model.PaymentStatus.SUCCEEDED;
import static com.raffleease.raffleease.Domains.Tickets.Model.TicketStatus.SOLD;

@RequiredArgsConstructor
@Service
public class OrdersEditServiceImpl implements OrdersEditService {
    private final OrdersService ordersService;
    private final ReservationsService reservationsService;
    private final TicketsService ticketsService;
    private final TicketsQueryService ticketsQueryService;
    private final OrdersMapper mapper;

    @Override
    @Transactional
    public Object completeOrder(Long orderId, OrderComplete orderComplete) {
        Order order = ordersService.findById(orderId);
        if (order.getStatus() != PENDING) {
            throw new BusinessException(String.format("Unsupported status transition from %s to %s", order.getStatus(), COMPLETED));
        }
        updateTicketsStatus(order);
        LocalDateTime now = LocalDateTime.now();
        Payment payment = order.getPayment();
        payment.setStatus(SUCCEEDED);
        payment.setPaymentMethod(orderComplete.paymentMethod());
        payment.setCompletedAt(now);
        order.setStatus(COMPLETED);
        order.setCompletedAt(now);
        return mapper.fromOrder(ordersService.save(order));
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = ordersService.findById(orderId);
        if (order.getStatus() != PENDING) {
            throw new BusinessException(String.format("Unsupported status transition from %s to %s", order.getStatus(), CANCELLED));
        }
        releaseOrderTickets(order);
        LocalDateTime now = LocalDateTime.now();
        Payment payment = order.getPayment();
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setCancelledAt(now);
        order.setStatus(CANCELLED);
        order.setCancelledAt(now);
        return mapper.fromOrder(ordersService.save(order));
    }

    @Override
    public OrderDTO addComment(Long orderId, AddCommentRequest request) {
        Order order = ordersService.findById(orderId);
        order.setComment(request.comment());
        return mapper.fromOrder(ordersService.save(order));
    }

    @Override
    public Order edit(Order order, OrderEdit orderEdit) {
        // TODO: CHeck and fix
        /*
        if (Objects.nonNull(orderEdit.cart())) {
            order.setCart(orderEdit.cart());
        }

         */

        if (Objects.nonNull(orderEdit.payment())) {
            order.setPayment(orderEdit.payment());
        }

        if (Objects.nonNull(orderEdit.customer())) {
            order.setCustomer(orderEdit.customer());
        }

        if (Objects.nonNull(orderEdit.orderDate())) {
            order.setCreatedAt(orderEdit.orderDate());
        }

        return ordersService.save(order);
    }

    private void releaseOrderTickets(Order order) {
        List<Ticket> tickets = getTicketsFromOrder(order);
        reservationsService.release(tickets);
    }

    private void updateTicketsStatus(Order order) {
        List<Ticket> tickets = getTicketsFromOrder(order);
        ticketsService.updateStatus(tickets, SOLD);
    }

    private List<Ticket> getTicketsFromOrder(Order order) {
        List<Long> ticketIds = order.getOrderItems().stream().map(OrderItem::getTicketId).toList();
        return ticketsQueryService.findAllById(ticketIds);
    }
}
