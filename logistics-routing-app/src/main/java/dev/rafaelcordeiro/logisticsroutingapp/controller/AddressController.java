package dev.rafaelcordeiro.logisticsroutingapp.controller;

import dev.rafaelcordeiro.logisticsroutingapp.core.facade.AddressFacade;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.APIResponse;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("address")
@CrossOrigin(origins="*")
public class AddressController {

    @Autowired
    AddressFacade addressFacade;

    @GetMapping
    public @ResponseBody ResponseEntity<APIResponse<Address>> searchAddressesByMatchingName(@RequestParam String name) {
        var addresses = addressFacade.searchAddressesByMatchingName(name);
        var response = new APIResponse<Address>();
        response.setEntities(addresses);
        return ResponseEntity.ok(response);
    }

}
