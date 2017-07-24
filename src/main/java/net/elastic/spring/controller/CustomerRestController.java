package net.elastic.spring.controller;

import java.util.List;

import net.elastic.spring.recipe.ElastiSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.elastic.spring.dao.CustomerDAO;
import net.elastic.spring.model.Customer;

@RestController
public class CustomerRestController {

	
	@Autowired
	private CustomerDAO customerDAO;

	@PostMapping(value = "/customers")
	public ResponseEntity createCustomer(@RequestBody Customer customer) {
	//public void createCustomer(@RequestBody Customer customer) {
	

		ElastiSearchService elastiSearchService = null;
		elastiSearchService = ElastiSearchService.getInstance();

		customerDAO.create(customer);
		System.out.println ("gaurav in customer post: " + customer);
		System.out.println ("gaurav in customer post: " + customer.getId());
		System.out.println ("gaurav in customer post: " + customer.getFirstName());

		elastiSearchService.IndexComputeController (customer.getFirstName());

		return new ResponseEntity(customer, HttpStatus.OK);
	}

	@GetMapping("/customers/{varstr}")
	public ResponseEntity getCustomer (@PathVariable("varstr") String str) {

		ElastiSearchService elastiSearchService = null;
		elastiSearchService = ElastiSearchService.getInstance();

		System.out.println ("elastic search get reqeust id: " + str);
		elastiSearchService.IndexComputeControllerSearch (str);
		return new ResponseEntity (HttpStatus.OK);
	}
}
