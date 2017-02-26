package edu.shuwang.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.shuwang.form.QuoteForm;
import edu.shuwang.model.Quote;

@RestController
public class QuoteController {
    
	@Autowired
	private QuoteAction quoteAction;
    
    
    @RequestMapping("/api/quote/random")
    public Quote random() {
        return quoteAction.random();
    }
    
    @RequestMapping("/api/quote/list/{authorName}")
    public Quote[] list(@PathVariable("authorName") String authorName) {
        return quoteAction.list(authorName);
    }
    
    @RequestMapping(value = "/api/quote", method = RequestMethod.POST)
    public void saveQuote(@RequestBody QuoteForm quoteForm) {
    	quoteAction.saveQuote(quoteForm);
    }

}
