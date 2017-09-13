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

import net.elastic.spring.dto.QueryIndexer;

@RestController
public class QueryIndexerRestController {

	@PostMapping(value = "/esearch")
	public ResponseEntity createIndex(@RequestBody QueryIndexer computeIndex) {
	

		ElastiSearchService elastiSearchService = null;
		elastiSearchService = ElastiSearchService.getInstance();

		elastiSearchService.IndexComputeController (computeIndex);

		return new ResponseEntity(computeIndex, HttpStatus.OK);
	}

	@GetMapping("/esearch/{index}/{varstr}")
	public ResponseEntity getQuery (@PathVariable("varstr") String str, @PathVariable("index") String index) {

		ElastiSearchService elastiSearchService = null;
		elastiSearchService = ElastiSearchService.getInstance();

		elastiSearchService.IndexComputeControllerSearch (index, str);
		return new ResponseEntity (HttpStatus.OK);
	}
}
