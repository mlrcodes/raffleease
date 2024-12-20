package com.raffleease.raffleease.Domains.Orders.Repository;

import com.raffleease.raffleease.Domains.Orders.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrdersRepository extends JpaRepository<Order, Long> {
}
