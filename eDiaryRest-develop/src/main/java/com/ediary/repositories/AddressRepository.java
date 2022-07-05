package com.ediary.repositories;

import com.ediary.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByStreetAndPhoneNumber(String name, String phoneNumber);
    Long countAllByUserIsNotNull();
}
