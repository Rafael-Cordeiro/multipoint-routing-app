package dev.rafaelcordeiro.logisticsroutingapp.core.facade;

import dev.rafaelcordeiro.logisticsroutingapp.core.dao.AddressDAO;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AddressFacade {

    @Autowired
    AddressDAO addressDAO;

    public Address getAddressById(String id) {
        return addressDAO.getAddressById(id);
    }

    public List<Address> searchAddressesByMatchingName(String name) {
        return addressDAO.searchAddressesByMatchingName(name);
    }

}
